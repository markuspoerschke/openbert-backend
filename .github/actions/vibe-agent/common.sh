#!/bin/bash

# Mistral Vibe Agent - Common Functions
# Shared utilities for all scripts

LOGFILE="${LOGFILE:-/tmp/vibe-agent.log}"

# Keep diagnostics off stdout: callers capture JSON, issue numbers and URLs.
# Logging functions
log_info() {
    echo "[VIBE] $1" >&2
    echo "[INFO] $(date '+%Y-%m-%d %H:%M:%S') - $1" >> "$LOGFILE"
}

log_warn() {
    echo "::warning::[VIBE] $1" >&2
    echo "[WARN] $(date '+%Y-%m-%d %H:%M:%S') - $1" >> "$LOGFILE"
}

log_error() {
    echo "::error::[VIBE] $1" >&2
    echo "[ERROR] $(date '+%Y-%m-%d %H:%M:%S') - $1" >> "$LOGFILE"
}

# Configuration with defaults
VIBE_API_KEY="${VIBE_API_KEY:-}"
GITHUB_TOKEN="${GITHUB_TOKEN:-}"
VIBE_MODEL="${VIBE_MODEL:-mistral-large-latest}"
MAX_RETRIES="${MAX_RETRIES:-3}"
VIBE_TIMEOUT_MINUTES="${VIBE_TIMEOUT_MINUTES:-30}"
VIBE_DEBUG="${VIBE_DEBUG:-false}"
DRY_RUN="${DRY_RUN:-false}"

# GitHub CLI handles authentication, request errors, and API pagination.
# Keep the Actions-provided token as the fallback for local GH_TOKEN users.
export GH_TOKEN="${GH_TOKEN:-$GITHUB_TOKEN}"
GITHUB_REPO="${GITHUB_REPOSITORY:-}"

# Get repository info
get_repo_info() {
    if [[ -n "${GITHUB_REPOSITORY:-}" ]]; then
        echo "$GITHUB_REPOSITORY"
    elif [[ -d .git ]]; then
        git config --get remote.origin.url | sed 's/.*github.com[:/]//;s/\.git$//'
    else
        echo ""
    fi
}

# Validate required credentials
validate_credentials() {
    if [[ -z "$VIBE_API_KEY" ]]; then
        log_error "VIBE_API_KEY is not set"
        exit 1
    fi
    
    if [[ -z "$GH_TOKEN" ]]; then
        log_error "GH_TOKEN or GITHUB_TOKEN must be set"
        exit 1
    fi
    
    # Test GitHub token
    if ! gh repo view "$GITHUB_REPO" --json nameWithOwner > /dev/null; then
        log_error "GitHub repository access failed"
        exit 1
    fi
}

# Check if dry run mode
is_dry_run() {
    [[ "$DRY_RUN" == "true" ]]
}

# Debug logging
debug_log() {
    if [[ "$VIBE_DEBUG" == "true" ]]; then
        echo "[DEBUG] $1" >&2
        echo "[DEBUG] $(date '+%Y-%m-%d %H:%M:%S') - $1" >> "$LOGFILE"
    fi
}

require_number() {
    [[ "$2" =~ ^[1-9][0-9]*$ ]] || { log_error "$1 must be a positive integer"; return 1; }
}

push_branch() {
    is_dry_run && return 0
    git push origin "HEAD:refs/heads/$1"
}
