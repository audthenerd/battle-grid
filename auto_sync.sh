#!/bin/bash

# BattleGrid Auto Sync & Merge System
# Automatically syncs databases from connected devices and merges them

echo "🔄 BattleGrid Auto Sync Started - $(date)"

# Make scripts executable
chmod +x sync_databases.sh

# Step 1: Sync databases from all connected devices
echo "📱 Step 1: Syncing from connected devices..."
./sync_databases.sh

# Step 2: Merge all device databases into master database
echo "🔀 Step 2: Merging into master database..."
python3 merge_databases.py

# Step 3: Cleanup old backups (keep last 7 days)
echo "🧹 Step 3: Cleaning up old backups..."
find ./db_backups -type f -name "*.db" -mtime +7 -delete 2>/dev/null || true
find ./db_backups -type d -empty -delete 2>/dev/null || true

echo "✅ Auto sync completed - $(date)"
echo "---" 