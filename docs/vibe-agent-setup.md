# Mistral Vibe Agent Setup for OpenBert Backend

The agent implements issues labeled `vibe`, creates draft PRs, responds to
review feedback, and attempts CI repairs. A person must review and merge the PRs.

## Requirements

- GitHub Actions enabled on the repository.
- A Mistral API key with access to the configured model and active billing.
- Either permission for GitHub Actions to create pull requests, enabled in
  **Settings → Actions → General → Workflow permissions → Allow GitHub Actions
  to create and approve pull requests**, or a bot token configured below.
- For Projects status updates, a bot PAT with the `project` scope and write
  access to each linked project. Repository write access alone is insufficient.
- The agent workflows and scripts merged into the default branch before using
  scheduled, review, or `workflow_run` triggers.

The composite action sets up Python 3.12, Java 25, jq, rsync, and Mistral Vibe
3.0.1. It uses the supported headless CLI rather than a shell installer.

## Repository secrets

Configure these under **Settings → Secrets and variables → Actions**:

| Secret | Purpose | Default |
| --- | --- | --- |
| `VIBE_API_KEY` | Mistral API key; required | None |
| `VIBE_GITHUB_TOKEN` | Optional personal access token belonging to the bot account | Uses `GITHUB_TOKEN` |
| `VIBE_MODEL` | Mistral model name | `mistral-large-latest` |
| `MAX_RETRIES` | Maximum CI repair attempts per PR | `3` |
| `VIBE_TIMEOUT_MINUTES` | Timeout for each Vibe invocation | `30` |
| `VIBE_DEBUG` | Additional diagnostic logging | `false` |

## GitHub authentication and identity

Without `VIBE_GITHUB_TOKEN`, the agent uses the automatically supplied
`GITHUB_TOKEN`. Each agent workflow requests
`contents: write`, `pull-requests: write`, `issues: write`, and `actions: write`.
Issues write permission is needed for issue/PR conversation comments and retry
tracking. Actions write permission is needed to dispatch Build explicitly.
The repository must also enable **Allow GitHub Actions to create and approve
pull requests**. The workflow's `pull-requests: write` permission alone does not
enable this setting. If it is disabled, PR creation fails with
`GitHub Actions is not permitted to create or approve pull requests`.

To publish as your dedicated GitHub bot account:

1. Give the bot write access to this repository and accept the invitation.
2. Give the bot write access to the linked Projects board (for example,
   **Openbert → Settings → Manage access**).
3. Sign in as the bot and create a personal access token. For a bot collaborating
   on this personally owned repository, use a classic PAT with `repo`,
   `workflow`, and `project` scopes. These allow repository operations, workflow
   changes, and Projects status updates respectively.
   GitHub currently does not support fine-grained PATs for outside/repository
   collaborators; see [GitHub's PAT limitations](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#fine-grained-personal-access-tokens-limitations).
   Where fine-grained PATs are supported, grant this repository Contents,
   Pull requests, Issues, and Workflows write permissions, plus Actions read.
4. Save that token as the repository Actions secret `VIBE_GITHUB_TOKEN`.

The `project` scope allows Projects queries and mutations; `read:project` alone
cannot update a status. The built-in `GITHUB_TOKEN` does not provide this Projects
access. See [GitHub's Projects API authentication requirements](https://docs.github.com/en/issues/planning-and-tracking-with-projects/automating-your-project/using-the-api-to-manage-projects#authentication).

All four agent workflows use the same selected token for checkout/Git pushes and
GitHub CLI operations. A bot token takes precedence over `GH_TOKEN` and
`GITHUB_TOKEN`; an invalid bot token fails rather than falling back to another
identity. The agent resolves the token's login and numeric ID before running Vibe
and sets the commit author/committer to that login and its GitHub noreply address.
PRs and comments are authored by the bot. The workflow's triggering actor still
identifies the user/event that started the run.

The preinstalled GitHub CLI (`gh`) handles repository access, issue and PR
operations, comments, workflow dispatch, and build inspection. `gh api --paginate` retrieves complete
collections, and `gh api` handles inline review endpoints. Custom HTTP request
and pagination helpers are not needed. Locally, `GH_TOKEN` can be supplied instead
of `GITHUB_TOKEN` and takes precedence when both are present and no bot token
is set.

`VIBE_API_KEY` is exposed to Vibe as `MISTRAL_API_KEY`. The selected model is
configured through a temporary `VIBE_HOME/config.toml`; credentials are not
written into the repository or passed as CLI arguments.

## Run the agent

1. Open an issue with concrete requirements and add the `vibe` label.
2. Wait for the **Vibe Agent - Scheduled** workflow (every three hours), or open
   **Actions → Vibe Agent - Manual → Run workflow**.
3. Optionally supply an issue number. Without one, the oldest eligible issue
   is selected. A manual selection must also have the `vibe` label.
4. Follow the start comment's Actions link. The Projects status becomes
   **In Progress**, and the issue receives the temporary `in progress` label.
5. Review the resulting draft PR and its Build results.

Before adding the label or posting its start comment, the agent reads all linked
Projects items with `gh api graphql --paginate` and updates their `Status` field
with `gh project item-edit`. Project, item, field, and option IDs are discovered
automatically. It matches the **In Progress** option without case sensitivity.
Archived items, closed projects, and items already in that status are skipped.
An issue without visible active Projects items can still run; the agent does not
add it to a project. The bot needs access to the board for it to be visible.

If a Projects request fails or an active linked project has no matching Status
field/option, the run stops before the start comment and Vibe execution. Correct
the token scope, project access, or field configuration before retrying.

The agent creates the `in progress` label when needed. Existing label color and
description are preserved. On exit it attempts to remove the label, including after failures and
catchable cancellation. Failures receive a comment linking back to the run; a
successful implementation receives the existing draft-PR delivery comment.
The Projects status remains **In Progress** after exit, including failures and
cancellation, until a person or project automation changes it. Publishing a draft
does not mean the issue is complete.

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

Bot-token pushes and PR creation trigger the normal Build workflows, so the agent
does not dispatch an additional Build. `GITHUB_TOKEN` pushes do not trigger push
workflows, and its PR workflows may require approval. In that mode the agent
explicitly dispatches `test.yml`, which declares `workflow_dispatch`.
The completed **Build** triggers the CI monitor, so no background polling process
needs to survive the implementation job.

## CI repairs

The monitor handles only failed Builds originating from this repository on a
Vibe branch with an open PR. It checks the exact failing run's commit against
the current remote head; stale runs are skipped. It checks out that branch before
calling Vibe and passes failed jobs and logs into the repair prompt.

Each attempted repair adds a bot-authored `vibe-ci-attempt` marker to the PR.
Markers from both the current bot account and `github-actions[bot]` are counted,
so switching to a bot token preserves existing retry limits and duplicate checks.
Those comments persist the retry count across runners and identify Build runs
already handled. `MAX_RETRIES` applies across the PR's lifetime. A successful
repair commit is pushed and another Build is triggered. If Vibe produces no
changes, or the limit is reached, manual intervention is required.

## Review feedback

The review workflow accepts comments from repository owners, members,
collaborators, and `chatgpt-codex-connector[bot]`. It skips fork PRs and non-Vibe
branches. Actionable comments are handled on the current remote branch head.
Changes are committed, pushed, and built before a response is posted.
Comments from the authenticated bot itself and `github-actions[bot]` are ignored
to prevent the bot's own responses from starting another review cycle.

Review and CI workflows always check out trusted default-branch scripts. If that
checkout does not contain the local action yet (for example, while reviewing the
PR that first introduces the agent), they skip with an explicit notice. They do
not load the PR's agent scripts as a fallback.

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

Vibe uses `--output streaming` so assistant/tool messages are visible during
execution in the Actions log. The same stream is retained in `/tmp/vibe-*.log`
via `tee`, with CLI failures still propagated. Failed agent executions upload
those logs for seven days using the Node.js 24-based artifact action.
Temporary-workspace cleanup makes Vibe's read-only plugin cache writable before
deletion. If deletion still fails, it warns without turning a successful execution
into a failure or hiding an actual CLI failure. Check API errors, model availability,
repository workflow permissions, the retry markers, and the exact failing Build
run when investigating a failure.

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
