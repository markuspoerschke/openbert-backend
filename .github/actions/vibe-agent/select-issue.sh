#!/bin/bash
set -euo pipefail

# Mistral Vibe Agent - Issue Selection
# Selects an actionable issue labeled 'vibe'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/common.sh"

# Main function to select an issue
select_issue() {
    local issues
    issues=$(list_vibe_issues) || return 1
    
    if [[ -z "$issues" ]]; then
        log_info "No issues with 'vibe' label found"
        return 0
    fi
    
    debug_log "Found issues: $issues"
    
    # Filter out issues that already have active vibe PRs
    local filtered_issues=()
    while IFS= read -r issue; do
        local issue_number
        issue_number=$(echo "$issue" | jq -r '.number')
        
        local pr_status=0
        has_vibe_pr_for_issue "$issue_number" || pr_status=$?
        case "$pr_status" in
            0) ;; # An existing PR already implements this issue.
            1) filtered_issues+=("$issue") ;;
            *) return 1 ;;
        esac
    done <<< "$issues"
    
    if [[ ${#filtered_issues[@]} -eq 0 ]]; then
        log_info "All vibe issues have active PRs"
        return 0
    fi
    
    # Sort by creation date (oldest first)
    local sorted_issues
    sorted_issues=$(printf '%s\n' "${filtered_issues[@]}" | jq -s 'sort_by(.created_at)')
    
    # Select the first one
    local selected
    selected=$(echo "$sorted_issues" | jq -r '.[0].number')
    
    echo "$selected"
    return 0
}

# List all open issues with 'vibe' label
list_vibe_issues() {
    github_api_list "/issues?state=open&labels=vibe" | \
        jq -c '.[] | select(.pull_request == null or .pull_request == "") | {number, title, created_at, labels}'
}

# Check if there's an active PR for a vibe issue
has_active_vibe_pr() {
    local prs
    prs=$(github_api_list "/pulls?state=open") || return 2
    
    local count
    count=$(echo "$prs" | jq -r '[.[] | select(.head.ref | startswith("vibe/"))] | length') || return 2
    
    if [[ "$count" -gt 0 ]]; then
        log_info "Found $count active vibe PR(s)"
        return 0
    fi
    
    return 1
}

# Check if a specific issue has an active vibe PR
has_vibe_pr_for_issue() {
    local issue_number="$1"
    local prs
    
    prs=$(github_api_list "/pulls?state=open") || return 2
    
    local count
    count=$(echo "$prs" | jq -r "[.[] | select(.head.ref | startswith(\"vibe/${issue_number}-\"))] | length") || return 2
    
    [[ "$count" -gt 0 ]]
}

# Check if a specific issue has the vibe label
issue_has_vibe_label() {
    local issue_number="$1"
    local issue
    
    issue=$(github_api GET "/issues/${issue_number}")
    
    echo "$issue" | jq -e '.labels[]?.name | select(. == "vibe")' > /dev/null 2>&1
}

# Get issue details
get_issue_details() {
    local issue_number="$1"
    github_api GET "/issues/${issue_number}" | jq -c '. |
        {
            number: .number,
            title: .title,
            body: .body,
            labels: [.labels[].name],
            created_at: .created_at,
            updated_at: .updated_at
        }'
}

# Create a slug from issue title
create_slug() {
    local issue_data="$1"
    local title
    title=$(echo "$issue_data" | jq -r '.title')
    
    # Convert to lowercase, replace spaces with hyphens, remove special chars
    echo "$title" | tr '[:upper:]' '[:lower:]' | \
        sed 's/[^a-z0-9]/-/g' | \
        sed 's/--*/-/g' | \
        sed 's/^-//;s/-$//' | \
        head -c 50
}

# Main execution
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    select_issue
fi
