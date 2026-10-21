# Mistral Vibe Agent Setup for OpenBert Backend

This document describes how to configure and use the autonomous Mistral Vibe coding agent for the OpenBert Backend repository.

## Overview

The Vibe agent automates issue resolution by:

1. **Polling**: Checking for new GitHub issues labeled `vibe` every hour
2. **Selection**: Picking one actionable issue (skipping if a `vibe/` branch PR is already open)
3. **Implementation**: Running Mistral Vibe CLI non-interactively in GitHub Actions
4. **Delivery**: Creating a draft PR with the implementation
5. **CI Integration**: Monitoring CI results and fixing failures automatically
6. **Review**: Responding to actionable PR review comments

## Prerequisites

### Mistral Vibe CLI

- **Mistral Vibe Pro Subscription**: Required for API access
- **API Key**: Obtain from [Mistral AI Console](https://console.mistral.ai/)
- **Billing**: Vibe usage is billed separately from Mistral API; check current pricing at [Mistral Pricing](https://mistral.ai/pricing/)

### GitHub Requirements

- Repository must be on GitHub.com (GitHub Enterprise not currently supported)
- Workflows require GitHub Actions enabled
- Minimum permissions as specified below

---

## Configuration

### 1. GitHub Secrets

Create the following **Repository Secrets** in GitHub Settings > Secrets > Actions:

#### Required Secrets

| Secret Name | Description | Format |
|-------------|-------------|--------|
| `VIBE_API_KEY` | Mistral Vibe API key | 64-character hex string |
| `VIBE_MODEL` | Model to use (optional) | e.g., `mistral-large-latest` |
| `GITHUB_TOKEN` | Automatic, no setup needed | Provided by GitHub Actions |

#### Optional Secrets

| Secret Name | Description | Default |
|-------------|-------------|---------|
| `VIBE_ORGANIZATION` | Mistral organization ID | Uses default |
| `VIBE_PROJECT` | Mistral project ID | Uses default |
| `MAX_RETRIES` | Maximum retry attempts for CI failures | `3` |
| `VIBE_TIMEOUT_MINUTES` | Timeout for Vibe execution | `30` |

### 2. GitHub Variables

Create the following **Repository Variables** (Settings > Secrets > Variables > Actions):

| Variable Name | Description | Default |
|---------------|-------------|---------|
| `VIBE_CHECK_INTERVAL` | Schedule interval in minutes | `60` |

### 3. Repository Permissions

The workflow requires the following **minimum permissions**:

- **Contents**: Read and Write (to push branches and create PRs)
- **Pull Requests**: Read and Write (to create PRs and comment)
- **Issues**: Read (to list and read issues)
- **Actions**: Read (to check workflow runs)
- **Checks**: Read (to monitor CI status)

**Recommended**: Use a **Repository-level PAT** with `repo` scope instead of the default `GITHUB_TOKEN` for more control. Set it as `GH_PAT` secret.

### 4. GitHub App (Optional but Recommended)

For better security and granular permissions, create a GitHub App:

1. Go to Settings > Developer Settings > GitHub Apps > New GitHub App
2. App name: `Vibe Agent`
3. Description: `Autonomous coding agent for issue resolution`
4. Homepage URL: Your repository URL
5. Callback URL: Not needed (serverless)
6. Permissions:
   - Repository: `Contents: Read & Write`, `Pull Requests: Read & Write`, `Issues: Read & Write`
   - Organization: `Members: Read` (if needed)
7. Subscribe to events: `Issues`, `Pull Request`, `Check Run`, `Workflow Run`
8. Install on your repository
9. Generate Private Key
10. Store as secrets:
    - `GH_APP_ID`: App ID
    - `GH_APP_INSTALLATION_ID`: Installation ID
    - `GH_APP_PRIVATE_KEY`: Private key (PEM format)

---

## Activation

### Enable Workflows

The workflows are disabled by default. To activate:

1. Push the workflow files to your repository
2. Go to Actions tab
3. Find the `vibe-agent-scheduled.yml` workflow
4. Click "Enable workflow"
5. Repeat for `vibe-agent-manual.yml`

### Manual Trigger

To manually trigger the agent:

```bash
# Via GitHub CLI
gh workflow run vibe-agent-manual.yml

# Or via API
curl -X POST \
  -H "Authorization: token YOUR_TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  https://api.github.com/repos/OWNER/REPO/actions/workflows/vibe-agent-manual.yml/dispatches \
  -d '{"ref":"main"}'
```

---

## Workflow Files

### 1. Scheduled Workflow (`vibe-agent-scheduled.yml`)

```yaml
name: Vibe Agent - Scheduled

on:
  schedule:
    - cron: '0 * * * *'  # Every hour
  workflow_dispatch:     # Allow manual trigger

permissions:
  contents: write
  pull-requests: write
  issues: read
  actions: read
  checks: read

concurrency:
  group: vibe-agent
  cancel-in-progress: false

jobs:
  check-and-implement:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v7
        with:
          fetch-depth: 0
      
      - name: Run Vibe Agent
        uses: ./.github/actions/vibe-agent
        with:
          mode: 'scheduled'
        env:
          VIBE_API_KEY: ${{ secrets.VIBE_API_KEY }}
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

### 2. Manual Workflow (`vibe-agent-manual.yml`)

```yaml
name: Vibe Agent - Manual

on:
  workflow_dispatch:
    inputs:
      issue-number:
        description: 'Specific issue number to work on'
        required: false
        type: number

permissions:
  contents: write
  pull-requests: write
  issues: read
  actions: read
  checks: read

jobs:
  implement-issue:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v7
        with:
          fetch-depth: 0
      
      - name: Run Vibe Agent
        uses: ./.github/actions/vibe-agent
        with:
          mode: 'manual'
          issue-number: ${{ inputs.issue-number }}
        env:
          VIBE_API_KEY: ${{ secrets.VIBE_API_KEY }}
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

---

## Implementation Details

### Directory Structure

```
.github/
├── actions/
│   └── vibe-agent/
│       ├── action.yml          # Action definition
│       ├── entrypoint.sh       # Main entry point
│       ├── select-issue.sh     # Issue selection logic
│       ├── run-vibe.sh         # Vibe CLI execution
│       ├── create-pr.sh        # PR creation logic
│       ├── monitor-ci.sh       # CI monitoring
│       ├── handle-review.sh    # Review comment handling
│       └── lock-manager.sh     # Concurrency control
└── workflows/
    ├── vibe-agent-scheduled.yml
    └── vibe-agent-manual.yml
```

### Issue Selection Logic

The agent selects issues based on:

1. **Label**: Must have `vibe` label
2. **State**: Must be open
3. **No active PR**: No open PR with `vibe/<number>-*` branch
4. **Priority**: Sorted by creation date (oldest first)

### Branch Naming Convention

```
vibe/<issue-number>-<slug>
```

Example: `vibe/123-fix-oparl-parser`

### Commit Messages

```
vibe: <description>

Implements #<issue-number>

Generated by Mistral Vibe.
Co-Authored-By: Mistral Vibe <vibe@mistral.ai>
```

---

## Usage

### For Issue Authors

To request Vibe automation:

1. Create an issue with clear requirements
2. Add the `vibe` label
3. Wait for the agent to process it (up to 1 hour)

**Best Practices:**
- Use clear, actionable titles
- Include acceptance criteria
- Reference specific files or functions
- Keep scope focused and small

### For Maintainers

#### Approving PRs

1. Review the draft PR created by Vibe
2. Leave comments for any changes needed
3. The agent will automatically respond to actionable comments
4. Once satisfied, remove draft status and merge manually

#### Monitoring

Check workflow runs:
- Go to Actions tab
- Filter by `vibe-agent` workflows
- View logs for each run

#### Debugging

Common issues and solutions:

| Issue | Cause | Solution |
|-------|-------|----------|
| Vibe not starting | Missing API key | Set `VIBE_API_KEY` secret |
| No issues selected | All have active PRs | Wait for PRs to be merged/closed |
| CI failures not fixed | Retry limit reached | Increase `MAX_RETRIES` or manual intervention |
| Permission denied | Insufficient permissions | Check repository permissions |
| Rate limited | API rate limits | Check Mistral billing/usage |

---

## Cost Considerations

### Mistral Vibe Pricing

As of October 2026:
- **Mistral Vibe Pro**: Required for autonomous agents
- **Pricing**: Check [Mistral AI Pricing](https://mistral.ai/pricing/) for current rates
- **Billing**: Separate from Mistral API; based on tokens used

### GitHub Actions Cost

- **Free tier**: 2,000 minutes/month for free accounts
- **Pro accounts**: 3,000 minutes/month included
- **Enterprise**: Varies by plan

**Estimated usage**:
- Each Vibe execution: ~10-30 minutes
- With hourly scheduling: ~240-720 minutes/month
- Well within free tier limits

---

## Security Considerations

### Credential Management

1. **Never commit secrets** to the repository
2. Use GitHub Secrets for all sensitive data
3. Limit permissions to minimum required
4. Rotate API keys periodically
5. Audit workflow runs regularly

### Safe Practices

1. **No auto-merge**: PRs are always draft and require manual merge
2. **Branch protection**: Consider protecting main branch
3. **Review required**: Always review Vibe-generated code before merging
4. **Sandbox environment**: Vibe runs in isolated GitHub Actions runner

### API Key Usage

- Stored only in GitHub Secrets (encrypted)
- Accessible only during workflow execution
- Not logged or exposed in outputs
- Automatically masked in workflow logs

---

## Testing

### Local Testing

To test the agent locally:

```bash
# Set environment variables
export VIBE_API_KEY="your-api-key"
export GITHUB_TOKEN="your-gh-token"
export GITHUB_REPOSITORY="markuspoerschke/openbert-backend"

# Run the entry point
./.github/actions/vibe-agent/entrypoint.sh scheduled
```

### Test Workflow

1. Create a test issue with `vibe` label
2. Trigger the manual workflow
3. Verify PR is created
4. Check CI status
5. Verify automatic fixes if CI fails

---

## Troubleshooting

### Common Problems

#### 1. Workflow Not Triggering

**Check:**
- Workflow is enabled in Actions tab
- Secrets are correctly set
- Repository has Actions enabled

#### 2. No Issues Found

**Check:**
- Issues have `vibe` label
- Issues are open
- No active `vibe/` branch PRs

#### 3. Vibe CLI Errors

**Check:**
- API key is valid
- Billing is active on Mistral account
- Network connectivity from GitHub Actions

#### 4. Permission Errors

**Check:**
- Workflow permissions are sufficient
- GITHUB_TOKEN has correct scopes
- Repository settings allow workflows to push

#### 5. CI Failures Not Fixed

**Check:**
- MAX_RETRIES not exceeded
- Error is actionable (not environmental)
- Vibe has context to understand the error

### Logs and Diagnostics

View detailed logs:
1. Go to Actions tab
2. Select the workflow run
3. Expand each step to see logs
4. Download logs for offline analysis

Enable debug logging:
```yaml
env:
  ACTIONS_STEP_DEBUG: true
  VIBE_DEBUG: true
```

---

## Advanced Configuration

### Custom Models

Set a specific model via secret:
```
VIBE_MODEL: mistral-large-latest
```

### Custom Timeout

Adjust execution timeout:
```
VIBE_TIMEOUT_MINUTES: 60  # Default: 30
```

### Issue Filtering

Customize issue selection with labels:
```
VIBE_ADDITIONAL_LABELS: "priority:high,bug"
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `VIBE_API_KEY` | Mistral API key | Required |
| `VIBE_MODEL` | Model to use | `mistral-large-latest` |
| `VIBE_ORGANIZATION` | Mistral org ID | Default |
| `VIBE_PROJECT` | Mistral project ID | Default |
| `GITHUB_TOKEN` | GitHub token | Provided |
| `GITHUB_REPOSITORY` | Repository | Auto-detected |
| `MAX_RETRIES` | CI retry limit | `3` |
| `VIBE_TIMEOUT_MINUTES` | Execution timeout | `30` |
| `VIBE_DEBUG` | Enable debug logs | `false` |
| `DRY_RUN` | Test mode (no commits) | `false` |

---

## Support

### Mistral Vibe Documentation

- [Mistral Vibe CLI Docs](https://docs.mistral.ai/vibe/)
- [Mistral AI Console](https://console.mistral.ai/)

### GitHub Actions Documentation

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Workflow Syntax](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)

---

## Version History

| Date | Version | Changes |
|------|---------|---------|
| 2026-10-21 | 1.0.0 | Initial implementation |
