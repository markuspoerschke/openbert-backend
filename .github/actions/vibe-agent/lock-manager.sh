#!/bin/bash
set -euo pipefail

# Mistral Vibe Agent - Lock Manager
# Prevents concurrent Vibe executions

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/common.sh"

# Lock file location (in repo to persist across workflow runs)
LOCK_FILE=".github/vibe-agent.lock"
LOCK_TIMEOUT_SECONDS=3600  # 1 hour timeout

# Acquire lock
acquire_lock() {
    local lock_dir=".github"
    
    # Create .github directory if it doesn't exist
    mkdir -p "$lock_dir"
    
    # Check if lock file exists
    if [[ -f "$LOCK_FILE" ]]; then
        local lock_time lock_pid
        lock_time=$(head -1 "$LOCK_FILE" 2>/dev/null || echo "0")
        lock_pid=$(tail -1 "$LOCK_FILE" 2>/dev/null || echo "0")
        
        local current_time
        current_time=$(date +%s)
        
        # Check if lock is stale (older than timeout)
        local elapsed=$((current_time - lock_time))
        
        if [[ $elapsed -gt $LOCK_TIMEOUT_SECONDS ]]; then
            log_warn "Removing stale lock (age: ${elapsed}s > ${LOCK_TIMEOUT_SECONDS}s)"
            release_lock
        else
            log_info "Lock exists, created at $(date -r "$LOCK_FILE" '+%Y-%m-%d %H:%M:%S')"
            return 1
        fi
    fi
    
    # Create new lock
    echo "$(date +%s)" > "$LOCK_FILE"
    echo "$$" >> "$LOCK_FILE"
    
    log_info "Lock acquired"
    
    # Set cleanup trap
    trap release_lock EXIT
    
    return 0
}

# Release lock
release_lock() {
    if [[ -f "$LOCK_FILE" ]]; then
        local lock_pid
        lock_pid=$(tail -1 "$LOCK_FILE" 2>/dev/null || echo "0")
        
        # Only remove if we own the lock or it's stale
        if [[ "$lock_pid" == "$$" || "$lock_pid" == "0" ]]; then
            rm -f "$LOCK_FILE"
            log_info "Lock released"
        else
            log_info "Lock belongs to another process (PID: $lock_pid), not removing"
        fi
    fi
}

# Check if lock exists
is_locked() {
    [[ -f "$LOCK_FILE" ]]
}

# Get lock age in seconds
get_lock_age() {
    if [[ ! -f "$LOCK_FILE" ]]; then
        echo "0"
        return
    fi
    
    local lock_time
    lock_time=$(head -1 "$LOCK_FILE" 2>/dev/null || echo "0")
    local current_time
    current_time=$(date +%s)
    
    echo $((current_time - lock_time))
}

# Initialize lock management
init_lock_manager() {
    # Cleanup very old locks on startup
    if [[ -f "$LOCK_FILE" ]]; then
        local age
        age=$(get_lock_age)
        if [[ $age -gt $((LOCK_TIMEOUT_SECONDS * 2)) ]]; then
            log_warn "Cleaning up very old lock (age: ${age}s)"
            rm -f "$LOCK_FILE"
        fi
    fi
}

# Main execution - auto-initialize
init_lock_manager
