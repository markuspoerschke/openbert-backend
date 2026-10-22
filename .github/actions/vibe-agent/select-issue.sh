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
    gh api --paginate "repos/${GITHUB_REPO}/issues?state=open&labels=vibe&per_page=100" | \
        jq -c '.[] | select(.pull_request == null) | {number, title, created_at, labels}'
}

# gh issue/pr list impose result limits; API pagination exhausts the collection.
list_open_prs() {
    gh api --paginate "repos/${GITHUB_REPO}/pulls?state=open&per_page=100" | jq -s 'add'
}

# Check if there's an active PR for a vibe issue
has_active_vibe_pr() {
    local prs
    prs=$(list_open_prs) || return 2
    
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
    
    prs=$(list_open_prs) || return 2
    
    local count
    count=$(echo "$prs" | jq -r "[.[] | select(.head.ref | startswith(\"vibe/${issue_number}-\"))] | length") || return 2
    
    [[ "$count" -gt 0 ]]
}

# Check if a specific issue has the vibe label
issue_has_vibe_label() {
    local issue_number="$1"
    local issue
    
    issue=$(gh issue view "$issue_number" --repo "$GITHUB_REPO" --json labels)
    
    echo "$issue" | jq -e '.labels[]?.name | select(. == "vibe")' > /dev/null 2>&1
}

# Get issue details
get_issue_details() {
    local issue_number="$1"
    gh issue view "$issue_number" --repo "$GITHUB_REPO" \
        --json number,title,body,labels,createdAt,updatedAt \
        --jq '{number, title, body, labels: [.labels[].name], created_at: .createdAt, updated_at: .updatedAt}'
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
