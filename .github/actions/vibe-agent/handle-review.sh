#!/bin/bash
set -euo pipefail

# Mistral Vibe Agent - Review Comment Handler
# Responds to actionable PR review comments

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/common.sh"

# Main function to handle review comments
handle_review_comment() {
    local pr_number="$1"
    local comment_id="$2"
    local comment_data comment_body comment_user branch_name issue_number
    
    # Get comment details
    comment_data=$(get_comment_details "$pr_number" "$comment_id")
    comment_body=$(echo "$comment_data" | jq -r '.body')
    comment_user=$(echo "$comment_data" | jq -r '.user.login')
    
    log_info "Handling review comment #${comment_id} from @${comment_user}"
    debug_log "Comment body: ${comment_body}"
    
    # Check if this is a vibe PR
    branch_name=$(get_pr_branch "$pr_number")
    
    if ! is_vibe_branch "$branch_name"; then
        log_info "Not a Vibe branch, ignoring comment"
        return 0
    fi
    
    # Extract issue number from branch name
    issue_number=$(extract_issue_number "$branch_name")
    
    if [[ -z "$issue_number" ]]; then
        log_info "Could not extract issue number from branch: ${branch_name}"
        return 0
    fi
    
    # Check if comment is actionable
    if ! is_actionable_comment "$comment_body" "$comment_user"; then
        log_info "Comment is not actionable, ignoring"
        return 0
    fi
    
    log_info "Processing actionable comment on PR #${pr_number}"
    
    # Checkout the PR branch
    checkout_pr_branch "$branch_name" "$pr_number"
    
    # Let Vibe process the comment
    if ! process_comment_with_vibe "$comment_body" "$branch_name" "$issue_number"; then
        log_error "Failed to process comment with Vibe"
        add_comment_response "$pr_number" "$comment_id" "Sorry, I wasn't able to process your request. Please check the logs."
        return 1
    fi
    
    # Commit and push changes
    local commit_message="vibe: Address review feedback from @${comment_user}"
    commit_changes "$issue_number" "$commit_message"
    
    if ! is_dry_run; then
        git push origin "$branch_name" || true
    fi
    
    # Add response comment
    add_comment_response "$pr_number" "$comment_id" "I've addressed your feedback. Please review the changes."
    
    log_info "Successfully processed review comment"
    return 0
}

# Get comment details
get_comment_details() {
    local pr_number="$1"
    local comment_id="$2"
    github_api GET "/repos/${GITHUB_REPO}/pulls/${pr_number}/comments/${comment_id}"
}

# Get PR branch
get_pr_branch() {
    local pr_number="$1"
    github_api GET "/repos/${GITHUB_REPO}/pulls/${pr_number}" | jq -r '.head.ref'
}

# Check if branch is a Vibe branch
is_vibe_branch() {
    local branch="$1"
    [[ "$branch" =~ ^vibe/[0-9]+- ]]
}

# Extract issue number from branch name
extract_issue_number() {
    local branch="$1"
    echo "$branch" | grep -oE '^vibe/([0-9]+)' | grep -oE '[0-9]+'
}

# Check if comment is actionable
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

# Checkout PR branch
checkout_pr_branch() {
    local branch_name="$1"
    local pr_number="$2"
    
    log_info "Checking out PR branch: ${branch_name}"
    
    if is_dry_run; then
        log_info "[DRY RUN] Would checkout branch: ${branch_name}"
        return 0
    fi
    
    # Fetch the branch
    git fetch origin "$branch_name" || true
    git checkout "$branch_name" || git checkout -b "$branch_name" origin/"$branch_name"
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
    
    # Run Vibe
    local timeout_seconds=$((VIBE_TIMEOUT_MINUTES * 60))
    
    echo "$prompt" > /tmp/vibe-review-prompt.md
    
    if timeout "${timeout_seconds}" vibe \
        --api-key "${VIBE_API_KEY}" \
        --model "${VIBE_MODEL}" \
        --no-interactive \
        --yes \
        --timeout "${timeout_seconds}" \
        /tmp/vibe-review-prompt.md 2>/tmp/vibe-review-output.log; then
        
        log_info "Vibe comment processing completed"
        return 0
    else
        log_error "Vibe comment processing timed out"
        return 1
    fi
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
    
    local response_data
    response_data=$(jq -n --arg body "$message" --arg in_reply_to "${comment_id}" \
        '{body: $body, in_reply_to_id: $in_reply_to}')
    
    github_api POST "/repos/${GITHUB_REPO}/issues/${pr_number}/comments" "${response_data}" > /dev/null 2>&1 || true
}
