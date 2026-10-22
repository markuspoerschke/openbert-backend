#!/usr/bin/env python3
"""Fixture adapter for native gh commands; gh api uses the real CLI locally.

Only test credentials and the test HTTP server are used. Reject unknown flags,
record command arguments, and expose the native CLI's JSON field names.
"""
import argparse
import json
import os
from pathlib import Path
import subprocess
import sys
import urllib.error
import urllib.request

args = sys.argv[1:]
assert os.environ['GH_TOKEN'] == os.environ.get('TEST_GH_TOKEN', 'test-token')
body = sys.stdin.read() if '--body-file' in args else None
with Path(os.environ['GH_TEST_CALLS']).open('a') as output:
    output.write(json.dumps({'args': args, 'stdin': body}) + '\n')

if args[0] == 'api':
    parser = argparse.ArgumentParser()
    parser.add_argument('endpoint')
    parser.add_argument('--paginate', action='store_true')
    parser.add_argument('--method')
    parser.add_argument('--raw-field', action='append')
    options = parser.parse_args(args[1:])
    endpoint = options.endpoint
    assert endpoint.startswith('repos/owner/repo/')
    args[args.index(endpoint)] = os.environ['GH_TEST_API_URL'] + '/' + endpoint
    os.execv(os.environ['GH_TEST_REAL_CLI'], ['gh', *args])

command = tuple(args[:2])
parser = argparse.ArgumentParser()
parser.add_argument('--json')
parser.add_argument('--jq')
if command != ('repo', 'view'):
    parser.add_argument('--repo', required=True)
if command == ('pr', 'create'):
    parser.add_argument('--head', required=True)
    parser.add_argument('--base', required=True)
    parser.add_argument('--draft', action='store_true')
    parser.add_argument('--title', required=True)
    parser.add_argument('--body-file', required=True)
elif command in [('issue', 'comment'), ('pr', 'comment')]:
    parser.add_argument('--body-file', required=True)
elif command == ('workflow', 'run'):
    parser.add_argument('--ref', required=True)
elif command == ('run', 'view'):
    parser.add_argument('--log-failed', action='store_true')
if command != ('pr', 'create'):
    parser.add_argument('target')
options = parser.parse_args(args[2:])
if command == ('repo', 'view'):
    assert options.target == 'owner/repo'
else:
    assert options.repo == 'owner/repo'


def request(path, data=None):
    url = os.environ['GH_TEST_API_URL'] + '/repos/owner/repo' + path
    payload = json.dumps(data).encode() if data is not None else None
    try:
        with urllib.request.urlopen(urllib.request.Request(url, data=payload)) as response:
            content = response.read()
            return json.loads(content) if content else None
    except urllib.error.HTTPError as error:
        print(f'gh: {error.code} {error.reason}', file=sys.stderr)
        sys.exit(1)


def emit(data):
    if options.json:
        fields = options.json.split(',')
        data = {key: data[key] for key in fields}
    if options.jq:
        result = subprocess.run(['jq', '-cr', options.jq], input=json.dumps(data), text=True,
                                stdout=sys.stdout)
        sys.exit(result.returncode)
    print(json.dumps(data))


if command == ('repo', 'view'):
    data = request('')
    emit({'nameWithOwner': 'owner/' + data['name']})
elif command == ('issue', 'view'):
    data = request('/issues/' + options.target)
    emit(dict(data, createdAt=data.get('created_at'), updatedAt=data.get('updated_at')))
elif command == ('pr', 'view'):
    data = request('/pulls/' + options.target)
    emit({'state': data['state'].upper(), 'headRefName': data['head']['ref'],
          'isCrossRepository': data['head']['repo']['full_name'] != 'owner/repo'})
elif command == ('pr', 'create'):
    assert options.body_file == '-'
    data = request('/pulls', {'head': options.head, 'base': options.base,
                              'draft': options.draft, 'title': options.title, 'body': body})
    print(data['html_url'])
elif command in [('issue', 'comment'), ('pr', 'comment')]:
    assert options.body_file == '-'
    request(f'/issues/{options.target}/comments', {'body': body})
    print('https://github.com/owner/repo/issues/' + options.target + '#test-comment')
elif command == ('workflow', 'run'):
    assert options.target == 'test.yml'
    request('/actions/workflows/test.yml/dispatches', {'ref': options.ref})
elif command == ('run', 'view'):
    if options.log_failed:
        print('app.txt: compilation failed')
    else:
        assert options.json == 'jobs'
        emit(request(f'/actions/runs/{options.target}/jobs?filter=latest&per_page=100'))
else:
    raise AssertionError(f'Unexpected gh command: {command}')
