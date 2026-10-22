#!/bin/bash
set -euo pipefail

# A label does not change the Projects board. Resolve item, field, and option IDs
# from each linked project rather than assuming a project number or option ID.
set_issue_projects_in_progress() {
    local issue_number="$1" items item project_id item_id field_id option_id title
    # shellcheck disable=SC2016 # These are GraphQL variables, not shell variables.
    local query='
      query VibeIssueProjects($owner: String!, $name: String!, $number: Int!, $endCursor: String) {
        repository(owner: $owner, name: $name) {
          issue(number: $number) {
            projectItems(first: 100, after: $endCursor) {
              nodes {
                id
                isArchived
                fieldValueByName(name: "Status") {
                  ... on ProjectV2ItemFieldSingleSelectValue { optionId }
                }
                project {
                  id
                  title
                  closed
                  field(name: "Status") {
                    ... on ProjectV2SingleSelectField { id options { id name } }
                  }
                }
              }
              pageInfo { hasNextPage endCursor }
            }
          }
        }
      }'
    if ! items=$(gh api graphql --paginate --raw-field "query=$query" \
        --raw-field "owner=${GITHUB_REPO%%/*}" --raw-field "name=${GITHUB_REPO#*/}" \
        --field "number=$issue_number" | jq -se '[.[] |
          if .errors or .data.repository.issue == null then error("Cannot read issue projects")
          else .data.repository.issue.projectItems.nodes[] end |
          select(.isArchived == false) |
          if .project == null then error("Cannot read linked project")
          else select(.project.closed == false) end |
          {item: .id, project: .project.id, title: .project.title, field: .project.field.id,
           current: .fieldValueByName.optionId,
           options: [.project.field.options[]? | select((.name | ascii_downcase) == "in progress") | .id]}]'); then
        log_error "Cannot read Projects for issue #${issue_number}. VIBE_GITHUB_TOKEN needs the project scope and access to the linked project."
        return 1
    fi
    # Validate every target before changing any board or announcing work.
    if ! jq -e 'all(.[]; .item != null and .project != null and .field != null and (.options | length) == 1)' \
        <<< "$items" > /dev/null; then
        log_error 'Each active linked project must have a Status single-select field with an In Progress option.'
        return 1
    fi
    if [[ $(jq 'length' <<< "$items") == 0 ]]; then
        log_info "No visible active Projects item for issue #${issue_number}; skipping project status update"
        return 0
    fi
    while IFS= read -r item; do
        project_id=$(jq -r '.project' <<< "$item")
        item_id=$(jq -r '.item' <<< "$item")
        field_id=$(jq -r '.field' <<< "$item")
        option_id=$(jq -r '.options[0]' <<< "$item")
        title=$(jq -r '.title' <<< "$item")
        if [[ $(jq -r '.current' <<< "$item") == "$option_id" ]]; then
            continue
        fi
        if ! gh project item-edit --project-id "$project_id" --id "$item_id" \
            --field-id "$field_id" --single-select-option-id "$option_id" > /dev/null; then
            log_error "Cannot set issue #${issue_number} to In Progress in ${title}. Check the token's project scope and the bot's project write access."
            return 1
        fi
        log_info "Project ${title}: issue #${issue_number} set to In Progress"
    done < <(jq -c '.[]' <<< "$items")
}

# The label describes an active implementation; the board status also remains
# In Progress while a delivered draft PR awaits review.
mark_issue_in_progress() {
    local issue_number="$1" labels
    set_issue_projects_in_progress "$issue_number" || return 1
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
