# Mistral Vibe Agent

A GitHub Actions agent that selects issues labeled `vibe`, implements them with
Mistral Vibe, and publishes draft PRs. It can also attempt bounded repairs of
failed builds. It never merges PRs.

See [the setup guide](../../../docs/vibe-agent-setup.md) for configuration.

## Execution

All three workflows call this composite action, which invokes `entrypoint.sh`.
The entrypoint can also be sourced to access its functions without starting a run.

| Mode | Trigger | Behavior |
| --- | --- | --- |
| `scheduled` | Every three hours or manual dispatch | Select the oldest eligible issue when no Vibe PR is open |
| `manual` | Manual dispatch | Implement a specified labeled issue, or select one automatically |
| `ci-monitor` | Failed Build completion | Repair the current failing Vibe branch, within the retry limit |

Automatic review handling is permanently disabled. The `vibe-agent-review.yml`
workflow has been removed and must not be restored.

Implementation and repair use Vibe 3.0.1 with `--prompt-file`,
`--auto-approve`, and an execution timeout. The repository secret `VIBE_API_KEY`
is passed to the CLI as `MISTRAL_API_KEY`; `VIBE_MODEL` is written to a temporary
Vibe configuration. API keys are never passed as command arguments.

Vibe runs with `--output streaming`. Assistant and tool messages appear in the
Actions log as they arrive, and `tee` retains the stream in `/tmp/vibe-*.log`.
The pipeline preserves failures from both Vibe and log capture. Workspace cleanup
handles Vibe's read-only plugin cache; a cleanup error is logged as a warning and
preserves the original execution result so successful changes can be published.

Before implementation starts, the agent sets the issue's visible active Projects
items to **In Progress** using their actual `Status` field and option IDs. It uses
`gh api graphql --paginate` for discovery and `gh project item-edit` for updates.
Closed projects, archived items, and items already in progress are skipped; no
new project membership is created. A Projects API or configuration failure stops
the run before announcing work or invoking Vibe. The bot needs the classic PAT
`project` scope and write access to the board, in addition to repository access.

The issue then receives an `in progress` label and a comment linking to the
Actions run. The label is created if needed without
changing an existing label. Exit cleanup removes it after completion, failure,
or catchable cancellation, and reports failures on the issue. A completed draft
PR is linked by the existing delivery comment. The Projects status stays
**In Progress** until a person or project automation changes it. Dry runs do not
update issues or Projects.

Vibe works in a temporary copy without Git metadata. The caller applies changes,
commits them, pushes the branch, then creates a draft PR or posts a response.
With `GITHUB_TOKEN`, Build is explicitly dispatched because its pushes do not
trigger push workflows. With a bot token, pushes trigger CI automatically and
the extra dispatch is skipped.

GitHub operations use the runner's preinstalled `gh`: issue/PR views, draft PR
creation, comments, workflow dispatch, and build logs use native commands.
Inline review endpoints, run provenance, and collection listings use `gh api`;
`--paginate` exhausts collections without the list commands' default result limit.
The optional `VIBE_GITHUB_TOKEN` takes precedence over `GH_TOKEN` and the Actions
`GITHUB_TOKEN`. All three workflows pass the selected token to checkout too, so
Git pushes use the same account. A bot token's login and ID are resolved before
Vibe runs; commits use its login and GitHub noreply email, and PRs/comments use
that account. The triggering Actions actor is unchanged. See the setup guide for
bot access and token scopes.

## Configuration

| Environment variable | Default |
| --- | --- |
| `VIBE_API_KEY` | Required |
| `GITHUB_TOKEN` | Actions default; fallback when neither bot token nor `GH_TOKEN` is set |
| `VIBE_GITHUB_TOKEN` | Optional bot-account PAT; overrides GitHub CLI and checkout authentication |
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
run per group.

CI repair verifies the run repository, workflow, branch, and commit before
checking out the failing branch. It skips stale runs. Bot-authored attempt
comments persist retry counts across runners and prevent duplicate processing of
the same Build run. Both the current bot's markers and legacy `github-actions[bot]`
markers count. Fork PRs and non-Vibe branches are skipped.
The CI workflow checks that the local action exists after checking out the
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
Native GitHub CLI commands and GraphQL requests use fixtures; REST `gh api` uses
the installed CLI against that local server, including pagination and HTTP errors.
They do not call Mistral or write to GitHub. The Vibe Agent Tests workflow runs
the suite on pushes and pull requests that change `.github/actions/vibe-agent/**`,
`.github/workflows/vibe-agent-*.yml`, or `.github/workflows/test.yml`. It can also
be dispatched manually. Application builds run separately in the Build workflow.
