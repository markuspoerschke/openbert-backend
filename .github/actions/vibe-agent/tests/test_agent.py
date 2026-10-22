"""Regression tests with a local Git remote, HTTP API, and deterministic Vibe CLI.

Native gh commands use fixture responses; gh api runs the real CLI against a local server.
No GitHub credentials or Mistral requests are used. Run with:
python3 -m unittest discover -s .github/actions/vibe-agent/tests -v
"""
import http.server
import json
import os
from pathlib import Path
import shlex
import shutil
import subprocess
import tempfile
import threading
import unittest

AGENT = Path(__file__).resolve().parents[1]
REAL_GH = shutil.which("gh")


class AgentTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.repo = self.root / 'repo'
        self.repo.mkdir()
        self.bin = self.root / 'bin'
        self.bin.mkdir()
        self.requests = []
        self.routes = {}
        test = self

        class Handler(http.server.BaseHTTPRequestHandler):
            def do_GET(self):
                self.respond('GET')

            def do_POST(self):
                self.respond('POST')

            def respond(self, method):
                body = self.rfile.read(int(self.headers.get('Content-Length', '0')))
                test.requests.append((method, self.path, json.loads(body) if body else None))
                status, data = test.routes.get((method, self.path), (404, {'message': self.path}))
                self.send_response(status)
                next_page = self.path + '&page=2'
                if (method, next_page) in test.routes:
                    self.send_header('Link', f'<http://127.0.0.1:{test.server.server_port}{next_page}>; rel="next"')
                self.end_headers()
                if status != 204:
                    self.wfile.write(json.dumps(data).encode())

            def log_message(self, *_):
                pass

        self.server = http.server.ThreadingHTTPServer(('127.0.0.1', 0), Handler)
        threading.Thread(target=self.server.serve_forever, daemon=True).start()
        self.addCleanup(self.server.server_close)
        self.addCleanup(self.server.shutdown)
        self.env = dict(os.environ, GITHUB_REPOSITORY='owner/repo', GITHUB_TOKEN='test-token',
                        VIBE_API_KEY='test-api-key', VIBE_TIMEOUT_MINUTES='1', MAX_RETRIES='3',
                        GH_TOKEN='', GH_TEST_API_URL=f'http://127.0.0.1:{self.server.server_port}',
                        GH_TEST_REAL_CLI=REAL_GH, GH_TEST_CALLS=str(self.root / 'gh-calls.jsonl'),
                        LOGFILE=str(self.root / 'agent.log'),
                        PATH=f'{self.bin}:{os.environ["PATH"]}',
                        VIBE_CAPTURE=str(self.root / 'vibe.json'),
                        DRY_RUN='false')
        self.env.pop('VIBE_COMMENT_KIND', None)
        self.env.pop('GITHUB_EVENT_NAME', None)
        shutil.copyfile(AGENT / 'tests/fake_gh.py', self.bin / 'gh')
        (self.bin / 'gh').chmod(0o755)

    def bash(self, code, *, ok=True, **env):
        result = subprocess.run(['bash', '-c', f'source {shlex.quote(str(AGENT / "entrypoint.sh"))}\n{code}'],
                                cwd=self.repo, env=dict(self.env, **env), text=True,
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        if ok:
            self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        else:
            self.assertNotEqual(result.returncode, 0, result.stdout + result.stderr)
        return result

    def git(self, *args, cwd=None):
        return subprocess.check_output(['git', *args], cwd=cwd or self.repo,
                                       stderr=subprocess.DEVNULL, text=True).strip()

    def init_git(self, branch='main'):
        self.git('init', '-b', branch)
        self.git('config', 'user.email', 'test@example.com')
        self.git('config', 'user.name', 'Test')
        (self.repo / 'app.txt').write_text('original')
        self.git('add', '.')
        self.git('commit', '-m', 'Initial')

    def remote(self):
        self.origin = self.root / 'origin.git'
        self.git('init', '--bare', str(self.origin))
        self.git('remote', 'add', 'origin', str(self.origin))
        self.git('push', 'origin', 'main')

    def route(self, method, path, data=None, status=200):
        self.routes[method, '/repos/owner/repo' + path] = status, data

    def mock_vibe(self):
        path = self.bin / 'vibe'
        path.write_text('''#!/usr/bin/env python3
import json, os, pathlib, sys, tomllib
args = sys.argv[1:]
assert args[0] == '--prompt-file', args
assert args[2:] == ['--auto-approve', '--trust', '--time-limit', '60'], args
assert os.environ['MISTRAL_API_KEY'] == 'test-api-key'
config = tomllib.loads((pathlib.Path(os.environ['VIBE_HOME']) / 'config.toml').read_text())
assert config['active_model'] == 'vibe-agent'
assert config['models'][0]['name'] == 'mistral-large-latest'
prompt = pathlib.Path(args[1]).read_text()
assert len(prompt) > 20
assert not pathlib.Path('.git').exists()
pathlib.Path(os.environ['VIBE_CAPTURE']).write_text(json.dumps({'args': args, 'prompt': prompt}))
pathlib.Path('app.txt').write_text('fixed by Vibe')
pathlib.Path('new.txt').write_text('new file')
pathlib.Path('delete.txt').unlink(missing_ok=True)
sys.exit(int(os.environ.get('VIBE_EXIT', '0')))
''')
        path.chmod(0o755)

    def test_entrypoint_dispatches_from_arguments_and_environment(self):
        self.init_git()
        self.route('GET', '', {'name': 'repository'})
        issue = dict(number=42, title='Preview implementation', body='The issue body',
                     labels=[{'name': 'vibe'}], created_at='2020-01-01', updated_at='2020-01-01')
        self.route('GET', '/issues/42', issue)
        self.route('GET', '/pulls?state=open&per_page=100', [])
        for args, env in [(['manual', '42'], {}), ([], {'VIBE_AGENT_MODE': 'manual', 'VIBE_ISSUE_NUMBER': '42'})]:
            result = subprocess.run(['bash', str(AGENT / 'entrypoint.sh'), *args], cwd=self.repo,
                                    env=dict(self.env, DRY_RUN='true', **env), text=True,
                                    stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
            self.assertIn('[DRY RUN] Would implement #42', result.stderr)

    def test_source_is_inert_and_exposes_orchestration(self):
        result = self.bash('declare -F implement_issue run_ci_monitor > /dev/null')
        self.assertEqual(result.stdout, '')
        self.assertEqual(self.requests, [])

    def test_cli_credentials_use_repository_access_status(self):
        self.route('GET', '', {'name': 'valid-repository'})
        self.bash('validate_credentials')
        self.route('GET', '', {'message': 'rate limit'}, status=401)
        self.bash('validate_credentials', ok=False)
        self.assertEqual([x[:2] for x in self.requests], [('GET', '/repos/owner/repo')] * 2)

    def gh_calls(self):
        return [json.loads(line) for line in (self.root / 'gh-calls.jsonl').read_text().splitlines()]

    def test_explicit_cli_token_takes_precedence(self):
        self.route('GET', '', {'name': 'repository'})
        self.bash('validate_credentials', GH_TOKEN='alternate-token', TEST_GH_TOKEN='alternate-token',
                  GITHUB_TOKEN='')
        self.assertEqual(self.gh_calls()[0]['args'], ['repo', 'view', 'owner/repo', '--json', 'nameWithOwner'])

    def test_draft_pr_uses_cli_and_preserves_multiline_body(self):
        self.init_git()
        self.git('checkout', '-b', 'vibe/42-fix')
        issue_body = 'First line\n\n@literal `code` $(touch injected)'
        self.route('GET', '/issues/42', dict(number=42, title='Fix "quoted" issue', body=issue_body,
                   labels=[{'name': 'vibe'}], created_at='2020-01-01', updated_at='2020-01-01'))
        self.route('POST', '/pulls', {'html_url': 'https://github.com/owner/repo/pull/7'}, 201)
        self.route('POST', '/issues/42/comments', {}, 201)
        result = self.bash('create_draft_pr vibe/42-fix 42')
        self.assertEqual(result.stdout, 'https://github.com/owner/repo/pull/7\n')
        payload = next(body for method, path, body in self.requests if method == 'POST' and path.endswith('/pulls'))
        self.assertTrue(payload['draft'])
        self.assertEqual(payload['head'], 'vibe/42-fix')
        self.assertEqual(payload['base'], 'main')
        self.assertEqual(payload['title'], 'vibe: Fix "quoted" issue')
        self.assertIn(issue_body, payload['body'])
        self.assertFalse((self.repo / 'injected').exists())
        calls = self.gh_calls()
        creation = next(c for c in calls if c['args'][:2] == ['pr', 'create'])
        self.assertIn('--body-file', creation['args'])
        self.assertEqual(creation['stdin'], payload['body'])
        self.assertEqual(calls[-1]['args'][:2], ['issue', 'comment'])

    def test_dispatch_http_errors_propagate(self):
        self.route('POST', '/actions/workflows/test.yml/dispatches', {'message': 'forbidden'}, 403)
        result = self.bash('trigger_ci_monitoring vibe/42-fix', ok=False)
        self.assertNotIn('Build dispatched', result.stderr)

    def test_issue_selection_preserves_json_strings_and_skips_active_pr(self):
        issues = [dict(number=3, title='A "quoted" title\nwith spaces', created_at='2020-01-01', labels=[]),
                  dict(number=4, title='Second issue with spaces', created_at='2021-01-01', labels=[]),
                  dict(number=5, title='Newer issue', created_at='2022-01-01', labels=[])]
        self.route('GET', '/issues?state=open&labels=vibe&per_page=100', issues)
        self.route('GET', '/pulls?state=open&per_page=100', [{'head': {'ref': 'vibe/3-already-open'}}])
        self.assertEqual(self.bash('select_issue').stdout.strip(), '4')
        self.route('GET', '/issues?state=open&labels=vibe&per_page=100', [])
        self.assertEqual(self.bash('select_issue').stdout, '')

    def test_issue_selection_stops_when_pr_lookup_fails(self):
        self.route('GET', '/issues?state=open&labels=vibe&per_page=100', [
            dict(number=42, title='Fix something', created_at='2020-01-01', labels=[])])
        self.route('GET', '/pulls?state=open&per_page=100', {'message': 'forbidden'}, 403)
        self.bash('select_issue', ok=False)
        self.bash('run_scheduled_mode', ok=False)
        self.bash('issue_has_vibe_label() { return 0; }; run_manual_mode 42', ok=False)

    def test_cli_api_pagination(self):
        self.route('GET', '/pulls?state=open&per_page=100', list(range(100)))
        self.route('GET', '/pulls?state=open&per_page=100&page=2', [100])
        self.assertEqual(self.bash('list_open_prs | jq length').stdout.strip(), '101')
        self.route('GET', '/pulls?state=open&per_page=100&page=2', {'message': 'forbidden'}, 403)
        self.bash('list_open_prs', ok=False)

    def test_prompt_markdown_does_not_execute_commands(self):
        (self.repo / 'gradlew').write_text('#!/bin/sh\ntouch unwanted-build\n')
        (self.repo / 'gradlew').chmod(0o755)
        output = self.bash("build_vibe_prompt 42 'Fix `touch injected`' 'Body with spaces'").stdout
        self.assertIn('`./gradlew build --no-daemon`', output)
        self.assertIn('```', output)
        self.assertIn('`touch injected`', output)
        self.assertFalse((self.repo / 'unwanted-build').exists())
        self.assertFalse((self.repo / 'injected').exists())

    def test_all_vibe_paths_use_supported_headless_contract_and_sync_changes(self):
        self.init_git('vibe/42-fix')
        self.mock_vibe()
        for command in [
            'get_issue_details() { echo \'{"title":"Fix something", "body":"Issue description"}\'; }; run_vibe_cli 42 vibe/42-fix',
            "process_comment_with_vibe 'Please fix this' vibe/42-fix 42",
            'fix_ci_failure vibe/42-fix \'{"logs":"Compile error"}\'',
        ]:
            with self.subTest(command=command):
                (self.repo / 'app.txt').write_text('original')
                (self.repo / 'delete.txt').write_text('remove this')
                (self.repo / '.gradle').mkdir(exist_ok=True)
                (self.repo / '.gradle/cache').write_text('preserved')
                self.bash(command)
                self.assertEqual((self.repo / 'app.txt').read_text(), 'fixed by Vibe')
                self.assertTrue((self.repo / 'new.txt').exists())
                self.assertFalse((self.repo / 'delete.txt').exists())
                self.assertTrue((self.repo / '.git').is_dir())
                self.assertEqual((self.repo / '.gradle/cache').read_text(), 'preserved')
                self.assertNotIn('test-api-key', (self.root / 'agent.log').read_text())

    def test_failed_vibe_does_not_apply_partial_changes_even_in_conditional(self):
        self.init_git()
        self.mock_vibe()
        self.bash("if execute_vibe_prompt 'Please implement the requested changes' /tmp/vibe-test.log; then exit 99; fi",
                  VIBE_EXIT='2')
        self.assertTrue((self.root / 'vibe.json').exists())
        self.assertEqual((self.repo / 'app.txt').read_text(), 'original')
        self.assertFalse((self.repo / 'new.txt').exists())

    def test_implementation_commits_and_pushes_before_creating_pr(self):
        self.init_git()
        self.remote()
        self.bash('''
get_issue_details() { echo '{"title":"Fix issue with spaces"}'; }
run_vibe_cli() { echo implementation > app.txt; }
run_tests() { return 1; }
create_draft_pr() {
    [[ $(git --git-dir="$TEST_ORIGIN" show "refs/heads/$1:app.txt") == implementation ]] || return 1
    echo https://github.com/owner/repo/pull/7
}
trigger_ci_monitoring() { echo "$1" > dispatched; }
implement_issue 42
''', TEST_ORIGIN=str(self.origin))
        self.assertEqual((self.repo / 'dispatched').read_text().strip(), 'vibe/42-fix-issue-with-spaces')
        message = self.git('log', '-1', '--format=%B')
        self.assertIn('\n\nImplements #42\n', message)
        self.assertNotIn('\\n', message)

    def test_empty_implementation_does_not_create_pr(self):
        self.init_git()
        self.remote()
        self.bash('''
get_issue_details() { echo '{"title":"No change"}'; }
run_vibe_cli() { :; }
run_tests() { :; }
create_draft_pr() { touch unexpected-pr; }
implement_issue 42
''', ok=False)
        self.assertFalse((self.repo / 'unexpected-pr').exists())

    def test_manual_and_scheduled_modes_use_loaded_implementation(self):
        for mode in ['manual', 'scheduled']:
            with self.subTest(mode=mode):
                output = self.bash(f'''
has_active_vibe_pr() {{ return 1; }}
has_vibe_pr_for_issue() {{ return 1; }}
issue_has_vibe_label() {{ return 0; }}
select_issue() {{ echo 42; }}
implement_issue() {{ printf 'implemented:%s' "$1"; }}
run_{mode}_mode
''').stdout
                self.assertEqual(output, 'implemented:42')

    def test_review_uses_separate_lookup_and_reply_routes(self):
        self.route('GET', '/pulls/comments/22', {'id': 22, 'in_reply_to_id': 11,
                   'body': 'Please fix it', 'user': {'login': 'reviewer'}})
        self.route('GET', '/pulls/7', {'state': 'open', 'head': {'ref': 'vibe/42-fix', 'repo': {'full_name': 'owner/repo'}}})
        self.route('POST', '/pulls/7/comments/11/replies', {}, 201)
        self.init_git()
        self.bash('''
checkout_pr_branch() { :; }
process_comment_with_vibe() { :; }
handle_review_comment 7 22
''')
        self.assertEqual(self.requests[-1][:2], ('POST', '/repos/owner/repo/pulls/7/comments/11/replies'))
        self.assertEqual(set(self.requests[-1][2]), {'body'})
        message = '@literal\n$(touch injected)\n`code`'
        self.bash('add_comment_response 7 11 "$REVIEW_BODY"', REVIEW_BODY=message)
        self.assertEqual(self.requests[-1][2], {'body': message})
        self.assertFalse((self.repo / 'injected').exists())
        self.route('GET', '/issues/comments/33', {'body': 'Conversation feedback'})
        self.route('POST', '/issues/7/comments', {}, 201)
        self.bash('get_comment_details 7 33; add_comment_response 7 33 "Review response"', VIBE_COMMENT_KIND='issue_comment')
        self.assertEqual(self.requests[-2][:2], ('GET', '/repos/owner/repo/issues/comments/33'))
        self.assertEqual(self.requests[-1][:2], ('POST', '/repos/owner/repo/issues/7/comments'))

    def test_fork_pr_does_not_yield_a_branch(self):
        self.route('GET', '/pulls/7', {'state': 'open', 'head': {'ref': 'vibe/42-fix', 'repo': {'full_name': 'outsider/repo'}}})
        self.assertEqual(self.bash('get_pr_branch 7').stdout, '')

    def ci_routes(self, sha):
        self.route('GET', '/actions/runs/123', {
            'head_repository': {'full_name': 'owner/repo'}, 'head_branch': 'vibe/42-fix',
            'head_sha': sha, 'path': '.github/workflows/test.yml', 'conclusion': 'failure'})
        self.route('GET', '/pulls?state=open&head=owner%3Avibe%2F42-fix', [{'number': 7}])
        self.route('GET', '/pulls/7', {'state': 'open', 'head': {'ref': 'vibe/42-fix', 'repo': {'full_name': 'owner/repo'}}})
        self.route('GET', '/issues/7/comments?per_page=100', [])

    def test_ci_repairs_the_failing_branch_and_dispatches_next_build(self):
        self.init_git()
        self.remote()
        main_sha = self.git('rev-parse', 'HEAD')
        self.git('checkout', '-b', 'vibe/42-fix')
        (self.repo / 'app.txt').write_text('branch version')
        self.git('commit', '-am', 'Feature')
        self.git('push', 'origin', 'vibe/42-fix')
        self.ci_routes(self.git('rev-parse', 'HEAD'))
        self.git('checkout', 'main')
        self.mock_vibe()
        self.route('GET', '/actions/runs/123/jobs?filter=latest&per_page=100', {'jobs': [{'name': 'test', 'conclusion': 'failure', 'steps': []}]})
        self.route('POST', '/issues/7/comments', {}, 201)
        self.route('POST', '/actions/workflows/test.yml/dispatches', None, 204)
        self.bash('run_ci_monitor', VIBE_MONITOR_BRANCH='vibe/42-fix', VIBE_RUN_ID='123')
        self.assertEqual(self.git('branch', '--show-current'), 'vibe/42-fix')
        self.assertEqual(self.git('--git-dir=' + str(self.origin), 'show', 'vibe/42-fix:app.txt'), 'fixed by Vibe')
        self.assertEqual(self.git('--git-dir=' + str(self.origin), 'rev-parse', 'main'), main_sha)
        self.assertIn('compilation failed', json.loads((self.root / 'vibe.json').read_text())['prompt'])
        dispatches = [r for r in self.requests if r[1].endswith('/dispatches')]
        self.assertEqual(dispatches[0][2], {'ref': 'vibe/42-fix'})
        attempts = [r for r in self.requests if r[0] == 'POST' and 'vibe-ci-attempt:123' in str(r[2])]
        self.assertEqual(len(attempts), 1)
        # A later runner reads persisted attempt markers and skips duplicates.
        self.route('GET', '/issues/7/comments?per_page=100', [
            {'user': {'login': 'github-actions[bot]'}, 'body': attempts[0][2]['body']}])
        self.assertIn('already handled', self.bash('run_ci_monitor', VIBE_MONITOR_BRANCH='vibe/42-fix', VIBE_RUN_ID='123').stderr)
        self.assertEqual(self.bash('get_retry_count 7').stdout.strip(), '1')

    def test_ci_rejects_stale_fork_and_over_limit_runs(self):
        self.init_git()
        self.remote()
        self.git('push', 'origin', 'HEAD:refs/heads/vibe/42-fix')
        self.ci_routes('0' * 40)
        options = dict(VIBE_MONITOR_BRANCH='vibe/42-fix', VIBE_RUN_ID='123')
        self.assertIn('stale', self.bash('run_ci_monitor', **options).stderr)
        self.route('GET', '/issues/7/comments?per_page=100', [
            {'user': {'login': 'github-actions[bot]'}, 'body': '<!-- vibe-ci-attempt:122 -->\nPrior attempt'}])
        self.route('POST', '/issues/7/comments', {}, 201)
        self.assertIn('Retry limit', self.bash('run_ci_monitor', MAX_RETRIES='1', **options).stderr)
        run = self.routes['GET', '/repos/owner/repo/actions/runs/123'][1]
        run['head_repository']['full_name'] = 'outsider/repo'
        self.assertIn('same-repository', self.bash('run_ci_monitor', **options).stderr)
        self.assertEqual(self.git('branch', '--show-current'), 'main')

    def test_branch_injection_is_rejected_without_evaluation(self):
        self.bash('run_ci_monitor', VIBE_MONITOR_BRANCH='vibe/42-$(touch injected)', VIBE_RUN_ID='123')
        self.assertFalse((self.repo / 'injected').exists())
        self.assertEqual(self.requests, [])

    def test_dry_run_does_not_generate_or_publish_changes(self):
        self.bash('''
get_issue_details() { echo '{"title":"Preview"}'; }
implement_issue 42
execute_vibe_prompt 'Please make a change' /tmp/vibe-test.log
''', DRY_RUN='true')
        self.assertEqual(list(self.repo.iterdir()), [])
        self.assertEqual(self.requests, [])


if __name__ == '__main__':
    unittest.main()
