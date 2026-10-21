#!/bin/bash
set -euo pipefail

# Mistral Vibe Agent - Main Entrypoint
# This script orchestrates the autonomous coding agent workflow

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/common.sh"
source "${SCRIPT_DIR}/select-issue.sh"
source "${SCRIPT_DIR}/run-vibe.sh"
source "${SCRIPT_DIR}/create-pr.sh"
source "${SCRIPT_DIR}/monitor-ci.sh"
source "${SCRIPT_DIR}/handle-review.sh"
source "${SCRIPT_DIR}/lock-manager.sh"

# Logging setup
LOGFILE="/tmp/vibe-agent-$(date +%Y%m%d-%H%M%S).log"
touch "$LOGFILE"
exec > >(tee -a "$LOGFILE") 2>&1

log_info "Starting Mistral Vibe Agent"
log_info "Mode: ${VIBE_AGENT_MODE}"
log_info "Issue: ${VIBE_ISSUE_NUMBER:-none}"
log_info "PR: ${VIBE_PR_NUMBER:-none}"

# Ensure we have required credentials
validate_credentials

# Load repository info
GITHUB_REPO="${GITHUB_REPOSITORY:-$(get_repo_info)}"
log_info "Repository: ${GITHUB_REPO}"

# Main dispatch
case "${VIBE_AGENT_MODE}" in
    scheduled)
        log_info "Running in scheduled mode"
        run_scheduled_mode
        ;;
    manual)
        log_info "Running in manual mode"
        run_manual_mode "${VIBE_ISSUE_NUMBER}"
        ;;
    review)
        log_info "Running in review mode"
        run_review_mode "${VIBE_PR_NUMBER}" "${VIBE_COMMENT_ID}"
        ;;
    *)
        log_error "Unknown mode: ${VIBE_AGENT_MODE}"
        exit 1
        ;;
esac

log_info "Vibe Agent completed successfully"
exit 0

# Functions for each mode
run_scheduled_mode() {
    local issue
    
    # Check for existing vibe branches
    if has_active_vibe_pr; then
        log_info "Active vibe PR exists, skipping scheduled run"
        return 0
    fi
    
    # Select an issue
    issue=$(select_issue)
    if [[ -z "$issue" ]]; then
        log_info "No actionable issues found"
        return 0
    fi
    
    log_info "Selected issue: $issue"
    implement_issue "$issue"
}

run_manual_mode() {
    local issue_number="$1"
    
    if [[ -z "$issue_number" ]]; then
        log_info "No issue number specified, selecting one automatically"
        issue_number=$(select_issue)
        if [[ -z "$issue_number" ]]; then
            log_info "No actionable issues found"
            return 0
        fi
    fi
    
    # Check if issue has vibe label
    if ! issue_has_vibe_label "$issue_number"; then
        log_error "Issue #${issue_number} does not have 'vibe' label"
        exit 1
    fi
    
    implement_issue "$issue_number"
}

run_review_mode() {
    local pr_number="$1"
    local comment_id="$2"
    
    if [[ -z "$pr_number" || -z "$comment_id" ]]; then
        log_error "PR number and comment ID are required for review mode"
        exit 1
    fi
    
    handle_review_comment "$pr_number" "$comment_id"
}

# Core implementation function
implement_issue() {
    local issue_number="$1"
    local issue_data branch_name pr_url
    
    # Get issue details
    issue_data=$(get_issue_details "$issue_number")
    log_info "Issue data: $issue_data"
    
    # Create branch name
    local slug=$(create_slug "$issue_data")
    branch_name="vibe/${issue_number}-${slug}"
    
    # Create working branch
    log_info "Creating branch: ${branch_name}"
    create_working_branch "$branch_name" "$issue_number"
    
    # Run Vibe to implement the issue
    log_info "Running Vibe CLI for issue #${issue_number}"
    if ! run_vibe_cli "$issue_number" "$branch_name"; then
        log_error "Vibe CLI execution failed"
        # Attempt cleanup
        git checkout main || true
        git branch -D "$branch_name" || true
        exit 1
    fi
    
    # Run tests
    log_info "Running tests"
    if ! run_tests; then
        log_warn "Tests failed, will attempt to fix in CI monitoring"
    fi
    
    # Create PR
    log_info "Creating draft PR"
    pr_url=$(create_draft_pr "$branch_name" "$issue_number")
    log_info "Draft PR created: ${pr_url}"
    
    # Store PR info for CI monitoring
    echo "VIBE_BRANCH=${branch_name}" >> $GITHUB_ENV
    echo "VIBE_PR_URL=${pr_url}" >> $GITHUB_ENV
    
    # Monitor CI (async - will be handled by separate workflow)
    log_info "Triggering CI monitoring workflow"
    trigger_ci_monitoring "$branch_name" "$pr_url" "$issue_number"
}
