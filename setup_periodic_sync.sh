#!/bin/bash

# Setup Periodic Database Sync for BattleGrid
# This script sets up automated syncing using cron

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CRON_SCHEDULE="*/30 * * * *"  # Every 30 minutes (adjust as needed)

echo "🔧 Setting up periodic BattleGrid database sync..."

# Make all scripts executable
chmod +x "$SCRIPT_DIR/auto_sync.sh"
chmod +x "$SCRIPT_DIR/sync_databases.sh"

# Create log directory
mkdir -p "$SCRIPT_DIR/logs"

# Create cron job entry
CRON_JOB="$CRON_SCHEDULE cd '$SCRIPT_DIR' && ./auto_sync.sh >> logs/sync.log 2>&1"

# Check if cron job already exists
if crontab -l 2>/dev/null | grep -q "auto_sync.sh"; then
    echo "⚠️  Cron job already exists. Updating..."
    # Remove existing job
    crontab -l 2>/dev/null | grep -v "auto_sync.sh" | crontab -
fi

# Add new cron job
(crontab -l 2>/dev/null; echo "$CRON_JOB") | crontab -

echo "✅ Periodic sync setup complete!"
echo ""
echo "📅 Schedule: Every 30 minutes"
echo "📁 Project directory: $SCRIPT_DIR"
echo "📊 Logs will be saved to: $SCRIPT_DIR/logs/sync.log"
echo ""
echo "🎛️  To customize the schedule, edit this script and change CRON_SCHEDULE"
echo "   Examples:"
echo "   - Every 15 minutes: '*/15 * * * *'"
echo "   - Every hour:       '0 * * * *'"
echo "   - Every 6 hours:    '0 */6 * * *'"
echo "   - Daily at 2 AM:    '0 2 * * *'"
echo ""
echo "📋 Current cron jobs:"
crontab -l 2>/dev/null | grep -v "^#"
echo ""
echo "🛠️  To view sync logs: tail -f $SCRIPT_DIR/logs/sync.log"
echo "🛑 To stop periodic sync: crontab -e (and remove the auto_sync line)" 