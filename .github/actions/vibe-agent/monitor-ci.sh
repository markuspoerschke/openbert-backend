#!/bin/bash
set -euo pipefail

# Mistral Vibe Agent - CI Monitoring
# Monitors CI status and fixes failures automatically

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/common.sh"

# Track retry attempts
DECLARATIVE_RETRY_COUNT=0

# Trigger CI monitoring workflow
trigger_ci_monitoring() {
    local branch_name="$1"
    local pr_url="$2"
    local issue_number="$3"
    
    log_info "Triggering CI monitoring for: ${branch_name}"
    
    if is_dry_run; then
        log_info "[DRY RUN] Would trigger CI monitoring"
        return 0
    fi
    
    # Get workflow ID for test workflow
    local workflow_id
    workflow_id=$(get_workflow_id "test.yml")
    
    if [[ -z "$workflow_id" ]]; then
        log_error "Could not find test workflow"
        return 1
    fi
    
    # Trigger workflow on the branch
    local trigger_data
    trigger_data=$(jq -n --arg ref "${branch_name}" '{"ref": $ref}')
    
    local response
    response=$(github_api POST "/actions/workflows/${workflow_id}/dispatches" "${trigger_data}")
    
    debug_log "CI trigger response: ${response}"
    
    # Store info for later monitoring
    echo "VIBE_MONITOR_BRANCH=${branch_name}" >> $GITHUB_ENV
    echo "VIBE_MONITOR_ISSUE=${issue_number}" >> $GITHUB_ENV
    echo "VIBE_MONITOR_PR=${pr_url}" >> $GITHUB_ENV
    
    log_info "CI triggered for branch: ${branch_name}"
    
    # Start monitoring in background
    monitor_ci_status "$branch_name" "$issue_number" "$pr_url" &
    
    return 0
}

# Get workflow ID by filename
get_workflow_id() {
    local filename="$1"
    local workflows
    workflows=$(github_api GET "/actions/workflows")
    
    echo "$workflows" | jq -r --arg name "$filename" \
        '.workflows[] | select(.path | contains($name)) | .id'
}

# Monitor CI status
monitor_ci_status() {
    local branch_name="$1"
    local issue_number="$2"
    local pr_url="$3"
    local attempt=0
    local max_attempts="${MAX_RETRIES}"
    
    log_info "Starting CI monitoring for ${branch_name}"
    
    while [[ $attempt -lt $max_attempts ]]; do
        attempt=$((attempt + 1))
        log_info "CI check attempt ${attempt}/${max_attempts}"
        
        # Get workflow runs for this branch
        local runs
        runs=$(github_api GET "/actions/workflows/test.yml/runs?branch=${branch_name}&per_page=1")
        
        debug_log "Workflow runs: ${runs}"
        
        local status
        status=$(echo "$runs" | jq -r '.workflow_runs[0].status // "queued"')
        local conclusion
        conclusion=$(echo "$runs" | jq -r '.workflow_runs[0].conclusion // ""')
        
        log_info "CI Status: ${status}, Conclusion: ${conclusion}"
        
        case "$status" in
            completed)
                if [[ "$conclusion" == "success" ]]; then
                    log_info "CI passed successfully"
                    return 0
                else
                    log_warn "CI failed with conclusion: ${conclusion}"
                    if ! handle_ci_failure "$branch_name" "$issue_number" "$attempt" "$max_attempts"; then
                        log_error "Failed to fix CI issues"
                        return 1
                    fi
                fi
                ;;
            in_progress|queued)
                log_info "CI still running, waiting..."
                sleep 60  # Wait 1 minute before checking again
                ;;
            *)
                log_info "Unknown status: ${status}, waiting..."
                sleep 60
                ;;
        esac
    done
    
    log_error "CI monitoring timed out after ${max_attempts} attempts"
    return 1
}

# Handle CI failure
handle_ci_failure() {
    local branch_name="$1"
    local issue_number="$2"
    local attempt="$3"
    local max_attempts="$4"
    
    log_info "Attempting to fix CI failure (attempt ${attempt}/${max_attempts})"
    
    # Get failure logs
    local failure_info
    failure_info=$(get_ci_failure_info "$branch_name")
    
    debug_log "CI Failure info: ${failure_info}"
    
    # Check if we should retry based on failure type
    if ! is_actionable_failure "$failure_info"; then
        log_warn "Failure is not actionable, manual intervention needed"
        add_pr_comment "$branch_name" "CI failed with non-actionable error: ${failure_info}"
        return 0
    fi
    
    # Let Vibe analyze and fix the issue
    if ! fix_ci_failure "$branch_name" "$failure_info"; then
        log_error "Vibe could not fix the issue"
        return 1
    fi
    
    # Commit the fixes
    local fix_message="vibe: Fix CI failure - attempt ${attempt}"
    commit_changes "$issue_number" "$fix_message"
    
    # Push changes
    if ! is_dry_run; then
        git push origin "$branch_name" || true
    fi
    
    return 0
}

# Get CI failure info
get_ci_failure_info() {
    local branch_name="$1"
    local runs
    runs=$(github_api GET "/actions/workflows/test.yml/runs?branch=${branch_name}&per_page=1")
    
    local run_id
    run_id=$(echo "$runs" | jq -r '.workflow_runs[0].id')
    
    if [[ -z "$run_id" || "$run_id" == "null" ]]; then
        echo "No workflow run found"
        return
    fi
    
    # Get run details
    local run_details
    run_details=$(github_api GET "/actions/workflows/test.yml/runs/${run_id}")
    
    echo "$run_details" | jq -r '
        {
            status: .status,
            conclusion: .conclusion,
            failure_reason: (.failure_reason // ""),
            test_summary: (.test_summary // "")
        } | tostring'
}

# Check if failure is actionable
is_actionable_failure() {
    local failure_info="$1"
    
    # Extract conclusion
    local conclusion
    conclusion=$(echo "$failure_info" | jq -r '.conclusion // ""')
    
    # Non-actionable failures
    case "$conclusion" in
        cancelled|timed_out|neutral)
            return 1
            ;;
        *)
            # Check for specific non-actionable patterns
            if echo "$failure_info" | grep -qiE "(timeout|out of memory|permission|authentication|rate limit)"; then
                return 1
            fi
            return 0
            ;;
    esac
}

# Let Vibe fix the CI failure
fix_ci_failure() {
    local branch_name="$1"
    local failure_info="$2"
    
    log_info "Running Vibe to fix CI failure"
    
    # Extract error details
    local error_details
    error_details=$(echo "$failure_info" | jq -r '.conclusion + ": " + (.failure_reason // "")')
    
    # Build prompt for Vibe
    local fix_prompt
    fix_prompt=$(cat <<EOF
# Fix CI Failure

## Context
CI failed with: ${error_details}

## Task
Analyze the CI failure and fix the issues in the code.

## Constraints
- Only fix the specific issue causing CI to fail
- Do not modify unrelated code
- Ensure all tests pass
- Follow existing code patterns

## Instructions
1. Check the CI logs and error messages
2. Identify the root cause
3. Make minimal changes to fix the issue
4. Verify the fix works

## Current Branch
${branch_name}
EOF
    )
    
    # Run Vibe with the fix prompt
    local timeout_seconds=$((VIBE_TIMEOUT_MINUTES * 60))
    
    echo "$fix_prompt" > /tmp/vibe-fix-prompt.md
    
    if timeout "${timeout_seconds}" vibe \
        --api-key "${VIBE_API_KEY}" \
        --model "${VIBE_MODEL}" \
        --no-interactive \
        --yes \
        --timeout "${timeout_seconds}" \
        /tmp/vibe-fix-prompt.md 2>/tmp/vibe-fix-output.log; then
        
        log_info "Vibe fix completed"
        return 0
    else
        log_error "Vibe fix timed out"
        return 1
    fi
}

# Add comment to PR
add_pr_comment() {
    local branch_name="$1"
    local message="$2"
    
    # Find PR by branch
    local prs
    prs=$(github_api GET "/pulls?state=open&head=${branch_name}")
    
    local pr_number
    pr_number=$(echo "$prs" | jq -r '.[0].number')
    
    if [[ -n "$pr_number" && "$pr_number" != "null" ]]; then
        local comment_data
        comment_data=$(jq -n --arg body "$message" '{body: $body}')
        
        github_api POST "/repos/${GITHUB_REPO}/issues/${pr_number}/comments" "${comment_data}" > /dev/null 2>&1 || true
        log_info "Added comment to PR #${pr_number}"
    fi
}
