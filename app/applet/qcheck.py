import os, json, sqlite3, urllib.request, urllib.error
print("URL:", os.environ.get('SUPABASE_URL', 'Not set'))
print("ANON:", os.environ.get('SUPABASE_ANON_KEY', 'Not set'))
try:
    conn = sqlite3.connect('eco_apartment.db')
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM app_settings")
    print("DB SETTINGS:", cursor.fetchall())
except Exception as e:
    print("DB ERROR:", e)
