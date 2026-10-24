with open('server.py', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Remove from do_GET
wrong_get = '''        elif path == "/api/profile/update":
            res = db.update_resident_full_profile(data)
            self.send_json(res)
        elif path == "/api/residents":'''
correct_get = '''        elif path == "/api/residents":'''

if wrong_get in text:
    text = text.replace(wrong_get, correct_get, 1)
    print("Removed misplaced /api/profile/update from do_GET")

# 2. Add to do_POST
old_post = '''        if path == "/api/login":
            user = db.authenticate(data.get("username", ""), data.get("password", ""), data.get("role", ""))
            if user:
                self.send_json({"success": True, "user": user})
            else:
                self.send_json({"success": False, "message": "Invalid username or password."})
        elif path == "/api/residents":'''

new_post = '''        if path == "/api/login":
            user = db.authenticate(data.get("username", ""), data.get("password", ""), data.get("role", ""))
            if user:
                self.send_json({"success": True, "user": user})
            else:
                self.send_json({"success": False, "message": "Invalid username or password."})
        elif path == "/api/profile/update":
            res = db.update_resident_full_profile(data)
            self.send_json(res)
        elif path == "/api/residents":'''

if old_post in text:
    text = text.replace(old_post, new_post, 1)
    print("Added /api/profile/update to do_POST successfully")
else:
    print("old_post not found, searching alternate...")
    idx = text.find('def do_POST(')
    print(text[idx:idx+500])

with open('server.py', 'w', encoding='utf-8') as f:
    f.write(text)
print("Updated server.py")
