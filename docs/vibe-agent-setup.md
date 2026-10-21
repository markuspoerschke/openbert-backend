# Mistral Vibe Agent Setup for OpenBert Backend

The agent implements issues labeled `vibe`, creates draft PRs, responds to
review feedback, and attempts CI repairs. A person must review and merge the PRs.

## Requirements

- GitHub Actions enabled on the repository.
- A Mistral API key with access to the configured model and active billing.
- Permission for GitHub Actions to create pull requests, enabled in
  **Settings → Actions → General → Workflow permissions**.
- The agent workflows and scripts merged into the default branch before using
  scheduled, review, or `workflow_run` triggers.

The composite action sets up Python 3.12, Java 25, jq, rsync, and Mistral Vibe
3.0.1. It uses the supported headless CLI rather than a shell installer.

## Repository secrets

Configure these under **Settings → Secrets and variables → Actions**:

| Secret | Purpose | Default |
| --- | --- | --- |
| `VIBE_API_KEY` | Mistral API key; required | None |
| `VIBE_MODEL` | Mistral model name | `mistral-large-latest` |
| `MAX_RETRIES` | Maximum CI repair attempts per PR | `3` |
| `VIBE_TIMEOUT_MINUTES` | Timeout for each Vibe invocation | `30` |
| `VIBE_DEBUG` | Additional diagnostic logging | `false` |

`GITHUB_TOKEN` is automatically supplied by GitHub. Each agent workflow requests
`contents: write`, `pull-requests: write`, `issues: write`, and `actions: write`.
Issues write permission is needed for issue/PR conversation comments and retry
tracking. Actions write permission is needed to dispatch Build explicitly.
No PAT or GitHub App installation is required by these workflows.

`VIBE_API_KEY` is exposed to Vibe as `MISTRAL_API_KEY`. The selected model is
configured through a temporary `VIBE_HOME/config.toml`; credentials are not
written into the repository or passed as CLI arguments.

## Run the agent

1. Open an issue with concrete requirements and add the `vibe` label.
2. Wait for the hourly **Vibe Agent - Scheduled** workflow, or open
   **Actions → Vibe Agent - Manual → Run workflow**.
3. Optionally supply an issue number. Without one, the oldest eligible issue
   is selected. A manual selection must also have the `vibe` label.
4. Review the resulting draft PR and its Build results.

The scheduled run skips selection while any `vibe/` PR is open. Manual runs skip
an issue that already has an open Vibe PR. Branches use
`vibe/<issue-number>-<title-slug>`.

For a preview, enable **dry-run** on the manual or scheduled dispatch. The run
performs selection and reports the intended branch without invoking Vibe,
modifying code, committing, pushing, or creating a PR.

```bash
gh workflow run vibe-agent-manual.yml -f issue-number=123
gh workflow run vibe-agent-manual.yml -f dry-run=true
```

## Delivery and builds

Vibe works in a temporary repository copy without Git metadata. It receives the
issue text or feedback through a prompt file, with automatic tool approval and
a configured execution timeout. Its returned changes are committed and pushed
by the workflow. A draft PR is created only after the remote branch exists.
An empty implementation is reported as a failure instead of creating an empty PR.

Initial implementations run `./gradlew build --no-daemon`. If that local build
fails, changes are still published as a draft so CI and a reviewer can assess
them. PR descriptions leave validation unchecked rather than claiming success.

`GITHUB_TOKEN` pushes and PR creation do not trigger ordinary push/PR workflows.
The agent therefore dispatches `test.yml`, which declares `workflow_dispatch`.
The completed **Build** triggers the CI monitor, so no background polling process
needs to survive the implementation job.

## CI repairs

The monitor handles only failed Builds originating from this repository on a
Vibe branch with an open PR. It checks the exact failing run's commit against
the current remote head; stale runs are skipped. It checks out that branch before
calling Vibe and passes failed jobs and logs into the repair prompt.

Each attempted repair adds a bot-authored `vibe-ci-attempt` marker to the PR.
Those comments persist the retry count across runners and identify Build runs
already handled. `MAX_RETRIES` applies across the PR's lifetime. A successful
repair commit is pushed and another Build is dispatched. If Vibe produces no
changes, or the limit is reached, manual intervention is required.

## Review feedback

The review workflow accepts comments from repository owners, members,
collaborators, and `chatgpt-codex-connector[bot]`. It skips fork PRs and non-Vibe
branches. Actionable comments are handled on the current remote branch head.
Changes are committed, pushed, and built before a response is posted.

Inline feedback gets a reply in its original review thread, including when the
triggering comment was itself a reply. PR conversation comments receive a
conversation response. A run that produces no changes reports that manual review
is still needed; it does not claim to have fixed the feedback.

## Concurrency

All four agent workflows use the same concurrency group:

```yaml
concurrency:
  group: vibe-agent-${{ github.repository }}
  cancel-in-progress: false
```

This prevents competing implementations, review fixes, and CI repairs from
running simultaneously on separate runners. There is no repository-local lock
file. Custom workflows using the composite action must use the same group.
GitHub Actions retains one pending run per group; a newer event may replace an
older pending event. Retrigger feedback whose pending run was superseded.

## Diagnostics and validation

Agent diagnostics appear in Actions logs. Failed agent executions upload
`/tmp/vibe-*.log` for seven days. Check API errors, model availability, repository
workflow permissions, the retry markers, and the exact failing Build run when
investigating a failure.

Run the regression suite without live credentials or Mistral usage:

```bash
python3 -m unittest discover -s .github/actions/vibe-agent/tests -v
shellcheck -e SC1091 .github/actions/vibe-agent/*.sh
actionlint .github/workflows/vibe-agent-*.yml .github/workflows/test.yml
```

For local orchestration, provide `GITHUB_REPOSITORY`, `GITHUB_TOKEN`, and
`VIBE_API_KEY`, then invoke `bash .github/actions/vibe-agent/entrypoint.sh manual 123`.
The Actions concurrency group does not apply to local processes.

See the [action README](../.github/actions/vibe-agent/README.md) and the
[Mistral Vibe documentation](https://github.com/mistralai/mistral-vibe#readme).
