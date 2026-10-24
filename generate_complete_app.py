import re

with open('index.html', 'r', encoding='utf-8') as f:
    html = f.read()

print("Original index.html loaded, len:", len(html))

# 1. Update Sidebar
sidebar_old_regex = r'<aside class="sidebar" id="sidebar">.*?</aside>'
sidebar_new = '''<aside class="sidebar" id="sidebar">
        <div class="sidebar-header">
            <div class="brand-logo">🌿</div>
            <div class="brand-info">
                <h1>EcoApartment OS</h1>
                <p>SRM Green Residency</p>
            </div>
        </div>

        <div class="nav-section-title">Facility Management</div>
        <ul class="nav-links">
            <li class="nav-item"><button class="active" onclick="switchPage('dashboard')"><span class="nav-icon">📊</span> Main Dashboard</button></li>
            <li class="nav-item"><button onclick="switchPage('directory')"><span class="nav-icon">👥</span> Resident & Family Directory <span class="nav-badge" id="residentCountBadge">400</span></button></li>
            <li class="nav-item"><button onclick="switchPage('matrix')"><span class="nav-icon">🏢</span> Block & Flat Matrix</button></li>
            <li class="nav-item"><button onclick="switchPage('parking')"><span class="nav-icon">🚗</span> Parking Allotments</button></li>
            <li class="nav-item"><button onclick="switchPage('cctv')"><span class="nav-icon">📹</span> CCTV Surveillance <span class="nav-badge" style="background:#EF4444;color:white;font-weight:800;">8 LIVE</span></button></li>
            <li class="nav-item"><button onclick="switchPage('complaints')"><span class="nav-icon">🛠️</span> Complaints & Helpdesk <span class="nav-badge" id="complaintsCountBadge">4</span></button></li>
            <li class="nav-item admin-only-ui"><button onclick="switchPage('technician')"><span class="nav-icon">👷</span> Technician Dispatch</button></li>
            <li class="nav-item"><button onclick="switchPage('dues')"><span class="nav-icon">💳</span> Maintenance & Dues</button></li>
            <li class="nav-item"><button onclick="switchPage('notices')"><span class="nav-icon">📢</span> Community Notices</button></li>
            <li class="nav-item"><button onclick="switchPage('visitors')"><span class="nav-icon">🛡️</span> Visitor Pass & Gate</button></li>
        </ul>

        <div class="nav-section-title">IoT & Sustainability</div>
        <ul class="nav-links">
            <li class="nav-item"><button onclick="switchPage('iot-hub')"><span class="nav-icon">🌱</span> IoT & Sustainability Hub</button></li>
        </ul>

        <div class="nav-section-title">Platform & Database</div>
        <ul class="nav-links">
            <li class="nav-item"><button onclick="switchPage('reports')"><span class="nav-icon">📑</span> Reports & Data Export</button></li>
            <li class="nav-item admin-only-ui"><button onclick="switchPage('supabase')"><span class="nav-icon">⚡</span> Supabase Cloud Sync</button></li>
        </ul>
    </aside>'''

html = re.sub(sidebar_old_regex, sidebar_new, html, flags=re.DOTALL)

# 2. Update Topbar
topbar_old_regex = r'<header class="topbar">.*?</header>'
topbar_new = '''<header class="topbar">
            <div class="topbar-left">
                <button class="menu-toggle" onclick="toggleSidebar()">☰</button>
                <div class="page-title-badge">
                    <span class="page-title" id="topbarTitle">Main Dashboard</span>
                </div>
                <div id="roleIndicatorBadge" style="margin-left: 14px;">
                    <span id="roleBadgeText" style="background: #E0E7FF; color: #3730A3; font-size: 11px; padding: 5px 12px; border-radius: 9999px; font-weight: 800; letter-spacing: 0.02em;">🔒 Resident Read-Only View (Flat A-101)</span>
                </div>
            </div>

            <div class="topbar-right">
                <div class="user-pill" style="cursor: pointer;" onclick="openAuthModal()" title="View session or switch user">
                    <div class="user-avatar" id="userAvatar">S</div>
                    <div class="user-details">
                        <div class="user-name" id="userNameDisplay">R. Shyam Sundar</div>
                        <div class="user-role" id="userRoleDisplay">Resident • Flat A-101</div>
                    </div>
                </div>
                <button type="button" onclick="openAuthModal()" class="btn btn-outline btn-sm" title="Safely authenticate with Username & Password" style="background: #ffffff; color: var(--primary); font-weight: 800; font-size: 11px; padding: 7px 12px; border-radius: 8px; border: 1.5px solid var(--primary);">
                    🔐 Switch User / Login
                </button>
                <button type="button" onclick="handleLogout()" class="btn btn-outline btn-sm" title="Log out of session" style="background: #FEF2F2; color: #DC2626; border-color: #FCA5A5; font-weight: 700; font-size: 11px; padding: 7px 12px; border-radius: 8px;">
                    🚪 Logout
                </button>
            </div>
        </header>'''

html = re.sub(topbar_old_regex, topbar_new, html, flags=re.DOTALL)

# 3. Update Quick Nav Bar
quicknav_old_regex = r'<nav class="quick-nav-bar"[^>]*>.*?</nav>'
quicknav_new = '''<nav class="quick-nav-bar" id="quickNavBar">
            <button class="quick-tab active" data-page="dashboard" onclick="switchPage('dashboard')">📊 Dashboard</button>
            <button class="quick-tab" data-page="directory" onclick="switchPage('directory')">👥 Directory & Family</button>
            <button class="quick-tab" data-page="matrix" onclick="switchPage('matrix')">🏢 Flat Matrix</button>
            <button class="quick-tab" data-page="parking" onclick="switchPage('parking')">🚗 Parking Allotments</button>
            <button class="quick-tab" data-page="cctv" onclick="switchPage('cctv')">📹 CCTV Surveillance</button>
            <button class="quick-tab" data-page="complaints" onclick="switchPage('complaints')">🛠️ Complaints</button>
            <button class="quick-tab" data-page="dues" onclick="switchPage('dues')">💳 Dues & Billing</button>
            <button class="quick-tab" data-page="notices" onclick="switchPage('notices')">📢 Notices</button>
            <button class="quick-tab" data-page="visitors" onclick="switchPage('visitors')">🛡️ Visitor Pass</button>
            <button class="quick-tab" data-page="iot-hub" onclick="switchPage('iot-hub')">🌱 IoT & Sustainability Hub</button>
            <button class="quick-tab" data-page="reports" onclick="switchPage('reports')">📑 Reports</button>
        </nav>'''

html = re.sub(quicknav_old_regex, quicknav_new, html, flags=re.DOTALL)

print("Navigation sections updated.")

with open('index.html', 'w', encoding='utf-8') as f:
    f.write(html)
print("Saved navigation update to index.html")
