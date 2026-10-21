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

# GitHub API helpers
GITHUB_API="${GITHUB_API_URL:-https://api.github.com}"
GITHUB_REPO="${GITHUB_REPOSITORY:-}"

github_api() {
    local method="$1"
    local endpoint="$2"
    local data="${3:-}"
    
    local url="${GITHUB_API}/repos/${GITHUB_REPO}${endpoint}"
    
    local headers=(
        "-H" "Authorization: token ${GITHUB_TOKEN}"
        "-H" "Accept: application/vnd.github.v3+json"
    )
    
    if [[ "$method" == "GET" ]]; then
        curl --fail-with-body -sS "${headers[@]}" "$url"
    else
        headers+=("-H" "Content-Type: application/json")
        curl --fail-with-body -sS -X "$method" "${headers[@]}" -d "$data" "$url"
    fi
}

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
    
    if [[ -z "$GITHUB_TOKEN" ]]; then
        log_error "GITHUB_TOKEN is not set"
        exit 1
    fi
    
    # Test GitHub token
    if ! github_api GET "" > /dev/null; then
        log_error "GitHub token is invalid or expired"
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

# Fetch array endpoints across all pages without corrupting JSON strings.
github_api_list() {
    local endpoint="$1" page=1 response separator='?'
    [[ "$endpoint" != *'?'* ]] || separator='&'
    while :; do
        response=$(github_api GET "${endpoint}${separator}per_page=100&page=${page}") || return 1
        jq -c '.[]' <<< "$response"
        [[ $(jq 'length' <<< "$response") -eq 100 ]] || break
        page=$((page + 1))
    done | jq -s '.'
}

require_number() {
    [[ "$2" =~ ^[1-9][0-9]*$ ]] || { log_error "$1 must be a positive integer"; return 1; }
}

push_branch() {
    is_dry_run && return 0
    git push origin "HEAD:refs/heads/$1"
}
