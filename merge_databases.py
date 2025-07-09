#!/usr/bin/env python3

import sqlite3
import os
import glob
from datetime import datetime

class DatabaseMerger:
    def __init__(self, master_db_path="master_battlegrid.db"):
        self.master_db_path = master_db_path
        self.setup_master_database()
    
    def setup_master_database(self):
        """Create or update master database with all tables"""
        conn = sqlite3.connect(self.master_db_path)
        cursor = conn.cursor()
        
        # Create tables if they don't exist
        cursor.executescript("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT,
                email TEXT,
                avatar TEXT,
                level INTEGER DEFAULT 1,
                experience INTEGER DEFAULT 0,
                createdAt INTEGER,
                isActive INTEGER DEFAULT 1,
                source_device TEXT
            );
            
            CREATE TABLE IF NOT EXISTS nine_liners (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                requestType TEXT,
                line1 TEXT DEFAULT '',
                line2 TEXT DEFAULT '',
                line3 TEXT DEFAULT '',
                line4 TEXT DEFAULT '',
                line5 TEXT DEFAULT '',
                line6 TEXT DEFAULT '',
                line7 TEXT DEFAULT '',
                line8 TEXT DEFAULT '',
                line9 TEXT DEFAULT '',
                status TEXT DEFAULT 'DRAFT',
                createdAt INTEGER,
                transmittedAt INTEGER,
                createdBy INTEGER,
                source_device TEXT
            );
            
            CREATE TABLE IF NOT EXISTS battle_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER,
                name TEXT,
                description TEXT,
                mapData TEXT,
                startTime INTEGER,
                endTime INTEGER,
                isCompleted INTEGER DEFAULT 0,
                playerCount INTEGER DEFAULT 1,
                difficulty TEXT DEFAULT 'Normal',
                source_device TEXT
            );
            
            CREATE TABLE IF NOT EXISTS grid_positions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                sessionId INTEGER,
                entityName TEXT,
                entityType TEXT,
                xPosition INTEGER,
                yPosition INTEGER,
                health INTEGER DEFAULT 100,
                maxHealth INTEGER DEFAULT 100,
                isActive INTEGER DEFAULT 1,
                notes TEXT,
                updatedAt INTEGER,
                source_device TEXT
            );
        """)
        
        conn.commit()
        conn.close()
        print(f"✅ Master database initialized: {self.master_db_path}")
    
    def merge_device_database(self, device_db_path, device_id):
        """Merge data from a device database into master database"""
        if not os.path.exists(device_db_path):
            print(f"❌ Device database not found: {device_db_path}")
            return
        
        try:
            # Connect to both databases
            device_conn = sqlite3.connect(device_db_path)
            master_conn = sqlite3.connect(self.master_db_path)
            
            device_cursor = device_conn.cursor()
            master_cursor = master_conn.cursor()
            
            print(f"📲 Merging data from device: {device_id}")
            
            # Merge users
            device_cursor.execute("SELECT * FROM users")
            users = device_cursor.fetchall()
            
            for user in users:
                # Check if user already exists (by username and email)
                master_cursor.execute(
                    "SELECT id FROM users WHERE username = ? AND email = ? AND source_device = ?",
                    (user[1], user[2], device_id)
                )
                
                if not master_cursor.fetchone():
                    # Insert new user with source device
                    master_cursor.execute("""
                        INSERT INTO users (username, email, avatar, level, experience, createdAt, isActive, source_device)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """, (*user[1:], device_id))
                    print(f"  ✅ Added user: {user[1]}")
            
            # Merge nine_liners
            device_cursor.execute("SELECT * FROM nine_liners")
            nine_liners = device_cursor.fetchall()
            
            for request in nine_liners:
                # Check if request already exists (by content hash)
                content_hash = f"{request[1]}_{request[2]}_{request[6]}_{request[11]}"  # requestType + line1 + line6 + createdAt
                master_cursor.execute(
                    "SELECT id FROM nine_liners WHERE requestType = ? AND line1 = ? AND line6 = ? AND createdAt = ? AND source_device = ?",
                    (request[1], request[2], request[6], request[11], device_id)
                )
                
                if not master_cursor.fetchone():
                    # Insert new request with source device
                    master_cursor.execute("""
                        INSERT INTO nine_liners (requestType, line1, line2, line3, line4, line5, line6, line7, line8, line9, status, createdAt, transmittedAt, createdBy, source_device)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """, (*request[1:], device_id))
                    print(f"  ✅ Added 9-liner: {request[1]} - {request[5][:30]}...")
            
            # Merge battle_sessions and grid_positions similarly...
            # (Adding basic structure for now)
            
            master_conn.commit()
            
            # Show statistics
            master_cursor.execute("SELECT COUNT(*) FROM users WHERE source_device = ?", (device_id,))
            user_count = master_cursor.fetchone()[0]
            
            master_cursor.execute("SELECT COUNT(*) FROM nine_liners WHERE source_device = ?", (device_id,))
            request_count = master_cursor.fetchone()[0]
            
            print(f"  📊 Device {device_id}: {user_count} users, {request_count} requests")
            
            device_conn.close()
            master_conn.close()
            
        except Exception as e:
            print(f"❌ Error merging device {device_id}: {str(e)}")
    
    def merge_all_devices(self, synced_db_dir="./synced_databases"):
        """Merge databases from all synced devices"""
        if not os.path.exists(synced_db_dir):
            print(f"❌ Synced databases directory not found: {synced_db_dir}")
            return
        
        device_dirs = glob.glob(f"{synced_db_dir}/device_*")
        
        if not device_dirs:
            print("❌ No device databases found")
            return
        
        print(f"🔄 Merging {len(device_dirs)} device databases...")
        
        for device_dir in device_dirs:
            device_id = os.path.basename(device_dir).replace('device_', '')
            db_path = os.path.join(device_dir, 'battle_grid_database')
            
            self.merge_device_database(db_path, device_id)
        
        print("✅ All databases merged successfully!")
        
        # Show final statistics
        conn = sqlite3.connect(self.master_db_path)
        cursor = conn.cursor()
        
        cursor.execute("SELECT COUNT(*) FROM users")
        total_users = cursor.fetchone()[0]
        
        cursor.execute("SELECT COUNT(*) FROM nine_liners")
        total_requests = cursor.fetchone()[0]
        
        cursor.execute("SELECT COUNT(DISTINCT source_device) FROM nine_liners")
        device_count = cursor.fetchone()[0]
        
        print(f"📊 Master Database Summary:")
        print(f"   👥 Total Users: {total_users}")
        print(f"   📋 Total 9-Liner Requests: {total_requests}")
        print(f"   📱 Devices: {device_count}")
        
        conn.close()

if __name__ == "__main__":
    merger = DatabaseMerger()
    merger.merge_all_devices() 