# Mistral Vibe Agent

A GitHub Actions agent that selects issues labeled `vibe`, implements them with
Mistral Vibe, and publishes draft PRs. It can also address review feedback and
attempt bounded repairs of failed builds. It never merges PRs.

See [the setup guide](../../../docs/vibe-agent-setup.md) for configuration.

## Execution

All four workflows call this composite action, which invokes `entrypoint.sh`.
The entrypoint can also be sourced to access its functions without starting a run.

| Mode | Trigger | Behavior |
| --- | --- | --- |
| `scheduled` | Hourly or manual dispatch | Select the oldest eligible issue when no Vibe PR is open |
| `manual` | Manual dispatch | Implement a specified labeled issue, or select one automatically |
| `review` | PR conversation or inline comment | Apply actionable feedback and reply in the appropriate thread |
| `ci-monitor` | Failed Build completion | Repair the current failing Vibe branch, within the retry limit |

Implementation, review, and repair all use Vibe 3.0.1 with `--prompt-file`,
`--auto-approve`, and an execution timeout. The repository secret `VIBE_API_KEY`
is passed to the CLI as `MISTRAL_API_KEY`; `VIBE_MODEL` is written to a temporary
Vibe configuration. API keys are never passed as command arguments.

Vibe runs with `--output streaming`. Assistant and tool messages appear in the
Actions log as they arrive, and `tee` retains the stream in `/tmp/vibe-*.log`.
The pipeline preserves failures from both Vibe and log capture.

Before implementation starts, the issue receives an `in progress` label and a
comment linking to the Actions run. The label is created if needed without
changing an existing label. Exit cleanup removes it after completion, failure,
or catchable cancellation, and reports failures on the issue. A completed draft
PR is linked by the existing delivery comment. Dry runs do not update issues.

Vibe works in a temporary copy without Git metadata. The caller applies changes,
commits them, pushes the branch, then creates a draft PR or posts a response.
Build is explicitly dispatched because pushes made with `GITHUB_TOKEN` do not
trigger ordinary push/PR workflows.

GitHub operations use the runner's preinstalled `gh`: issue/PR views, draft PR
creation, comments, workflow dispatch, and build logs use native commands.
Inline review endpoints, run provenance, and collection listings use `gh api`;
`--paginate` exhausts collections without the list commands' default result limit.
`GH_TOKEN` takes precedence, with the Actions `GITHUB_TOKEN` as its fallback.

## Configuration

| Environment variable | Default |
| --- | --- |
| `VIBE_API_KEY` | Required |
| `GITHUB_TOKEN` | Required; provided by the caller |
| `GITHUB_REPOSITORY` | Provided by Actions |
| `VIBE_MODEL` | `mistral-large-latest` |
| `MAX_RETRIES` | `3` repair attempts per PR |
| `VIBE_TIMEOUT_MINUTES` | `30` per Vibe execution |
| `VIBE_DEBUG` | `false` |
| `DRY_RUN` | `false`; selection preview only, without invoking Vibe or publishing |

The caller must check out trusted agent scripts before using the composite action
and set workflow concurrency to `vibe-agent-${{ github.repository }}` with
`cancel-in-progress: false`. All supplied workflows share this group; local lock
files cannot coordinate separate runners. Actions retains at most one pending
run per group, so a superseded pending review event may need to be retriggered.

CI repair verifies the run repository, workflow, branch, and commit before
checking out the failing branch. It skips stale runs. Bot-authored attempt
comments persist retry counts across runners and prevent duplicate processing of
the same Build run. Fork PRs and non-Vibe branches are skipped. Review triggers
accept repository owners, members, collaborators, and the configured Codex review bot.
Review and CI workflows check that the local action exists after checking out the
trusted default branch. Events from before the agent is installed there skip with
a notice, rather than attempting to load a missing local action.

## Validation

On Linux with Bash, Python 3.12+, Git, GitHub CLI (`gh`), jq, and rsync:

```bash
python3 -m unittest discover -s .github/actions/vibe-agent/tests -v
shellcheck -e SC1091 .github/actions/vibe-agent/*.sh
actionlint .github/workflows/vibe-agent-*.yml .github/workflows/test.yml
```

Tests use a local HTTP server, temporary Git repositories and a fake Vibe CLI.
Native GitHub CLI commands use fixtures; `gh api` uses the installed CLI against
that local server, including pagination and HTTP errors.
They do not call Mistral or write to GitHub. The Build workflow runs the regression
suite alongside the application build.
