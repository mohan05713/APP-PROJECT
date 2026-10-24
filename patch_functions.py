with open('index.html', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Update loadDues
old_dues = 'async function loadDues() {\n            try {\n                const res = await fetch("/api/dues");'
new_dues = '''async function loadDues() {
            try {
                const isAdmin = (currentUser && (currentUser.role === 'AUTHORITY' || currentUser.role === 'ADMIN' || currentUser.username === 'admin'));
                const url = isAdmin ? "/api/dues" : `/api/dues?flat=${encodeURIComponent(currentUser.flat || 'A-101')}`;
                const res = await fetch(url);'''

if old_dues in text:
    text = text.replace(old_dues, new_dues)
    print("Patched loadDues for role-based filtering")
else:
    print("old_dues not found")

# 2. Update loadVisitors
old_vis = 'async function loadVisitors() {\n            try {\n                const res = await fetch("/api/visitors");'
new_vis = '''async function loadVisitors() {
            try {
                const isAdmin = (currentUser && (currentUser.role === 'AUTHORITY' || currentUser.role === 'ADMIN' || currentUser.username === 'admin'));
                const url = isAdmin ? "/api/visitors" : `/api/visitors?flat=${encodeURIComponent(currentUser.flat || 'A-101')}`;
                const res = await fetch(url);'''

if old_vis in text:
    text = text.replace(old_vis, new_vis)
    print("Patched loadVisitors for role-based filtering")
else:
    print("old_vis not found")

# 3. Update switchPage to include parking, cctv, iot-hub
old_titles = 'reports: "Reports & Data Exports",'
new_titles = '''parking: "Parking Allotments & EV Charging Bays",
                cctv: "24/7 CCTV Surveillance System",
                "iot-hub": "IoT & Sustainability Telemetry Hub",
                reports: "Reports & Data Exports",'''

if old_titles in text:
    text = text.replace(old_titles, new_titles, 1)
    print("Patched switchPage titles")
else:
    print("old_titles not found")

# 4. In switchPage, add loader triggers
old_switch_end = 'document.getElementById("topbarTitle").innerText = titles[pageId] || "EcoApartment OS";'
new_switch_end = '''document.getElementById("topbarTitle").innerText = titles[pageId] || "EcoApartment OS";
            if (pageId === "parking") loadParking();
            if (pageId === "cctv") loadCCTV();
            if (pageId === "dues") loadDues();
            if (pageId === "visitors") loadVisitors();
            if (pageId === "directory") loadDirectory();
            if (pageId === "iot-hub") loadTelemetry();'''

if old_switch_end in text:
    text = text.replace(old_switch_end, new_switch_end, 1)
    print("Patched switchPage loader triggers")
else:
    print("old_switch_end not found")

# 5. Update initial currentUser to R. Shyam Sundar (Flat A-101)
old_user = '''        // Global App State
        let currentUser = {
            username: "flat_402",
            name: "Mohan Palaniappan",
            role: "RESIDENT",
            flat: "B-402",
            phone: "+91 6380244185"
        };'''

new_user = '''        // Global App State - Default: R. Shyam Sundar (Flat A-101)
        let currentUser = {
            username: "flat_101",
            name: "R. Shyam Sundar",
            role: "RESIDENT",
            flat: "A-101",
            phone: "+91 77089 94065"
        };'''

if old_user in text:
    text = text.replace(old_user, new_user, 1)
    print("Updated default currentUser to R. Shyam Sundar (Flat A-101)")

# 6. Add Family button to directory table rows
old_action_btn = 'onclick="editResident(\'${r.flat_number}\')" title="Edit Resident Info">✏️</button>'
new_action_btn = '''onclick="openFamilyModal(\'${r.flat_number}\')" title="View Family Members">👨‍👩‍👧‍👦 Family</button>
                            <button type="button" class="btn btn-outline btn-sm admin-only-ui" onclick="editResident(\'${r.flat_number}\')" title="Edit Resident Info">✏️</button>'''

if old_action_btn in text:
    text = text.replace(old_action_btn, new_action_btn)
    print("Added Family button to directory rows")

with open('index.html', 'w', encoding='utf-8') as f:
    f.write(text)
print("Saved patched functions to index.html")
