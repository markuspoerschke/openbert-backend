#!/bin/bash
set -euo pipefail

# Mistral Vibe Agent - Review Comment Handler
# Responds to actionable PR review comments

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/common.sh"

# Review and conversation comments have separate REST routes.
handle_review_comment() {
    local pr_number="$1" comment_id="$2" comment_data comment_body comment_user
    local branch_name issue_number reply_id
    comment_data=$(get_comment_details "$pr_number" "$comment_id")
    comment_body=$(jq -r '.body' <<< "$comment_data")
    comment_user=$(jq -r '.user.login' <<< "$comment_data")
    reply_id=$(jq -r '.in_reply_to_id // .id' <<< "$comment_data")
    branch_name=$(get_pr_branch "$pr_number")
    if ! is_vibe_branch "$branch_name"; then
        log_info "Not an open, same-repository Vibe PR; skipping"
        return 0
    fi
    issue_number=$(extract_issue_number "$branch_name")
    if ! is_actionable_comment "$comment_body" "$comment_user"; then
        log_info "Comment is not actionable; skipping"
        return 0
    fi
    is_dry_run && { log_info "[DRY RUN] Would address comment #${comment_id} on ${branch_name}"; return 0; }
    checkout_pr_branch "$branch_name"
    process_comment_with_vibe "$comment_body" "$branch_name" "$issue_number"
    if git diff --quiet && [[ -z $(git ls-files --others --exclude-standard) ]]; then
        add_comment_response "$pr_number" "$reply_id" "The review run produced no code changes; this feedback still needs manual review."
        return 0
    fi
    commit_changes "$issue_number" "vibe: Address review feedback from @${comment_user}"
    push_branch "$branch_name"
    trigger_ci_monitoring "$branch_name"
    add_comment_response "$pr_number" "$reply_id" "Addressed in $(git rev-parse --short HEAD). Please review the changes and build results."
}

get_comment_details() {
    local comment_id="$2"
    if [[ "${VIBE_COMMENT_KIND:-${GITHUB_EVENT_NAME:-pull_request_review_comment}}" == issue_comment ]]; then
        gh api "repos/${GITHUB_REPO}/issues/comments/${comment_id}"
    else
        gh api "repos/${GITHUB_REPO}/pulls/comments/${comment_id}"
    fi
}

get_pr_branch() {
    gh pr view "$1" --repo "$GITHUB_REPO" --json state,headRefName,isCrossRepository \
        --jq 'select(.state == "OPEN" and .isCrossRepository == false) | .headRefName'
}

is_vibe_branch() {
    [[ "$1" =~ ^vibe/[1-9][0-9]*-[a-z0-9-]+$ ]]
}

extract_issue_number() {
    local branch="$1"
    branch="${branch#vibe/}"
    printf '%s\n' "${branch%%-*}"
}

is_actionable_comment() {
    local body="$1"
    local user="$2"
    
    # Skip bot comments
    if [[ "$user" == "github-actions[bot]" || "$user" == "github-actions" ]]; then
        return 1
    fi
    
    # Skip CI status comments
    if echo "$body" | grep -qiE "(ci|build|test|check)"; then
        # But allow if it's a request
        if ! echo "$body" | grep -qiE "(please|can you|could you|fix|update|change)"; then
            return 1
        fi
    fi
    
    # Must contain actionable language
    if echo "$body" | grep -qiE "(please|can you|could you|should|fix|update|change|add|remove|modify|refactor)"; then
        return 0
    fi
    
    # Must be a question or request
    if echo "$body" | grep -qE '\?$'; then
        return 0
    fi
    
    return 1
}

# Always fetch the remote head, even if a local branch already exists.
checkout_pr_branch() {
    local branch_name="$1"
    is_vibe_branch "$branch_name" || return 1
    git fetch origin "refs/heads/${branch_name}"
    git checkout -B "$branch_name" FETCH_HEAD
}

# Process comment with Vibe
process_comment_with_vibe() {
    local comment_body="$1"
    local branch_name="$2"
    local issue_number="$3"
    
    log_info "Running Vibe to process comment"
    
    # Build prompt
    local prompt
    prompt=$(cat <<EOF
# Address Review Feedback

## PR Branch
${branch_name}

## Issue
#${issue_number}

## Feedback to Address
${comment_body}

## Task
Analyze the feedback above and make the necessary changes to address it.

## Constraints
- Only make changes related to the feedback
- Preserve all existing functionality
- Follow existing code patterns
- Ensure tests pass

## Instructions
1. Read the feedback carefully
2. Identify what needs to change
3. Make minimal, focused changes
4. Verify the changes address the feedback
EOF
    )
    
    execute_vibe_prompt "$prompt" /tmp/vibe-review-output.log
}

# Add response to comment
add_comment_response() {
    local pr_number="$1"
    local comment_id="$2"
    local message="$3"
    
    log_info "Adding response to comment #${comment_id}"
    
    if is_dry_run; then
        log_info "[DRY RUN] Would add response: ${message}"
        return 0
    fi
    
    if [[ "${VIBE_COMMENT_KIND:-${GITHUB_EVENT_NAME:-pull_request_review_comment}}" == issue_comment ]]; then
        gh pr comment "$pr_number" --repo "$GITHUB_REPO" --body-file - <<< "$message" > /dev/null
    else
        gh api --method POST "repos/${GITHUB_REPO}/pulls/${pr_number}/comments/${comment_id}/replies" \
            --raw-field "body=$message" > /dev/null
    fi
}
