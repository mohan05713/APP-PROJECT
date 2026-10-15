import os
import json
import sqlite3
import urllib.request
import urllib.error

def check_env():
    print("--- Environment Variables ---")
    print(f"SUPABASE_URL: {os.environ.get('SUPABASE_URL', 'Not set')}")
    print(f"SUPABASE_ANON_KEY: {'[HIDDEN]' if os.environ.get('SUPABASE_ANON_KEY') else 'Not set'}")
    print(f"SUPABASE_KEY: {'[HIDDEN]' if os.environ.get('SUPABASE_KEY') else 'Not set'}")

def check_db_settings():
    print("\n--- SQLite Database Settings ---")
    try:
        conn = sqlite3.connect('eco_apartment.db')
        conn.row_factory = sqlite3.Row
        cursor = conn.cursor()
        cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name='app_settings'")
        if cursor.fetchone():
            cursor.execute("SELECT * FROM app_settings")
            rows = cursor.fetchall()
            for r in rows:
                key = r['config_key']
                val = r['config_value']
                if 'KEY' in key or 'PASS' in key:
                    print(f"{key}: [HIDDEN]")
                else:
                    print(f"{key}: {val}")
        else:
            print("Table 'app_settings' does not exist.")
    except Exception as e:
        print(f"Error checking DB: {e}")

def test_supabase_connectivity(url, key=None):
    print(f"\n--- Testing Supabase Connectivity to {url} ---")
    if not key:
        print("No key provided, skipping REST API test.")
        return

    endpoint = f"{url.rstrip('/')}/rest/v1/residents?select=count"
    headers = {
        "apikey": key,
        "Authorization": f"Bearer {key}",
        "Content-Type": "application/json"
    }
    try:
        req = urllib.request.Request(endpoint, headers=headers)
        with urllib.request.urlopen(req, timeout=5) as response:
            print(f"Success! HTTP {response.getcode()}")
            print(f"Response: {response.read().decode('utf-8')}")
    except urllib.error.HTTPError as e:
        print(f"HTTP Error: {e.code} {e.reason}")
        print(f"Body: {e.read().decode('utf-8')}")
    except Exception as e:
        print(f"Connection failed: {e}")

if __name__ == "__main__":
    check_env()
    check_db_settings()
    
    provided_url = "https://yeabhmluvopehqcqwuox.supabase.co"
    # Try with env key if present
    env_key = os.environ.get('SUPABASE_ANON_KEY') or os.environ.get('SUPABASE_KEY')
    test_supabase_connectivity(provided_url, env_key)
