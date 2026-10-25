import re

with open('server.py', 'r', encoding='utf-8') as f:
    code = f.read()

# 1. ADD CSS STYLES FOR QUICK NAV BAR & OVERLAY
css_to_add = """
        /* QUICK NAV TAB BAR FOR ALL SCREENS */
        .quick-nav-bar {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 10px 20px;
            background: #FFFFFF;
            border-bottom: 1px solid var(--border-light);
            overflow-x: auto;
            white-space: nowrap;
            -webkit-overflow-scrolling: touch;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
            z-index: 100;
        }
        .quick-nav-bar::-webkit-scrollbar {
            height: 4px;
        }
        .quick-nav-bar::-webkit-scrollbar-thumb {
            background: #CBD5E1;
            border-radius: 4px;
        }
        .quick-tab {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 6px 14px;
            border-radius: 20px;
            background: var(--bg-page);
            border: 1px solid var(--border-light);
            color: var(--text-body);
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s ease;
            flex-shrink: 0;
            text-decoration: none;
        }
        .quick-tab:hover {
            background: #E2E8F0;
            border-color: #94A3B8;
        }
        .quick-tab.active {
            background: var(--primary);
            color: #FFFFFF;
            border-color: var(--primary-dark);
            box-shadow: 0 2px 4px rgba(15, 118, 110, 0.25);
        }
        .sidebar-overlay {
            display: none;
            position: fixed;
            top: 0; left: 0; right: 0; bottom: 0;
            background: rgba(15, 23, 42, 0.5);
            z-index: 998;
        }
        .sidebar.open ~ .sidebar-overlay {
            display: block;
        }
"""

if ".quick-nav-bar" not in code:
    code = code.replace("/* RESPONSIVE DESIGN */", css_to_add + "\n        /* RESPONSIVE DESIGN */")

# 2. ADD QUICK NAV BAR HTML RIGHT AFTER HEADER
topbar_html = """        </header>
        <!-- QUICK NAV TAB BAR -->
        <nav class="quick-nav-bar" id="quickNavBar">
            <button class="quick-tab active" data-page="dashboard" onclick="switchPage('dashboard')">📊 Main Dashboard</button>
            <button class="quick-tab" data-page="login" onclick="switchPage('login')">🔑 Auth & Login</button>
            <button class="quick-tab" data-page="directory" onclick="switchPage('directory')">👥 Resident Directory (400)</button>
            <button class="quick-tab" data-page="matrix" onclick="switchPage('matrix')">🏢 Block & Flat Matrix</button>
            <button class="quick-tab" data-page="complaints" onclick="switchPage('complaints')">🛠️ Complaints Desk</button>
            <button class="quick-tab" data-page="technician" onclick="switchPage('technician')">👷 Technicians</button>
            <button class="quick-tab" data-page="dues" onclick="switchPage('dues')">💳 Dues & Billing</button>
            <button class="quick-tab" data-page="notices" onclick="switchPage('notices')">📢 Notice Board</button>
            <button class="quick-tab" data-page="visitors" onclick="switchPage('visitors')">🛡️ Visitor Pass</button>
            <button class="quick-tab" data-page="water" onclick="switchPage('water')">💧 Water IoT</button>
            <button class="quick-tab" data-page="reports" onclick="switchPage('reports')">📑 Reports & Exports</button>
            <a href="/app-debug.apk" download class="quick-tab" style="background: var(--primary-light); color: var(--primary-dark); font-weight: 700; border: 1px solid var(--primary);">📱 Download Android APK</a>
        </nav>"""

if 'id="quickNavBar"' not in code:
    code = code.replace("</header>", topbar_html)

# 3. ENHANCE PAGE-LOGIN WITH 1-TAP QUICK LOGIN CARDS
login_card_enhanced = """            <!-- PAGE 16: SESSION & AUTHENTICATION -->
            <div class="page-view" id="page-login">
                <!-- 1-TAP INSTANT ACCESS CARD -->
                <div class="card" style="max-width: 580px; margin: 20px auto 30px auto; background: linear-gradient(135deg, #F0FDFA, #E0F2FE); border: 2px solid var(--primary); box-shadow: var(--shadow-md);">
                    <div class="card-header" style="border-bottom: 1px solid rgba(15, 118, 110, 0.15);">
                        <div class="card-title" style="color: var(--primary-dark); font-size: 18px; display: flex; align-items: center; gap: 8px;">
                            <span>⚡ 1-Tap Instant Web App Login (Sample Data Pre-Loaded)</span>
                        </div>
                    </div>
                    <div style="padding: 16px; display: flex; flex-direction: column; gap: 12px;">
                        <button class="btn btn-primary" onclick="quickLoginResident()" style="width: 100%; font-size: 15px; padding: 12px 16px; display: flex; align-items: center; justify-content: center; gap: 10px;">
                            <span>🏠</span> <strong>Instant Login as Resident (Mohan Palaniappan • Flat B-402)</strong>
                        </button>
                        <button class="btn" onclick="quickLoginAdmin()" style="width: 100%; font-size: 15px; padding: 12px 16px; background: #0F172A; color: #FFFFFF; display: flex; align-items: center; justify-content: center; gap: 10px;">
                            <span>🛡️</span> <strong>Instant Login as Authority Desk (Admin Office)</strong>
                        </button>
                        <button class="btn" onclick="quickLoginTech()" style="width: 100%; font-size: 15px; padding: 12px 16px; background: #0284C7; color: #FFFFFF; display: flex; align-items: center; justify-content: center; gap: 10px;">
                            <span>👷</span> <strong>Instant Login as Plumbing Technician (R. Kathir)</strong>
                        </button>
                    </div>
                </div>

                <div class="card" style="max-width: 580px; margin: 0 auto;">
                    <div class="card-header">
                        <div class="card-title">🔑 Manual Credentials Sign-In</div>
                    </div>
                    <form onsubmit="handleManualLogin(event)">
                        <div class="form-group" style="margin-bottom: 14px;">
                            <label class="form-label">Role</label>
                            <select class="form-control" id="loginRoleSelect">
                                <option value="RESIDENT" selected>Resident</option>
                                <option value="AUTHORITY">Authority / Admin</option>
                                <option value="TECHNICIAN">Technician</option>
                            </select>
                        </div>
                        <div class="form-group" style="margin-bottom: 14px;">
                            <label class="form-label">Username, Flat Number, or Mobile</label>
                            <input type="text" class="form-control" id="loginUsername" value="flat_402" placeholder="e.g. flat_402, B-402, or 6380244185" required>
                        </div>
                        <div class="form-group" style="margin-bottom: 20px;">
                            <label class="form-label">Password</label>
                            <input type="password" class="form-control" id="loginPassword" value="pass123" placeholder="Enter password" required>
                        </div>
                        <button type="submit" class="btn btn-primary" style="width: 100%;">Sign In to Web App</button>
                    </form>
                </div>
            </div>"""

login_pattern = r'<!-- PAGE 16: SESSION & AUTHENTICATION -->\s*<div class="page-view" id="page-login">.*?</div>\s*</div>'
if "quickLoginResident" not in code:
    code = re.sub(login_pattern, login_card_enhanced, code, flags=re.DOTALL)

# 4. ENHANCE JS FUNCTIONS FOR SWITCHPAGE & QUICK LOGINS
js_quick_logins = """
        function quickLoginResident() {
            handleProfileSwitch('flat_402');
            const sel = document.getElementById("profileSwitcher");
            if (sel) sel.value = "flat_402";
            switchPage('dashboard');
            showToast("Logged in as Resident: Mohan Palaniappan (Flat B-402)", "🏠");
        }

        function quickLoginAdmin() {
            handleProfileSwitch('admin');
            const sel = document.getElementById("profileSwitcher");
            if (sel) sel.value = "admin";
            switchPage('dashboard');
            showToast("Logged in as Authority Desk (Admin Office)", "🛡️");
        }

        function quickLoginTech() {
            handleProfileSwitch('tech_plumber');
            const sel = document.getElementById("profileSwitcher");
            if (sel) sel.value = "tech_plumber";
            switchPage('technician');
            showToast("Logged in as Technician: R. Kathir (Plumbing)", "👷");
        }
"""

if "function quickLoginResident" not in code:
    code = code.replace("function handleProfileSwitch", js_quick_logins + "\n        function handleProfileSwitch")

# Update switchPage to also sync quick-tabs
old_switch = """        function switchPage(pageId) {
            document.querySelectorAll(".page-view").forEach(p => p.classList.remove("active"));
            const target = document.getElementById("page-" + pageId);
            if (target) target.classList.add("active");

            // Update sidebar buttons
            document.querySelectorAll(".nav-item button").forEach(b => {
                b.classList.remove("active");
                if (b.getAttribute("onclick") && b.getAttribute("onclick").includes("'" + pageId + "'")) {
                    b.classList.add("active");
                }
            });"""

new_switch = """        function switchPage(pageId) {
            document.querySelectorAll(".page-view").forEach(p => p.classList.remove("active"));
            const target = document.getElementById("page-" + pageId);
            if (target) target.classList.add("active");

            // Update sidebar buttons
            document.querySelectorAll(".nav-item button").forEach(b => {
                b.classList.remove("active");
                if (b.getAttribute("onclick") && b.getAttribute("onclick").includes("'" + pageId + "'")) {
                    b.classList.add("active");
                }
            });

            // Update quick nav bar tabs
            document.querySelectorAll(".quick-tab").forEach(t => {
                t.classList.remove("active");
                if (t.dataset.page === pageId) {
                    t.classList.add("active");
                }
            });"""

code = code.replace(old_switch, new_switch)

# 5. PRE-FILL MODALS WITH RICH SAMPLE DATA WHEN OPENED
old_modals_js = """        function openRaiseComplaintModal() {
            document.getElementById("cmpFlat").value = currentUser.flat;
            document.getElementById("modalComplaint").classList.add("active");
        }"""

new_modals_js = """        function openRaiseComplaintModal() {
            document.getElementById("cmpFlat").value = currentUser.flat || "B-402";
            document.getElementById("cmpCategory").value = "PLUMBING";
            document.getElementById("cmpPriority").value = "HIGH";
            document.getElementById("cmpFloor").value = "4";
            document.getElementById("cmpDesc").value = "Low water pressure in kitchen pipeline during peak morning hours (7:00 AM - 9:00 AM). Needs valve inspection.";
            document.getElementById("modalComplaint").classList.add("active");
        }"""

code = code.replace(old_modals_js, new_modals_js)

old_notice_js = """        function openPostNoticeModal() {
            document.getElementById("modalNotice").classList.add("active");
        }"""

new_notice_js = """        function openPostNoticeModal() {
            document.getElementById("notTitle").value = "Block B Main Overhead Tank Cleaning & Plumbing Audit";
            document.getElementById("notCategory").value = "MAINTENANCE";
            document.getElementById("notPriority").value = "IMPORTANT";
            document.getElementById("notDesc").value = "Annual overhead water tank sanitization and pipeline pressure audit scheduled for Saturday from 10:00 AM to 2:00 PM. Water supply will be temporarily paused.";
            document.getElementById("modalNotice").classList.add("active");
        }"""

code = code.replace(old_notice_js, new_notice_js)

old_gatepass_js = """        function openGatePassModal() {
            document.getElementById("visFlat").value = currentUser.flat;
            document.getElementById("modalGatePass").classList.add("active");
        }"""

new_gatepass_js = """        function openGatePassModal() {
            document.getElementById("visFlat").value = currentUser.flat || "B-402";
            document.getElementById("visName").value = "Suresh Kumar (Swiggy Delivery)";
            document.getElementById("visPhone").value = "+91 98401 55210";
            document.getElementById("visType").value = "DELIVERY";
            document.getElementById("visVehicle").value = "TN-09-CB-4892";
            document.getElementById("visPurpose").value = "Food order delivery for Flat B-402";
            document.getElementById("modalGatePass").classList.add("active");
        }"""

code = code.replace(old_gatepass_js, new_gatepass_js)

old_res_js = """        function openAddResidentModal() {
            document.getElementById("modalResidentTitle").innerText = "➕ Add New Resident";
            document.getElementById("resFlat").value = "";
            document.getElementById("resName").value = "";
            document.getElementById("resPhone").value = "";
            document.getElementById("resEmail").value = "";
            document.getElementById("modalResident").classList.add("active");
        }"""

new_res_js = """        function openAddResidentModal() {
            document.getElementById("modalResidentTitle").innerText = "➕ Add New Resident";
            document.getElementById("resFlat").value = "B-403";
            document.getElementById("resName").value = "Ananya Krishnan";
            document.getElementById("resPhone").value = "+91 94440 12890";
            document.getElementById("resEmail").value = "ananya.k@gmail.com";
            document.getElementById("resBlock").value = "B";
            document.getElementById("resFloor").value = "4";
            document.getElementById("resOccupancy").value = "OWNER";
            document.getElementById("resMembers").value = "3";
            document.getElementById("modalResident").classList.add("active");
        }"""

code = code.replace(old_res_js, new_res_js)

with open('server.py', 'w', encoding='utf-8') as f:
    f.write(code)

print("Successfully patched server.py with Quick Nav Bar, 1-Tap Login, and Pre-filled Sample Data Modals!")
