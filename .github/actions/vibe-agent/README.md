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

Vibe works in a temporary copy without Git metadata. The caller applies changes,
commits them, pushes the branch, then creates a draft PR or posts a response.
Build is explicitly dispatched because pushes made with `GITHUB_TOKEN` do not
trigger ordinary push/PR workflows.

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

## Validation

On Linux with Bash, Python 3.12+, Git, curl, jq, and rsync:

```bash
python3 -m unittest discover -s .github/actions/vibe-agent/tests -v
shellcheck -e SC1091 .github/actions/vibe-agent/*.sh
actionlint .github/workflows/vibe-agent-*.yml .github/workflows/test.yml
```

Tests use a local HTTP server, temporary Git repositories and a fake Vibe CLI.
They do not call Mistral or write to GitHub. The Build workflow runs the regression
suite alongside the application build.
