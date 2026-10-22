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
VIBE_GITHUB_TOKEN="${VIBE_GITHUB_TOKEN:-}"
VIBE_BOT_LOGIN="${VIBE_BOT_LOGIN:-github-actions[bot]}"
VIBE_MODEL="${VIBE_MODEL:-mistral-large-latest}"
MAX_RETRIES="${MAX_RETRIES:-3}"
VIBE_TIMEOUT_MINUTES="${VIBE_TIMEOUT_MINUTES:-30}"
VIBE_DEBUG="${VIBE_DEBUG:-false}"
DRY_RUN="${DRY_RUN:-false}"

# GitHub CLI handles authentication, request errors, and API pagination.
# The optional bot token is also passed to checkout by the workflows, keeping
# API operations and Git pushes under the same account.
export GH_TOKEN="${VIBE_GITHUB_TOKEN:-${GH_TOKEN:-$GITHUB_TOKEN}}"
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
        log_error "VIBE_GITHUB_TOKEN, GH_TOKEN or GITHUB_TOKEN must be set"
        exit 1
    fi
    
    # Test GitHub token
    if ! gh repo view "$GITHUB_REPO" --json nameWithOwner > /dev/null; then
        log_error "GitHub repository access failed"
        exit 1
    fi
}

# Installation GITHUB_TOKENs cannot query /user. Resolve the account only when
# a personal bot token was supplied; otherwise retain the Actions identity.
configure_git_identity() {
    local account account_id email='41898282+github-actions[bot]@users.noreply.github.com'
    VIBE_BOT_LOGIN='github-actions[bot]'
    if [[ -n "$VIBE_GITHUB_TOKEN" ]]; then
        account=$(gh api user) || { log_error "Could not identify the VIBE_GITHUB_TOKEN account"; return 1; }
        VIBE_BOT_LOGIN=$(jq -er '.login | select(type == "string" and length > 0)' <<< "$account") || return 1
        account_id=$(jq -er '.id' <<< "$account") || return 1
        require_number "GitHub account ID" "$account_id" || return 1
        email="${account_id}+${VIBE_BOT_LOGIN}@users.noreply.github.com"
    fi
    git config user.name "$VIBE_BOT_LOGIN"
    git config user.email "$email"
    log_info "Publishing as ${VIBE_BOT_LOGIN}"
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
