with open("index.html", "r", encoding="utf-8") as f:
    html = f.read()

# 1. Remove from quick nav bar
old_quick_tab = '<button class="quick-tab" data-page="login" onclick="switchPage(\'login\')">🔑 Auth & Login</button>'
if old_quick_tab in html:
    html = html.replace(old_quick_tab + '\n            ', '')
    html = html.replace(old_quick_tab, '')
    print("Removed Auth & Login from quick nav bar")
else:
    print("quick tab not found as exact string")

# 2. Remove from sidebar nav links
old_side_item = '<li class="nav-item"><button onclick="switchPage(\'login\')"><span class="nav-icon">🔑</span> Session & Auth</button></li>'
if old_side_item in html:
    html = html.replace(old_side_item + '\n        ', '')
    html = html.replace(old_side_item, '')
    print("Removed Session & Auth from sidebar nav")
else:
    print("side item not found as exact string")

# 3. Clean up titles in page-login
html = html.replace('🔑 EcoApartment OS Authentication & Registration', '🔑 EcoApartment OS — Sign In Portal')
html = html.replace('🔑 EcoApartment OS Authentication', '🔑 EcoApartment OS — Sign In Portal')
html = html.replace('👤 Resident Tenant Login', '👤 Resident Sign In')

with open("index.html", "w", encoding="utf-8") as f:
    f.write(html)

print("Updated index.html successfully!")

# Also sync to server.py
with open("server.py", "r", encoding="utf-8") as f:
    srv = f.read()

start_marker = 'HTML_PAGE = """'
end_marker = '\n"""\n\n# =====================================================================\n# REQUEST HANDLER & REST API'

s_idx = srv.find(start_marker)
e_idx = srv.find(end_marker)

if s_idx != -1 and e_idx != -1:
    new_srv = srv[:s_idx + len(start_marker)] + html + srv[e_idx:]
    with open("server.py", "w", encoding="utf-8") as f_out:
        f_out.write(new_srv)
    print("Synced updated index.html to server.py HTML_PAGE!")
else:
    print("Could not find markers in server.py")

