#!/bin/bash

# BattleGrid Database Sync Script
# Syncs data from all connected Android devices to laptop

PACKAGE_NAME="com.example.battlegrid"
LOCAL_DB_DIR="./synced_databases"
BACKUP_DIR="./db_backups/$(date +%Y%m%d_%H%M%S)"

echo "🚀 Starting BattleGrid Database Sync..."

# Create directories
mkdir -p "$LOCAL_DB_DIR"
mkdir -p "$BACKUP_DIR"

# Get list of connected devices
DEVICES=$(adb devices | grep -v "List of devices" | grep "device$" | cut -f1)

if [ -z "$DEVICES" ]; then
    echo "❌ No Android devices connected"
    exit 1
fi

echo "📱 Found devices: $(echo $DEVICES | wc -w)"

# Sync from each device
for DEVICE in $DEVICES; do
    echo "📲 Syncing from device: $DEVICE"
    
    DEVICE_DIR="$LOCAL_DB_DIR/device_$DEVICE"
    mkdir -p "$DEVICE_DIR"
    
    # Check if app is installed on primary user (user 0)
    if adb -s "$DEVICE" shell pm list packages --user 0 | grep -q "$PACKAGE_NAME"; then
        echo "✅ BattleGrid app found on $DEVICE (primary user)"
        
        # Force app to close to ensure data is checkpointed
        adb -s "$DEVICE" shell "am force-stop $PACKAGE_NAME" 2>/dev/null
        sleep 1
        
        # Pull all database files (main db, WAL, and SHM files)
        adb -s "$DEVICE" shell "run-as $PACKAGE_NAME cat /data/data/$PACKAGE_NAME/databases/battle_grid_database" > "$DEVICE_DIR/battle_grid_database" 2>/dev/null
        adb -s "$DEVICE" shell "run-as $PACKAGE_NAME cat /data/data/$PACKAGE_NAME/databases/battle_grid_database-wal" > "$DEVICE_DIR/battle_grid_database-wal" 2>/dev/null
        adb -s "$DEVICE" shell "run-as $PACKAGE_NAME cat /data/data/$PACKAGE_NAME/databases/battle_grid_database-shm" > "$DEVICE_DIR/battle_grid_database-shm" 2>/dev/null
        
        # If we have WAL files, checkpoint them into the main database
        if [ -s "$DEVICE_DIR/battle_grid_database-wal" ]; then
            echo "🔄 Checkpointing WAL data for $DEVICE"
            sqlite3 "$DEVICE_DIR/battle_grid_database" "PRAGMA wal_checkpoint(TRUNCATE);" 2>/dev/null || true
            # Clean up WAL files after checkpoint
            rm -f "$DEVICE_DIR/battle_grid_database-wal" "$DEVICE_DIR/battle_grid_database-shm" 2>/dev/null
        fi
        
        if [ -s "$DEVICE_DIR/battle_grid_database" ]; then
            echo "✅ Database synced from $DEVICE"
            
            # Backup the database
            cp "$DEVICE_DIR/battle_grid_database" "$BACKUP_DIR/device_${DEVICE}_$(date +%H%M%S).db"
            
            # Show data summary
            NINE_LINERS=$(sqlite3 "$DEVICE_DIR/battle_grid_database" "SELECT COUNT(*) FROM nine_liners;" 2>/dev/null || echo "0")
            USERS=$(sqlite3 "$DEVICE_DIR/battle_grid_database" "SELECT COUNT(*) FROM users;" 2>/dev/null || echo "0")
            echo "📊 Device $DEVICE: $NINE_LINERS requests, $USERS users"
        else
            echo "❌ Failed to sync database from $DEVICE"
        fi
    else
        echo "❌ BattleGrid app not installed on $DEVICE (primary user)"
        
        # Check if it might be on work profile
        echo "ℹ️  Note: Device has work profile - app might be installed there"
        echo "ℹ️  Work profile access requires additional permissions"
    fi
    
    echo "---"
done

echo "✅ Sync complete! Databases stored in: $LOCAL_DB_DIR"
echo "📦 Backups stored in: $BACKUP_DIR" 