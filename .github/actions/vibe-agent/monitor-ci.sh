#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/common.sh"

# GITHUB_TOKEN pushes do not generate push/PR workflows. workflow_dispatch does.
# The completed Build is handled by workflow_run; no background polling process.
trigger_ci_monitoring() {
    local branch_name="$1"
    is_dry_run && return 0
    gh workflow run test.yml --repo "$GITHUB_REPO" --ref "$branch_name"
    log_info "Build dispatched for ${branch_name}"
}

find_pr_number() {
    local branch_name="$1" head
    head=$(jq -rn --arg head "${GITHUB_REPO%%/*}:${branch_name}" '$head | @uri')
    gh api "repos/${GITHUB_REPO}/pulls?state=open&head=${head}" | jq -r '.[0].number // empty'
}

# Attempt comments persist across runners, including failed repair executions.
# All mutating Vibe workflows share one concurrency group.
get_retry_comments() {
    gh api --paginate "repos/${GITHUB_REPO}/issues/$1/comments?per_page=100" | jq -sc '[.[][] |
        select(.user.login == "github-actions[bot]" and
            (.body | startswith("<!-- vibe-ci-attempt:")))]'
}

get_retry_count() {
    get_retry_comments "$1" | jq 'length'
}

increment_retry_count() {
    local pr_number="$1" run_id="$2" attempt="$3" body
    printf -v body '<!-- vibe-ci-attempt:%s -->\nCI repair attempt %s for Build run %s.' "$run_id" "$attempt" "$run_id"
    add_pr_comment "$pr_number" "$body"
}

get_ci_failure_info() {
    local run_id="$1" jobs logs
    jobs=$(gh run view "$run_id" --repo "$GITHUB_REPO" --json jobs)
    logs=$(gh run view "$run_id" --repo "$GITHUB_REPO" --log-failed | tail -c 60000) || return 1
    jq -n --argjson jobs "$jobs" --arg logs "$logs" \
        '{jobs: [$jobs.jobs[] | {name, conclusion, steps}], logs: $logs}'
}

fix_ci_failure() {
    local branch_name="$1" failure_info="$2" prompt
    printf -v prompt '# Fix CI Failure\n\nBranch: %s\n\nFailed jobs and logs:\n%s\n\nAnalyze the failure, make focused changes, follow AGENTS.md, and verify with ./gradlew build --no-daemon. Leave changes uncommitted for the workflow. Do not merge PRs.' "$branch_name" "$failure_info"
    execute_vibe_prompt "$prompt" /tmp/vibe-fix-output.log
}

add_pr_comment() {
    local pr_number="$1" message="$2"
    is_dry_run && return 0
    gh pr comment "$pr_number" --repo "$GITHUB_REPO" --body-file - <<< "$message" > /dev/null
}

run_ci_monitor() {
    local branch="${VIBE_MONITOR_BRANCH:-}" run_id="${VIBE_RUN_ID:-}"
    local run pr_number comments retry_count issue_number failure_info remote_sha
    is_vibe_branch "$branch" || { log_info "Not a Vibe branch; skipping"; return 0; }
    require_number "Build run ID" "$run_id"
    # Revalidate server-side data before checking out or executing branch code.
    run=$(gh api "repos/${GITHUB_REPO}/actions/runs/${run_id}")
    if ! jq -e --arg repo "$GITHUB_REPO" --arg branch "$branch" \
        '.head_repository.full_name == $repo and .head_branch == $branch and
         .path == ".github/workflows/test.yml" and .conclusion == "failure"' <<< "$run" > /dev/null; then
        log_info "Not a failed same-repository Build; skipping"
        return 0
    fi
    pr_number=$(find_pr_number "$branch")
    [[ -n "$pr_number" ]] || { log_info "No open PR; skipping"; return 0; }
    [[ $(get_pr_branch "$pr_number") == "$branch" ]] || return 1
    comments=$(get_retry_comments "$pr_number")
    if jq -e --arg marker "<!-- vibe-ci-attempt:${run_id} -->" \
        'any(.[]; .body | startswith($marker))' <<< "$comments" > /dev/null; then
        log_info "Build already handled; skipping"
        return 0
    fi
    retry_count=$(get_retry_count "$pr_number")
    if (( retry_count >= MAX_RETRIES )); then
        log_warn "Retry limit reached (${retry_count}/${MAX_RETRIES}); manual intervention required"
        add_pr_comment "$pr_number" "CI repair limit reached (${retry_count}/${MAX_RETRIES}); manual intervention is required."
        return 0
    fi
    is_dry_run && { log_info "[DRY RUN] Would repair Build ${run_id} on ${branch}"; return 0; }
    git fetch origin "refs/heads/${branch}"
    remote_sha=$(git rev-parse FETCH_HEAD)
    if [[ "$remote_sha" != "$(jq -r '.head_sha' <<< "$run")" ]]; then
        log_info "Build is stale: branch has advanced; skipping"
        return 0
    fi
    git checkout -B "$branch" "$remote_sha"
    issue_number=$(extract_issue_number "$branch")
    failure_info=$(get_ci_failure_info "$run_id")
    increment_retry_count "$pr_number" "$run_id" "$((retry_count + 1))"
    fix_ci_failure "$branch" "$failure_info"
    if git diff --quiet && [[ -z $(git ls-files --others --exclude-standard) ]]; then
        add_pr_comment "$pr_number" "CI repair produced no changes; manual review is required."
        return 0
    fi
    commit_changes "$issue_number" "vibe: Fix CI failure - attempt $((retry_count + 1))"
    push_branch "$branch"
    trigger_ci_monitoring "$branch"
    add_pr_comment "$pr_number" "CI repair attempt $((retry_count + 1)) pushed in $(git rev-parse --short HEAD); a new Build was dispatched."
}
