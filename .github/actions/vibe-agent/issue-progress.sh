#!/bin/bash
set -euo pipefail

# This label describes an active implementation, rather than an open draft PR.
mark_issue_in_progress() {
    local issue_number="$1" labels
    labels=$(gh label list --repo "$GITHUB_REPO" --search 'in progress' --limit 100 --json name) || return 1
    if ! jq -e 'any(.[]; (.name | ascii_downcase) == "in progress")' <<< "$labels" > /dev/null; then
        gh label create 'in progress' --repo "$GITHUB_REPO" --color FBCA04 \
            --description 'Vibe agent is currently implementing this issue'
    fi
    gh issue edit "$issue_number" --repo "$GITHUB_REPO" --add-label 'in progress' > /dev/null
}

issue_run_location() {
    if [[ -n "${GITHUB_RUN_ID:-}" ]]; then
        printf '[Follow progress in GitHub Actions](%s/%s/actions/runs/%s).' \
            "${GITHUB_SERVER_URL:-https://github.com}" "$GITHUB_REPO" "$GITHUB_RUN_ID"
    else
        printf 'Follow progress in the local terminal.'
    fi
}

announce_issue_work() {
    local issue_number="$1" branch_name="$2" message
    printf -v message 'Vibe agent is starting work on this issue.\n\nBranch: %s\n\n%s' \
        "$branch_name" "$(issue_run_location)"
    gh issue comment "$issue_number" --repo "$GITHUB_REPO" --body-file - <<< "$message" > /dev/null
}

# Run on EXIT, including failed generation/publication and catchable cancellation.
# Preserve the implementation's exit status even when GitHub cleanup fails.
finish_issue_work() {
    local issue_number="$1" status="$2" started="$3" pr_url="$4" message
    if [[ "$started" == true ]]; then
        gh issue edit "$issue_number" --repo "$GITHUB_REPO" --remove-label 'in progress' > /dev/null \
            || log_warn "Could not remove the in progress label from issue #${issue_number}"
        if (( status != 0 )); then
            if [[ -n "$pr_url" ]]; then
                printf -v message 'Vibe agent created %s, but a follow-up step failed (exit %s).\n\n%s' \
                    "$pr_url" "$status" "$(issue_run_location)"
            else
                printf -v message 'Vibe agent stopped before completing this issue (exit %s). Manual review is needed.\n\n%s' \
                    "$status" "$(issue_run_location)"
            fi
            gh issue comment "$issue_number" --repo "$GITHUB_REPO" --body-file - <<< "$message" > /dev/null \
                || log_warn "Could not report the failure on issue #${issue_number}"
        fi
    fi
    return "$status"
}
