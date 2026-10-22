#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/common.sh"

# Vibe caches plugin packages in read-only directories. Make this private
# workspace writable before deletion; cleanup must retain the execution result.
cleanup_vibe_workspace() {
    local work_dir="$1" execution_status="$2"
    chmod -R u+rwX -- "$work_dir" 2>/dev/null || true
    if ! rm -rf -- "$work_dir"; then
        log_warn "Could not clean up Vibe workspace: ${work_dir}" || true
    fi
    return "$execution_status"
}

# All three modes use the pinned CLI's headless interface. Credentials never
# enter command arguments or debug logs. Git publication belongs to the caller.
execute_vibe_prompt() (
    set -euo pipefail
    local prompt="$1" output_log="$2" repo_root work_dir timeout_seconds
    is_dry_run && { log_info "[DRY RUN] Would run Vibe"; return 0; }
    repo_root=$(pwd)
    work_dir=$(mktemp -d /tmp/vibe-work.XXXXXX)
    trap 'cleanup_vibe_workspace "$work_dir" "$?"' EXIT
    mkdir -p "$work_dir/repo" "$work_dir/home"
    printf '%s\n' "$prompt" > "$work_dir/prompt.md"
    # Build a valid TOML model entry instead of using the unsupported --model.
    python3 - "$work_dir/home/config.toml" "$VIBE_MODEL" <<'PY'
import json
from pathlib import Path
import sys
Path(sys.argv[1]).write_text(
    'active_model = "vibe-agent"\nenable_update_checks = false\n'
    '[[models]]\nalias = "vibe-agent"\nprovider = "mistral"\n'
    f'name = {json.dumps(sys.argv[2])}\n'
)
PY
    local excludes=(--exclude='.git' --exclude='.gradle/' --exclude='build/'
        --exclude='node_modules/' --exclude='.idea/' --exclude='*.iml' --exclude='.vibe/')
    rsync -a "${excludes[@]}" "$repo_root/" "$work_dir/repo/" || return 1
    cd "$work_dir/repo" || return 1
    timeout_seconds=$((VIBE_TIMEOUT_MINUTES * 60))
    log_info "Running Vibe (model: ${VIBE_MODEL}, timeout: ${timeout_seconds}s)"
    # Streaming emits assistant/tool messages during the run. tee keeps the same
    # events in the failure artifact; pipefail preserves CLI and logging failures.
    # The external timeout is a final guard if the CLI fails to shut down.
    if MISTRAL_API_KEY="$VIBE_API_KEY" VIBE_HOME="$work_dir/home" \
        timeout --kill-after=30 "$((timeout_seconds + 30))" \
        vibe --prompt-file "$work_dir/prompt.md" --auto-approve --trust \
        --time-limit "$timeout_seconds" --output streaming 2>&1 | tee "$output_log"; then
        log_info "Vibe completed"
    else
        local status=$?
        log_error "Vibe failed (exit ${status}); see ${output_log}"
        return "$status"
    fi
    rsync -a --delete "${excludes[@]}" "$work_dir/repo/" "$repo_root/"
)

run_vibe_cli() {
    local issue_number="$1" branch_name="$2" issue_data prompt
    [[ $(git branch --show-current) == "$branch_name" ]] || return 1
    issue_data=$(get_issue_details "$issue_number")
    prompt=$(build_vibe_prompt "$issue_number" "$(jq -r '.title' <<< "$issue_data")" "$(jq -r '.body // ""' <<< "$issue_data")")
    execute_vibe_prompt "$prompt" /tmp/vibe-execution.log
}

build_vibe_prompt() {
    local issue_number="$1" issue_title="$2" issue_body="$3"
    printf '# Issue Implementation Task\n\nIssue #%s: %s\n\n%s\n\nRepository: %s\n' \
        "$issue_number" "$issue_title" "$issue_body" "$GITHUB_REPO"
    # A literal heredoc preserves Markdown backticks without shell evaluation.
    cat <<'PROMPT'

## Requirements
1. Read AGENTS.md and follow existing DDD and Hexagonal Architecture patterns.
2. Implement only the requirements from the issue and add appropriate tests.
3. Follow the OParl specifications and GLOSSARY.md terminology.
4. Verify the Kotlin/Spring Boot application with `./gradlew build --no-daemon`.
5. Leave changes in the working tree. The workflow commits and publishes them.
6. Do not merge PRs.

## File Structure
```
src/main/kotlin/nrw/openbert/backend/
├── oparl/          # OParl entities and implementations
│   ├── domain/     # Domain models
│   ├── application/# Application services
│   └── infrastructure/# Adapters (HTTP, MCP, DB)
└── resolve/        # Resolution services
```
PROMPT
}

run_tests() {
    log_info "Running build and tests"
    [[ -f ./gradlew ]] || { log_error "gradlew not found"; return 1; }
    ./gradlew build --no-daemon 2>&1 | tee /tmp/vibe-build.log
}
