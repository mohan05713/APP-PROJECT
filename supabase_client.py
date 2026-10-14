"""
EcoApartment OS - Supabase Integration Client
Connects to Supabase PostgreSQL REST API with automatic fallback to local SQLite.
Supports Windows 11 / VS Code zero-configuration local execution and cloud sync.
"""

import os
import json
import urllib.request
import urllib.error
from typing import Dict, Any, Optional, List

class SupabaseClient:
    def __init__(self, url: Optional[str] = None, key: Optional[str] = None):
        # Read from params, environment, or default placeholders
        self.url = (url or os.environ.get("SUPABASE_URL", "")).rstrip("/")
        self.key = key or os.environ.get("SUPABASE_ANON_KEY", os.environ.get("SUPABASE_KEY", ""))
        self.is_configured = bool(self.url and self.key and "placeholder" not in self.url)

    def test_connection(self) -> Dict[str, Any]:
        """Tests the connection to Supabase Cloud REST API."""
        if not self.is_configured:
            return {
                "connected": False,
                "mode": "LOCAL_SQLITE",
                "message": "Local SQLite active (eco_apartment.db). Configure SUPABASE_URL and SUPABASE_ANON_KEY in Administrative Settings to link Supabase Cloud."
            }

        endpoint = f"{self.url}/rest/v1/app_settings?select=config_key&limit=1"
        headers = {
            "apikey": self.key,
            "Authorization": f"Bearer {self.key}",
            "Content-Type": "application/json"
        }

        try:
            req = urllib.request.Request(endpoint, headers=headers)
            with urllib.request.urlopen(req, timeout=5) as response:
                status = response.getcode()
                if status == 200:
                    return {
                        "connected": True,
                        "mode": "CLOUD_SUPABASE",
                        "status_code": status,
                        "message": "Successfully connected to Supabase PostgreSQL Database!"
                    }
                return {
                    "connected": False,
                    "mode": "LOCAL_SQLITE_FALLBACK",
                    "status_code": status,
                    "message": f"Supabase returned HTTP {status}"
                }
        except urllib.error.HTTPError as e:
            return {
                "connected": False,
                "mode": "LOCAL_SQLITE_FALLBACK",
                "status_code": e.code,
                "message": f"Supabase HTTP Error: {e.reason}"
            }
        except Exception as e:
            return {
                "connected": False,
                "mode": "LOCAL_SQLITE_FALLBACK",
                "message": f"Connection check failed: {str(e)}"
            }

    def fetch_table(self, table: str, params: Optional[Dict[str, str]] = None) -> Optional[List[Dict[str, Any]]]:
        """Fetches rows from a Supabase table using the REST API."""
        if not self.is_configured:
            return None

        query_str = ""
        if params:
            query_str = "?" + urllib.parse.urlencode(params)

        endpoint = f"{self.url}/rest/v1/{table}{query_str}"
        headers = {
            "apikey": self.key,
            "Authorization": f"Bearer {self.key}",
            "Content-Type": "application/json"
        }

        try:
            req = urllib.request.Request(endpoint, headers=headers)
            with urllib.request.urlopen(req, timeout=8) as response:
                if response.getcode() == 200:
                    return json.loads(response.read().decode("utf-8"))
        except Exception as e:
            print(f"[SupabaseClient] Error fetching {table}: {e}")
        return None

    def insert_row(self, table: str, data: Dict[str, Any]) -> bool:
        """Inserts a single row into Supabase table."""
        if not self.is_configured:
            return False

        endpoint = f"{self.url}/rest/v1/{table}"
        headers = {
            "apikey": self.key,
            "Authorization": f"Bearer {self.key}",
            "Content-Type": "application/json",
            "Prefer": "return=minimal"
        }

        try:
            body = json.dumps(data).encode("utf-8")
            req = urllib.request.Request(endpoint, data=body, headers=headers, method="POST")
            with urllib.request.urlopen(req, timeout=8) as response:
                return response.getcode() in [200, 201, 204]
        except Exception as e:
            print(f"[SupabaseClient] Error inserting into {table}: {e}")
            return False
