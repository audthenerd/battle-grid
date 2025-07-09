#!/usr/bin/env python3

import sqlite3
import sys
from datetime import datetime
import argparse

class DataViewer:
    def __init__(self, db_path="master_battlegrid.db"):
        self.db_path = db_path
        
    def connect(self):
        try:
            return sqlite3.connect(self.db_path)
        except sqlite3.Error as e:
            print(f"❌ Database connection error: {e}")
            sys.exit(1)
    
    def show_summary(self):
        """Show overall database summary"""
        conn = self.connect()
        cursor = conn.cursor()
        
        print("📊 BattleGrid Master Database Summary")
        print("=" * 50)
        
        # Overall stats
        cursor.execute("SELECT COUNT(*) FROM users")
        total_users = cursor.fetchone()[0]
        
        cursor.execute("SELECT COUNT(*) FROM nine_liners")
        total_requests = cursor.fetchone()[0]
        
        cursor.execute("SELECT COUNT(DISTINCT source_device) FROM nine_liners WHERE source_device IS NOT NULL")
        device_count = cursor.fetchone()[0]
        
        print(f"👥 Total Users: {total_users}")
        print(f"📋 Total 9-Liner Requests: {total_requests}")
        print(f"📱 Active Devices: {device_count}")
        print()
        
        # Device breakdown
        cursor.execute("""
            SELECT source_device, COUNT(*) as request_count 
            FROM nine_liners 
            WHERE source_device IS NOT NULL 
            GROUP BY source_device 
            ORDER BY request_count DESC
        """)
        
        device_stats = cursor.fetchall()
        if device_stats:
            print("📱 Requests per Device:")
            for device, count in device_stats:
                print(f"   {device}: {count} requests")
            print()
        
        # Request type breakdown
        cursor.execute("""
            SELECT requestType, COUNT(*) as count 
            FROM nine_liners 
            GROUP BY requestType 
            ORDER BY count DESC
        """)
        
        request_types = cursor.fetchall()
        if request_types:
            print("📊 Request Types:")
            for req_type, count in request_types:
                print(f"   {req_type}: {count}")
            print()
        
        # Status breakdown
        cursor.execute("""
            SELECT status, COUNT(*) as count 
            FROM nine_liners 
            GROUP BY status 
            ORDER BY count DESC
        """)
        
        statuses = cursor.fetchall()
        if statuses:
            print("📈 Request Status:")
            for status, count in statuses:
                print(f"   {status}: {count}")
        
        conn.close()
    
    def show_recent_requests(self, limit=10):
        """Show recent 9-liner requests"""
        conn = self.connect()
        cursor = conn.cursor()
        
        cursor.execute("""
            SELECT requestType, line1, line5, status, createdAt, source_device 
            FROM nine_liners 
            ORDER BY createdAt DESC 
            LIMIT ?
        """, (limit,))
        
        requests = cursor.fetchall()
        
        print(f"🕒 Last {len(requests)} 9-Liner Requests")
        print("=" * 80)
        
        for req in requests:
            req_type, line1, line5, status, created_at, device = req
            created_date = datetime.fromtimestamp(created_at / 1000) if created_at else "Unknown"
            line5_short = (line5[:40] + "...") if len(line5) > 40 else line5
            
            print(f"📋 {req_type} | {status}")
            print(f"   IP/BP: {line1}")
            print(f"   Target: {line5_short}")
            print(f"   Device: {device} | Created: {created_date}")
            print("-" * 80)
        
        conn.close()
    
    def show_users(self):
        """Show all users"""
        conn = self.connect()
        cursor = conn.cursor()
        
        cursor.execute("""
            SELECT username, email, level, experience, createdAt, source_device 
            FROM users 
            ORDER BY createdAt DESC
        """)
        
        users = cursor.fetchall()
        
        print(f"👥 All Users ({len(users)})")
        print("=" * 60)
        
        for user in users:
            username, email, level, exp, created_at, device = user
            created_date = datetime.fromtimestamp(created_at / 1000) if created_at else "Unknown"
            
            print(f"👤 {username} ({email})")
            print(f"   Level: {level} | XP: {exp}")
            print(f"   Device: {device} | Joined: {created_date}")
            print("-" * 60)
        
        conn.close()
    
    def search_requests(self, search_term):
        """Search 9-liner requests"""
        conn = self.connect()
        cursor = conn.cursor()
        
        cursor.execute("""
            SELECT requestType, line1, line5, line6, status, createdAt, source_device 
            FROM nine_liners 
            WHERE line1 LIKE ? OR line5 LIKE ? OR line6 LIKE ? OR requestType LIKE ?
            ORDER BY createdAt DESC
        """, (f"%{search_term}%", f"%{search_term}%", f"%{search_term}%", f"%{search_term}%"))
        
        results = cursor.fetchall()
        
        print(f"🔍 Search Results for '{search_term}' ({len(results)} found)")
        print("=" * 80)
        
        for result in results:
            req_type, line1, line5, line6, status, created_at, device = result
            created_date = datetime.fromtimestamp(created_at / 1000) if created_at else "Unknown"
            
            print(f"📋 {req_type} | {status}")
            print(f"   IP/BP: {line1}")
            print(f"   Target: {line5}")
            print(f"   Grid: {line6}")
            print(f"   Device: {device} | Created: {created_date}")
            print("-" * 80)
        
        conn.close()

def main():
    parser = argparse.ArgumentParser(description="BattleGrid Database Viewer")
    parser.add_argument("--db", default="master_battlegrid.db", help="Database file path")
    
    subparsers = parser.add_subparsers(dest="command", help="Available commands")
    
    # Summary command
    subparsers.add_parser("summary", help="Show database summary")
    
    # Recent requests command
    recent_parser = subparsers.add_parser("recent", help="Show recent requests")
    recent_parser.add_argument("--limit", type=int, default=10, help="Number of requests to show")
    
    # Users command
    subparsers.add_parser("users", help="Show all users")
    
    # Search command
    search_parser = subparsers.add_parser("search", help="Search requests")
    search_parser.add_argument("term", help="Search term")
    
    args = parser.parse_args()
    
    if not args.command:
        parser.print_help()
        return
    
    viewer = DataViewer(args.db)
    
    if args.command == "summary":
        viewer.show_summary()
    elif args.command == "recent":
        viewer.show_recent_requests(args.limit)
    elif args.command == "users":
        viewer.show_users()
    elif args.command == "search":
        viewer.search_requests(args.term)

if __name__ == "__main__":
    main() 