import os
import re

# 1. Update server.py
with open("server.py", "r", encoding="utf-8") as f:
    server_code = f.read()

# Make sure server.py serves index.html from disk dynamically
old_serve = """        # 1. Web Application Single-Page Frontend
        if path == "/" or path == "/index.html" or path == "/app":
            self.send_response(200)
            self.send_header("Content-type", "text/html; charset=utf-8")
            self.end_headers()
            self.wfile.write(HTML_PAGE.encode("utf-8"))"""

new_serve = """        # 1. Web Application Single-Page Frontend
        if path == "/" or path == "/index.html" or path == "/app":
            self.send_response(200)
            self.send_header("Content-type", "text/html; charset=utf-8")
            self.end_headers()
            idx_file = os.path.join(os.path.dirname(__file__), "index.html")
            if os.path.exists(idx_file):
                with open(idx_file, "rb") as f_idx:
                    self.wfile.write(f_idx.read())
            else:
                self.wfile.write(HTML_PAGE.encode("utf-8"))"""

if old_serve in server_code:
    server_code = server_code.replace(old_serve, new_serve)
    print("Replaced serve logic in server.py")
else:
    print("old_serve not found in server.py, checking alternate")

# Update export endpoints in server.py
old_export = """        # 14. Export Residents CSV (All 400)
        elif path == "/api/export/residents.csv" or path == "/api/download/csv":
            self.send_response(200)
            self.send_header("Content-type", "text/csv; charset=utf-8")
            self.send_header("Content-Disposition", 'attachment; filename="SRM_Green_Residency_400_Residents.csv"')
            self.end_headers()
            
            with db.get_connection() as conn:
                cursor = conn.cursor()
                cursor.execute("SELECT flat_number, block, floor_number, resident_name, contact_number, email, occupancy_status, dues_status, vehicle_number FROM users WHERE role = 'RESIDENT' ORDER BY block, flat_number")
                rows = cursor.fetchall()
                csv_data = "Flat,Block,Floor,Resident Name,Contact Number,Email,Occupancy Status,Dues Status,Vehicle Number\\n"
                for r in rows:
                    csv_data += f'"{r[0]}","{r[1]}",{r[2]},"{r[3]}","{r[4]}","{r[5]}","{r[6]}","{r[7]}","{r[8]}"\\n'
                self.wfile.write(csv_data.encode("utf-8"))"""

new_export = """        # 14. Export Residents CSV (All 400 with Username, Password, Contact, Email)
        elif path == "/api/export/residents.csv" or path == "/api/download/csv":
            self.send_response(200)
            self.send_header("Content-type", "text/csv; charset=utf-8")
            self.send_header("Content-Disposition", 'attachment; filename="SRM_Green_Residency_400_Residents.csv"')
            self.end_headers()
            
            with db.get_connection() as conn:
                cursor = conn.cursor()
                cursor.execute("SELECT flat_number, block, floor_number, resident_name, username, password, contact_number, email, occupancy_status, dues_status, vehicle_number FROM users WHERE role = 'RESIDENT' ORDER BY block, flat_number")
                rows = cursor.fetchall()
                csv_data = "Flat,Block,Floor,Resident Name,Username,Password,Contact Number,Email,Occupancy Status,Dues Status,Vehicle Number\\n"
                for r in rows:
                    csv_data += f'"{r[0]}","{r[1]}",{r[2]},"{r[3]}","{r[4]}","{r[5]}","{r[6]}","{r[7]}","{r[8]}","{r[9]}","{r[10]}"\\n'
                self.wfile.write(csv_data.encode("utf-8"))

        # 14.5 Export Residents JSON (All 400 with Credentials)
        elif path == "/api/export/residents.json":
            self.send_response(200)
            self.send_header("Content-type", "application/json; charset=utf-8")
            self.send_header("Content-Disposition", 'attachment; filename="SRM_Green_Residency_400_Residents.json"')
            self.end_headers()
            
            with db.get_connection() as conn:
                cursor = conn.cursor()
                cursor.execute("SELECT flat_number, block, floor_number, resident_name, username, password, contact_number, email, occupancy_status, dues_status, vehicle_number FROM users WHERE role = 'RESIDENT' ORDER BY block, flat_number")
                rows = cursor.fetchall()
                res_list = []
                for r in rows:
                    res_list.append({
                        "flat_number": r[0], "block": r[1], "floor_number": r[2],
                        "resident_name": r[3], "username": r[4], "password": r[5],
                        "contact_number": r[6], "email": r[7], "occupancy_status": r[8],
                        "dues_status": r[9], "vehicle_number": r[10]
                    })
                self.wfile.write(json.dumps(res_list, indent=2).encode("utf-8"))

        # 14.6 Export Single Resident Profile (CSV, JSON, TXT)
        elif path.startswith("/api/export/resident/"):
            rest = path.replace("/api/export/resident/", "")
            parts = rest.rsplit(".", 1)
            flat_or_user = urllib.parse.unquote(parts[0])
            fmt = parts[1] if len(parts) > 1 else "csv"
            
            with db.get_connection() as conn:
                cursor = conn.cursor()
                cursor.execute("SELECT flat_number, block, floor_number, resident_name, username, password, contact_number, email, occupancy_status, dues_status, vehicle_number FROM users WHERE LOWER(flat_number) = ? OR LOWER(username) = ?", (flat_or_user.lower(), flat_or_user.lower()))
                r = cursor.fetchone()
                if not r:
                    self.send_error(404, "Resident not found")
                elif fmt == "json":
                    self.send_response(200)
                    self.send_header("Content-type", "application/json; charset=utf-8")
                    self.send_header("Content-Disposition", f'attachment; filename="resident_{flat_or_user}.json"')
                    self.end_headers()
                    data = {"flat_number": r[0], "block": r[1], "floor_number": r[2], "resident_name": r[3], "username": r[4], "password": r[5], "contact_number": r[6], "email": r[7], "occupancy_status": r[8], "dues_status": r[9], "vehicle_number": r[10]}
                    self.wfile.write(json.dumps(data, indent=2).encode("utf-8"))
                elif fmt == "txt":
                    self.send_response(200)
                    self.send_header("Content-type", "text/plain; charset=utf-8")
                    self.send_header("Content-Disposition", f'attachment; filename="resident_{flat_or_user}.txt"')
                    self.end_headers()
                    txt = f"=====================================================\\nECO-OS RESIDENTIAL PROFILE REPORT\\n=====================================================\\nFlat Number      : {r[0]}\\nBlock / Floor    : Block {r[1]}, Floor {r[2]}\\nResident Name    : {r[3]}\\nUsername (ID)    : {r[4]}\\nPassword         : {r[5]}\\nPhone Number     : {r[6]}\\nEmail Address    : {r[7]}\\nOccupancy Status : {r[8]}\\nDues Status      : {r[9]}\\nVehicle Number   : {r[10]}\\n=====================================================\\n"
                    self.wfile.write(txt.encode("utf-8"))
                else: # CSV
                    self.send_response(200)
                    self.send_header("Content-type", "text/csv; charset=utf-8")
                    self.send_header("Content-Disposition", f'attachment; filename="resident_{flat_or_user}.csv"')
                    self.end_headers()
                    csv_data = "Flat,Block,Floor,Resident Name,Username,Password,Contact Number,Email,Occupancy Status,Dues Status,Vehicle Number\\n"
                    csv_data += f'"{r[0]}","{r[1]}",{r[2]},"{r[3]}","{r[4]}","{r[5]}","{r[6]}","{r[7]}","{r[8]}","{r[9]}","{r[10]}"\\n'
                    self.wfile.write(csv_data.encode("utf-8"))"""

if old_export in server_code:
    server_code = server_code.replace(old_export, new_export)
    print("Replaced export endpoints in server.py")
else:
    print("old_export not found in server.py")

with open("server.py", "w", encoding="utf-8") as f:
    f.write(server_code)

print("server.py successfully updated!")
