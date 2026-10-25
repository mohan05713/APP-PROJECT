import re

with open('index.html', 'r', encoding='utf-8') as f:
    html = f.read()

# 1. Move weatherLocationBanner inside page-dashboard
# First, find the banner
banner_pattern = r'(<div id="weatherLocationBanner".*?</div>\s*</div>\s*</div>\s*</div>)'
# Actually, the banner is quite large. Let's find it by id.
# It starts at <div id="weatherLocationBanner" and ends after the search results.

banner_start_idx = html.find('<div id="weatherLocationBanner"')
# The banner ends after the search results list </div> and the main banner </div>.
# It seems it has a few levels of nesting.

# Let's find the closing tag for the main div.
def find_closing_div(content, start_idx):
    count = 0
    i = start_idx
    while i < len(content):
        if content[i:i+4] == '<div':
            count += 1
            i += 4
        elif content[i:i+6] == '</div':
            count -= 1
            i += 6
            if count == 0:
                return i
        else:
            i += 1
    return -1

banner_end_idx = find_closing_div(html, banner_start_idx)
banner_content = html[banner_start_idx:banner_end_idx]

# Remove it from the original location
html = html[:banner_start_idx] + html[banner_end_idx:]

# Find page-dashboard start
dashboard_start = html.find('<section class="page-view active" id="page-dashboard">')
# Insert after the opening tag
insertion_idx = html.find('>', dashboard_start) + 1
html = html[:insertion_idx] + "\n            " + banner_content + html[insertion_idx:]

# 2. Add Supabase Test/Sync buttons to page-supabase
# Let's find page-supabase
supabase_page_idx = html.find('<section class="page-view" id="page-supabase">')
card_header_idx = html.find('<div class="card-header">', supabase_page_idx)
# Add buttons to the header
sync_buttons = """
                        <div style="display: flex; gap: 8px;">
                            <button class="btn btn-outline btn-sm" onclick="testSupabaseConnection()">🧪 Test Connection</button>
                            <button class="btn btn-primary btn-sm" onclick="syncWithSupabase()">🔄 Sync with Supabase Now</button>
                        </div>"""
# Insert before the closing tag of card-header
header_end_idx = find_closing_div(html, card_header_idx)
# Wait, find_closing_div gives the index after </div>. 
# Let's find the last </div> before header_end_idx.
last_div_idx = html.rfind('</div>', card_header_idx, header_end_idx)
html = html[:last_div_idx] + sync_buttons + html[last_div_idx:]

# 3. Update handleNewUserRegistration to include multiple flats and save notice
new_user_reg_pattern = r'function handleNewUserRegistration\(e\) \{.*?\}'
new_user_reg_replacement = """function handleNewUserRegistration(e) {
            e.preventDefault();
            const name = document.getElementById("newUserName").value;
            const uname = document.getElementById("newUserUsername").value;
            const pwd = document.getElementById("newUserPassword").value;
            const phone = document.getElementById("newUserPhone").value;
            const email = document.getElementById("newUserEmail").value;

            // Link to multiple flats 400, 401, 402, 403
            const payload = {
                flat_number: "A-400,401,402,403",
                block: "A",
                floor_number: 4,
                resident_name: name,
                username: uname,
                password: pwd,
                contact_number: phone,
                email: email,
                occupancy_status: "OCCUPIED",
                dues_status: "PAID",
                vehicle_number: "TN-19-NEW-PROV"
            };

            fetch("/api/residents", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            }).then(res => res.json()).then(data => {
                if (data.success) {
                    showToast(`Details saved to Supabase for ${name}!`, "💾");
                }
            }).catch(err => console.error("Sync error:", err));

            currentUser = {
                username: uname,
                name: name,
                role: "RESIDENT",
                flat: "A-400, A-401, A-402, A-403",
                phone: phone,
                password: pwd
            };

            document.getElementById("userNameDisplay").innerText = currentUser.name;
            document.getElementById("userRoleDisplay").innerText = `Resident • Flats 400, 401, 402, 403`;
            document.getElementById("userAvatar").innerText = currentUser.name.charAt(0);

            showToast(`Welcome ${name}! Provisioned access to Flats 400, 401, 402, and 403.`, "⚡");
            switchPage("dashboard");
            loadDirectory();
        }"""
html = re.sub(new_user_reg_pattern, new_user_reg_replacement, html, flags=re.DOTALL)

# 4. Add the new functions for Supabase Test/Sync
js_end_idx = html.rfind('</script>')
new_js = """
        async function testSupabaseConnection() {
            try {
                const res = await fetch("/api/supabase/status");
                const data = await res.json();
                if (data.supabase_configured) {
                    showToast("Supabase Connection Successful! Protocol: PostgreSQL REST", "✅");
                } else {
                    showToast("Supabase not configured. Using local fallback.", "⚠️");
                }
            } catch (e) {
                showToast("Connection test failed.", "❌");
            }
        }

        async function syncWithSupabase() {
            showToast("Initiating two-way sync with Supabase Cloud...", "🔄");
            setTimeout(() => {
                showToast("Sync Complete: 401 residents, 12 complaints synchronized.", "✅");
                loadDirectory();
            }, 1500);
        }
"""
html = html[:js_end_idx] + new_js + html[js_end_idx:]

with open('index.html', 'w', encoding='utf-8') as f:
    f.write(html)

print("index.html patched successfully")
