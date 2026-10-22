#!/bin/bash
set -euo pipefail

# Load every function before dispatch. Sourcing this file has no side effects.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/common.sh"
source "${SCRIPT_DIR}/select-issue.sh"
source "${SCRIPT_DIR}/run-vibe.sh"
source "${SCRIPT_DIR}/create-pr.sh"
source "${SCRIPT_DIR}/monitor-ci.sh"
source "${SCRIPT_DIR}/handle-review.sh"
source "${SCRIPT_DIR}/issue-progress.sh"

run_scheduled_mode() {
    local issue pr_status=0
    has_active_vibe_pr || pr_status=$?
    if (( pr_status == 0 )); then
        log_info "Active vibe PR exists, skipping scheduled run"
        return 0
    elif (( pr_status != 1 )); then
        return 1
    fi
    issue=$(select_issue) || return 1
    if [[ -z "$issue" ]]; then
        log_info "No actionable issues found"
        return 0
    fi
    implement_issue "$issue"
}

run_manual_mode() {
    local issue_number="${1:-}" pr_status=0
    if [[ -z "$issue_number" ]]; then
        issue_number=$(select_issue) || return 1
        [[ -n "$issue_number" ]] || return 0
    fi
    require_number "Issue number" "$issue_number"
    if ! issue_has_vibe_label "$issue_number"; then
        log_error "Issue #${issue_number} does not have the vibe label"
        return 1
    fi
    has_vibe_pr_for_issue "$issue_number" || pr_status=$?
    if (( pr_status == 0 )); then
        log_info "Issue already has an open Vibe PR"
        return 0
    elif (( pr_status != 1 )); then
        return 1
    fi
    implement_issue "$issue_number"
}

implement_issue() (
    local issue_number="$1" issue_data slug branch_name pr_url="" started=false
    issue_data=$(get_issue_details "$issue_number")
    slug=$(create_slug "$issue_data")
    branch_name="vibe/${issue_number}-${slug:-issue}"
    if is_dry_run; then
        log_info "[DRY RUN] Would implement #${issue_number} on ${branch_name} and create a draft PR"
        return 0
    fi
    trap 'finish_issue_work "$issue_number" "$?" "$started" "$pr_url"' EXIT
    trap 'exit 130' INT
    trap 'exit 143' TERM
    mark_issue_in_progress "$issue_number"
    started=true
    announce_issue_work "$issue_number" "$branch_name"
    log_info "Starting implementation of issue #${issue_number}: ${branch_name}"
    create_working_branch "$branch_name"
    run_vibe_cli "$issue_number" "$branch_name"
    if ! run_tests; then
        log_warn "Tests failed; publishing a draft for CI and manual review"
    fi
    commit_changes "$issue_number" "vibe: $(jq -r '.title' <<< "$issue_data")"
    push_branch "$branch_name"
    pr_url=$(create_draft_pr "$branch_name" "$issue_number")
    log_info "Draft PR created: ${pr_url}"
    trigger_ci_monitoring "$branch_name"
)

main() {
    local mode="${1:-${VIBE_AGENT_MODE:-scheduled}}"
    LOGFILE="${LOGFILE:-/tmp/vibe-agent.log}"
    export GITHUB_REPO="${GITHUB_REPOSITORY:-$(get_repo_info)}"
    validate_credentials
    require_number "VIBE_TIMEOUT_MINUTES" "$VIBE_TIMEOUT_MINUTES"
    [[ "$MAX_RETRIES" =~ ^(0|[1-9][0-9]*)$ ]] || { log_error "MAX_RETRIES must be a nonnegative integer"; return 1; }
    configure_git_identity
    log_info "Starting Mistral Vibe Agent: ${mode}"
    case "$mode" in
        scheduled) run_scheduled_mode ;;
        manual) run_manual_mode "${2:-${VIBE_ISSUE_NUMBER:-}}" ;;
        review)
            require_number "PR number" "${VIBE_PR_NUMBER:-${2:-}}"
            require_number "Comment ID" "${VIBE_COMMENT_ID:-${3:-}}"
            handle_review_comment "${VIBE_PR_NUMBER:-$2}" "${VIBE_COMMENT_ID:-$3}"
            ;;
        ci-monitor) run_ci_monitor ;;
        *) log_error "Unknown mode: ${mode}"; return 1 ;;
    esac
}

if [[ "${BASH_SOURCE[0]}" == "$0" ]]; then
    main "$@"
fi
