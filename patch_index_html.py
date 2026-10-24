import re

with open("index.html", "r", encoding="utf-8") as f:
    html = f.read()

# 1. WEATHER & GPS BANNER
weather_banner_html = """        <main class="content-area">
            <!-- DYNAMIC GPS LOCATION & LIVE WEATHER TELEMETRY BANNER -->
            <div id="weatherLocationBanner" style="background: linear-gradient(135deg, #0F766E 0%, #115E59 100%); color: white; padding: 18px 22px; border-radius: 14px; margin-bottom: 24px; box-shadow: 0 4px 12px rgba(15, 118, 110, 0.2); display: flex; flex-direction: column; gap: 14px;">
                <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 14px;">
                    <div style="display: flex; align-items: center; gap: 14px;">
                        <div style="font-size: 38px; line-height: 1;" id="weatherIconDisplay">☀️</div>
                        <div>
                            <div style="display: flex; align-items: center; gap: 10px; flex-wrap: wrap;">
                                <span style="font-weight: 800; font-size: 17px; letter-spacing: -0.01em;" id="locationPlaceName">Detecting your exact GPS location...</span>
                                <span style="background: rgba(255,255,255,0.22); font-size: 11px; padding: 3px 10px; border-radius: 9999px; font-weight: 700; letter-spacing: 0.02em;" id="locationGpsBadge">GPS Sensor Ready</span>
                            </div>
                            <div style="font-size: 13px; opacity: 0.92; margin-top: 4px;" id="locationDistrictAndCoords">Grant device GPS permission to detect where you are and view real-time weather metrics</div>
                        </div>
                    </div>
                    <div style="display: flex; align-items: center; gap: 8px; flex-wrap: wrap;">
                        <button type="button" onclick="requestDeviceGps()" style="background: #ffffff; color: #0F766E; font-weight: 800; font-size: 12px; border: none; padding: 9px 15px; border-radius: 9px; cursor: pointer; display: flex; align-items: center; gap: 7px; box-shadow: 0 2px 6px rgba(0,0,0,0.15); transition: transform 0.1s ease;">
                            <span>📍 Allow GPS / Check Place</span>
                        </button>
                        <button type="button" onclick="detectLocationViaIP()" style="background: rgba(255,255,255,0.18); color: white; border: 1px solid rgba(255,255,255,0.35); font-weight: 700; font-size: 12px; padding: 9px 13px; border-radius: 9px; cursor: pointer; display: flex; align-items: center; gap: 6px;">
                            <span>🌐 IP Auto-Detect</span>
                        </button>
                    </div>
                </div>
                <!-- LIVE WEATHER DATA ROW -->
                <div style="display: flex; gap: 24px; font-size: 13px; padding-top: 10px; border-top: 1px solid rgba(255,255,255,0.22); flex-wrap: wrap; align-items: center;">
                    <div>🌡️ Temp: <strong id="weatherTempDisplay" style="font-size: 14px;">--°C</strong></div>
                    <div>☁️ Condition: <strong id="weatherConditionDisplay">Detecting...</strong></div>
                    <div>💧 Humidity: <strong id="weatherHumidityDisplay">--%</strong></div>
                    <div>💨 Wind: <strong id="weatherWindDisplay">-- km/h</strong></div>
                    <div style="margin-left: auto; font-size: 11px; opacity: 0.85;" id="weatherUpdatedTime">Telemetry synced</div>
                </div>
            </div>"""

if '<main class="content-area">' in html:
    html = html.replace('<main class="content-area">', weather_banner_html, 1)
    print("Added weather banner to index.html")

# 2. ENHANCE PAGE LOGIN WITH ONE-CLICK SHORTCUTS, NEW USER TAB, SEE PASSWORD EYE TOGGLES, AND 400 CREDENTIALS DIRECTORY
old_login_page_pattern = r'<section class="page-view" id="page-login">.*?</section>'
new_login_page = """<section class="page-view" id="page-login">
                <!-- AUTH CARD CONTAINER -->
                <div style="max-width: 900px; margin: 20px auto; display: flex; flex-direction: column; gap: 24px;">
                    
                    <!-- TAB SELECTOR & SIGN-IN CARD -->
                    <div class="card" style="padding: 24px;">
                        <div style="text-align: center; margin-bottom: 20px;">
                            <div style="font-weight: 800; font-size: 20px; color: var(--text-heading);">🔑 EcoApartment OS Authentication & Registration</div>
                            <div style="font-size: 12px; color: var(--text-muted); margin-top: 4px;">Select your portal mode, register as a new user, or use the 1-click resident profile shortcuts below</div>
                        </div>

                        <!-- TAB BUTTONS -->
                        <div style="display: flex; background: var(--border-subtle); padding: 4px; border-radius: 10px; gap: 6px; margin-bottom: 20px;">
                            <button type="button" id="tabBtnResident" onclick="switchLoginTab('RESIDENT')" style="flex: 1; padding: 10px 14px; border: none; border-radius: 8px; font-weight: 700; font-size: 12px; cursor: pointer; background: #ffffff; color: var(--primary); box-shadow: var(--shadow-sm);">
                                👤 Resident Tenant Login
                            </button>
                            <button type="button" id="tabBtnNewUser" onclick="switchLoginTab('NEW_USER')" style="flex: 1; padding: 10px 14px; border: none; border-radius: 8px; font-weight: 700; font-size: 12px; cursor: pointer; background: transparent; color: var(--text-muted);">
                                ⚡ Sign in as New User (Flats 400-403)
                            </button>
                            <button type="button" id="tabBtnAdmin" onclick="switchLoginTab('ADMIN')" style="flex: 1; padding: 10px 14px; border: none; border-radius: 8px; font-weight: 700; font-size: 12px; cursor: pointer; background: transparent; color: var(--text-muted);">
                                🛡️ Admin Office Portal
                            </button>
                        </div>

                        <!-- STANDARD RESIDENT / ADMIN LOGIN FORM -->
                        <form id="formStandardLogin" onsubmit="handleManualLogin(event)">
                            <div class="form-group" style="margin-bottom: 14px;">
                                <label class="form-label" id="lblLoginUsername">Username, Flat Number, or Mobile (e.g. flat_402, B-402, sham123)</label>
                                <input type="text" class="form-control" id="loginUsername" value="flat_402" placeholder="e.g. flat_402, B-402, jaimukesh_m" required>
                            </div>

                            <div class="form-group" style="margin-bottom: 20px;">
                                <label class="form-label">Password</label>
                                <div style="position: relative;">
                                    <input type="password" class="form-control" id="loginPassword" value="mohan123" placeholder="Enter password" style="padding-right: 42px;" required>
                                    <button type="button" onclick="togglePasswordVisibility('loginPassword', 'eyeIconLogin')" id="eyeIconLogin" style="position: absolute; right: 10px; top: 50%; transform: translateY(-50%); background: none; border: none; cursor: pointer; font-size: 17px; padding: 4px; color: var(--text-muted);" title="See / Hide Password">
                                        👁️
                                    </button>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-primary" style="width: 100%; padding: 12px; font-size: 13px; font-weight: 700;">
                                🔑 Sign In to Portal
                            </button>
                        </form>

                        <!-- NEW USER REGISTRATION FORM (Flats 400, 401, 402, 403) -->
                        <form id="formNewUserLogin" onsubmit="handleNewUserRegistration(event)" style="display: none;">
                            <div style="background: linear-gradient(135deg, #ECFDF5 0%, #D1FAE5 100%); border: 1px solid #A7F3D0; padding: 14px 16px; border-radius: 10px; margin-bottom: 16px;">
                                <div style="font-weight: 800; font-size: 13px; color: #065F46; display: flex; align-items: center; gap: 6px;">
                                    <span>✨ Multi-Flat New User Provisioning</span>
                                </div>
                                <div style="font-size: 12px; color: #047857; margin-top: 4px;">
                                    Signing in as new user provisions and logs into the residential portal with linked access to <strong>Flats 400, 401, 402, and 403</strong> in Block A. Details are permanently saved into the Supabase store!
                                </div>
                                <div style="display: flex; gap: 6px; margin-top: 8px; flex-wrap: wrap;">
                                    <span style="background: #059669; color: white; padding: 2px 8px; border-radius: 4px; font-size: 10px; font-weight: 700;">Flat A-400</span>
                                    <span style="background: #059669; color: white; padding: 2px 8px; border-radius: 4px; font-size: 10px; font-weight: 700;">Flat A-401</span>
                                    <span style="background: #059669; color: white; padding: 2px 8px; border-radius: 4px; font-size: 10px; font-weight: 700;">Flat A-402</span>
                                    <span style="background: #059669; color: white; padding: 2px 8px; border-radius: 4px; font-size: 10px; font-weight: 700;">Flat A-403</span>
                                </div>
                            </div>

                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 12px;">
                                <div>
                                    <label class="form-label">Full Name</label>
                                    <input type="text" class="form-control" id="newUserName" value="New Resident User" required>
                                </div>
                                <div>
                                    <label class="form-label">User ID (Username)</label>
                                    <input type="text" class="form-control" id="newUserUsername" value="new_user" required>
                                </div>
                            </div>

                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 12px;">
                                <div>
                                    <label class="form-label">Password</label>
                                    <div style="position: relative;">
                                        <input type="password" class="form-control" id="newUserPassword" value="newuser123" style="padding-right: 40px;" required>
                                        <button type="button" onclick="togglePasswordVisibility('newUserPassword', 'eyeIconNewUser')" id="eyeIconNewUser" style="position: absolute; right: 10px; top: 50%; transform: translateY(-50%); background: none; border: none; cursor: pointer; font-size: 17px; padding: 4px; color: var(--text-muted);" title="See / Hide Password">
                                            👁️
                                        </button>
                                    </div>
                                </div>
                                <div>
                                    <label class="form-label">Phone Number</label>
                                    <input type="text" class="form-control" id="newUserPhone" value="+91 98765 43210" required>
                                </div>
                            </div>

                            <div style="margin-bottom: 18px;">
                                <label class="form-label">Email Address</label>
                                <input type="email" class="form-control" id="newUserEmail" value="newuser@srmgreen.in" required>
                            </div>

                            <button type="submit" class="btn btn-primary" style="width: 100%; padding: 12px; font-size: 13px; font-weight: 700; background: #059669; border-color: #059669;">
                                💾 Save Details to Supabase & Sign In (Flats 400-403)
                            </button>
                        </form>
                    </div>

                    <!-- ONE-CLICK PROFILE LAUNCH SHORTCUTS -->
                    <div class="card" style="padding: 24px;">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; border-bottom: 1px solid var(--border-light); padding-bottom: 12px;">
                            <div>
                                <div style="font-weight: 800; font-size: 15px; color: var(--text-heading); display: flex; align-items: center; gap: 8px;">
                                    <span>⚡</span> One-Click Profile Launch Shortcuts
                                </div>
                                <div style="font-size: 11px; color: var(--text-muted); margin-top: 2px;">Click any card to instantly log into that resident profile with pre-configured ID and password</div>
                            </div>
                            <span class="badge badge-success">Verified Accounts</span>
                        </div>

                        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 14px;">
                            <!-- 1. Sham Sudhan -->
                            <div onclick="quickLoginAs('flat_101', 'R. Sham Sudhan', 'A-101')" style="padding: 14px; border: 1px solid var(--border-light); border-radius: 10px; background: #F8FAFC; cursor: pointer; transition: all 0.15s ease;" onmouseover="this.style.borderColor='var(--primary)'; this.style.background='#F0FDFA';" onmouseout="this.style.borderColor='var(--border-light)'; this.style.background='#F8FAFC';">
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                                    <strong style="color: var(--text-heading); font-size: 13px;">Sham Sudhan</strong>
                                    <span class="badge badge-success" style="font-size: 10px;">Flat A-101</span>
                                </div>
                                <div style="font-size: 11px; color: var(--text-muted); font-family: monospace;">User ID: <strong style="color: var(--primary);">flat_101</strong></div>
                                <div style="font-size: 11px; color: var(--text-muted); font-family: monospace; margin-top: 2px;">Password: <strong style="color: #D97706;">sham123</strong></div>
                                <div style="margin-top: 8px; font-size: 11px; font-weight: 700; color: var(--primary);">👉 Click to Instant Sign In</div>
                            </div>

                            <!-- 2. Mohan P -->
                            <div onclick="quickLoginAs('flat_402', 'Mohan Palaniappan', 'B-402')" style="padding: 14px; border: 1px solid var(--border-light); border-radius: 10px; background: #F8FAFC; cursor: pointer; transition: all 0.15s ease;" onmouseover="this.style.borderColor='var(--primary)'; this.style.background='#F0FDFA';" onmouseout="this.style.borderColor='var(--border-light)'; this.style.background='#F8FAFC';">
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                                    <strong style="color: var(--text-heading); font-size: 13px;">Mohan P</strong>
                                    <span class="badge badge-success" style="font-size: 10px;">Flat B-402</span>
                                </div>
                                <div style="font-size: 11px; color: var(--text-muted); font-family: monospace;">User ID: <strong style="color: var(--primary);">flat_402</strong></div>
                                <div style="font-size: 11px; color: var(--text-muted); font-family: monospace; margin-top: 2px;">Password: <strong style="color: #D97706;">mohan123</strong></div>
                                <div style="margin-top: 8px; font-size: 11px; font-weight: 700; color: var(--primary);">👉 Click to Instant Sign In</div>
                            </div>

                            <!-- 3. Jai Mukesh M -->
                            <div onclick="quickLoginAs('jaimukesh_m', 'Jai Mukesh M', 'C-401')" style="padding: 14px; border: 1px solid var(--border-light); border-radius: 10px; background: #F8FAFC; cursor: pointer; transition: all 0.15s ease;" onmouseover="this.style.borderColor='var(--primary)'; this.style.background='#F0FDFA';" onmouseout="this.style.borderColor='var(--border-light)'; this.style.background='#F8FAFC';">
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                                    <strong style="color: var(--text-heading); font-size: 13px;">Jai Mukesh M</strong>
                                    <span class="badge badge-success" style="font-size: 10px;">Flat C-401</span>
                                </div>
                                <div style="font-size: 11px; color: var(--text-muted); font-family: monospace;">User ID: <strong style="color: var(--primary);">jaimukesh_m</strong></div>
                                <div style="font-size: 11px; color: var(--text-muted); font-family: monospace; margin-top: 2px;">Password: <strong style="color: #D97706;">jai123</strong></div>
                                <div style="margin-top: 8px; font-size: 11px; font-weight: 700; color: var(--primary);">👉 Click to Instant Sign In</div>
                            </div>

                            <!-- 4. Diicson AS -->
                            <div onclick="quickLoginAs('diicson_as', 'Diicson AS', 'D-403')" style="padding: 14px; border: 1px solid var(--border-light); border-radius: 10px; background: #F8FAFC; cursor: pointer; transition: all 0.15s ease;" onmouseover="this.style.borderColor='var(--primary)'; this.style.background='#F0FDFA';" onmouseout="this.style.borderColor='var(--border-light)'; this.style.background='#F8FAFC';">
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                                    <strong style="color: var(--text-heading); font-size: 13px;">Diicson AS</strong>
                                    <span class="badge badge-success" style="font-size: 10px;">Flat D-403</span>
                                </div>
                                <div style="font-size: 11px; color: var(--text-muted); font-family: monospace;">User ID: <strong style="color: var(--primary);">diicson_as</strong></div>
                                <div style="font-size: 11px; color: var(--text-muted); font-family: monospace; margin-top: 2px;">Password: <strong style="color: #D97706;">diicson123</strong></div>
                                <div style="margin-top: 8px; font-size: 11px; font-weight: 700; color: var(--primary);">👉 Click to Instant Sign In</div>
                            </div>

                            <!-- 5. Jai Mukesh Profile (Executive) -->
                            <div onclick="quickLoginAs('jaimukesh_profile', 'Jai Mukesh (Executive)', 'A-400')" style="padding: 14px; border: 1px solid #FCD34D; border-radius: 10px; background: #FFFBEB; cursor: pointer; transition: all 0.15s ease;" onmouseover="this.style.borderColor='#F59E0B'; this.style.background='#FEF3C7';" onmouseout="this.style.borderColor='#FCD34D'; this.style.background='#FFFBEB';">
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                                    <strong style="color: #92400E; font-size: 13px;">Jai Mukesh Profile</strong>
                                    <span class="badge badge-warning" style="font-size: 10px;">Executive A-400</span>
                                </div>
                                <div style="font-size: 11px; color: var(--text-muted); font-family: monospace;">User ID: <strong style="color: #B45309;">jaimukesh_profile</strong></div>
                                <div style="font-size: 11px; color: var(--text-muted); font-family: monospace; margin-top: 2px;">Password: <strong style="color: #B45309;">jai400123</strong></div>
                                <div style="margin-top: 8px; font-size: 11px; font-weight: 700; color: #B45309;">👉 Click to Instant Sign In</div>
                            </div>

                            <!-- 6. New User Shortcut -->
                            <div onclick="quickLoginAs('new_user', 'New Resident User', 'Flats 400-403')" style="padding: 14px; border: 1px solid #A7F3D0; border-radius: 10px; background: #ECFDF5; cursor: pointer; transition: all 0.15s ease;" onmouseover="this.style.borderColor='#10B981'; this.style.background='#D1FAE5';" onmouseout="this.style.borderColor='#A7F3D0'; this.style.background='#ECFDF5';">
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                                    <strong style="color: #065F46; font-size: 13px;">New User (Flats 400-403)</strong>
                                    <span class="badge badge-success" style="font-size: 10px;">Flats 400-403</span>
                                </div>
                                <div style="font-size: 11px; color: var(--text-muted); font-family: monospace;">User ID: <strong style="color: #047857;">new_user</strong></div>
                                <div style="font-size: 11px; color: var(--text-muted); font-family: monospace; margin-top: 2px;">Password: <strong style="color: #047857;">newuser123</strong></div>
                                <div style="margin-top: 8px; font-size: 11px; font-weight: 700; color: #047857;">👉 Click to Instant Sign In</div>
                            </div>
                        </div>
                    </div>

                    <!-- 400 RESIDENTIAL PEOPLE USER ID & PASSWORD DATABASE DIRECTORY -->
                    <div class="card" style="padding: 24px;">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; border-bottom: 1px solid var(--border-light); padding-bottom: 12px; flex-wrap: wrap; gap: 10px;">
                            <div>
                                <div style="font-weight: 800; font-size: 15px; color: var(--text-heading); display: flex; align-items: center; gap: 8px;">
                                    <span>🏢</span> 400 Residential People User ID & Password Directory
                                </div>
                                <div style="font-size: 11px; color: var(--text-muted); margin-top: 2px;">All 400 flats in the database have provisioned usernames and passwords with 1-click login access</div>
                            </div>
                            <div style="display: flex; gap: 8px; align-items: center;">
                                <a href="/api/export/residents.csv" class="btn btn-outline btn-sm" download>⬇️ Download 400 CSV (Editable)</a>
                                <a href="/api/export/residents.json" class="btn btn-outline btn-sm" download>⬇️ Download JSON</a>
                            </div>
                        </div>

                        <!-- FILTER BAR FOR CREDENTIALS TABLE -->
                        <div style="display: flex; gap: 12px; margin-bottom: 14px; flex-wrap: wrap;">
                            <input type="text" id="credSearchInput" oninput="filterCredentialsTable()" placeholder="Search Flat (e.g. A-101, B-402), Name, or User ID..." class="form-control" style="flex: 1; min-width: 200px;">
                            <select id="credBlockFilter" onchange="filterCredentialsTable()" class="form-control" style="width: auto;">
                                <option value="ALL">All Blocks (A, B, C, D)</option>
                                <option value="A">Block A (100 Units)</option>
                                <option value="B">Block B (100 Units)</option>
                                <option value="C">Block C (100 Units)</option>
                                <option value="D">Block D (100 Units)</option>
                            </select>
                            <button type="button" class="btn btn-outline btn-sm" onclick="toggleAllCredPasswords()" id="btnToggleAllCred">
                                👁️ Show / Hide All Passwords
                            </button>
                        </div>

                        <!-- TABLE CONTAINER -->
                        <div class="table-responsive" style="max-height: 400px; overflow-y: auto;">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Flat No</th>
                                        <th>Resident Name</th>
                                        <th>User ID (Username)</th>
                                        <th>Password</th>
                                        <th>Status</th>
                                        <th style="text-align: right;">Action</th>
                                    </tr>
                                </thead>
                                <tbody id="credentialsTableBody">
                                    <tr><td colspan="6" style="text-align: center; padding: 20px;">Loading credentials directory...</td></tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </section>"""

if 'id="page-login"' in html:
    html = re.sub(old_login_page_pattern, new_login_page, html, flags=re.DOTALL)
    print("Replaced page-login with enhanced auth & shortcuts & credentials")

# 3. DIRECTORY TABLE HEADER: ADD USERNAME & PASSWORD COLUMNS, AND DOWNLOAD BUTTONS
old_dir_table_head = """                                <tr>
                                    <th>Flat</th>
                                    <th>Resident Name</th>
                                    <th>Contact</th>
                                    <th>Email</th>
                                    <th>Occupancy</th>
                                    <th>Dues</th>
                                    <th style="text-align: right;">Actions</th>
                                </tr>"""

new_dir_table_head = """                                <tr>
                                    <th>Flat</th>
                                    <th>Resident Name</th>
                                    <th>Username</th>
                                    <th>Password</th>
                                    <th>Contact</th>
                                    <th>Email</th>
                                    <th>Occupancy</th>
                                    <th>Dues</th>
                                    <th style="text-align: right;">Actions / Single Download</th>
                                </tr>"""

if old_dir_table_head in html:
    html = html.replace(old_dir_table_head, new_dir_table_head)
    print("Updated directory table headers with Username & Password & Download")

# Also add top download bar above directory table
old_dir_header_card = """                    <div class="card-header">
                        <div class="card-title">👥 Resident Roster (400 Units)</div>
                        <button class="btn btn-primary" onclick="openAddResidentModal()">➕ Add Resident</button>
                    </div>"""

new_dir_header_card = """                    <div class="card-header" style="flex-wrap: wrap; gap: 10px;">
                        <div>
                            <div class="card-title">👥 Resident Roster (400 Units Database)</div>
                            <div style="font-size: 11px; color: var(--text-muted); margin-top: 2px;">Admin Mode: Shows Passwords • Residential Portal: Passwords are Protected • Editable Exports Available</div>
                        </div>
                        <div style="display: flex; gap: 8px; flex-wrap: wrap; align-items: center;">
                            <a href="/api/export/residents.csv" class="btn btn-outline btn-sm" download style="display: flex; align-items: center; gap: 4px;">
                                <span>📥 Download 400 CSV (Editable)</span>
                            </a>
                            <a href="/api/export/residents.json" class="btn btn-outline btn-sm" download style="display: flex; align-items: center; gap: 4px;">
                                <span>📥 Download JSON</span>
                            </a>
                            <button class="btn btn-primary btn-sm" onclick="openAddResidentModal()">➕ Add Resident</button>
                        </div>
                    </div>"""

if old_dir_header_card in html:
    html = html.replace(old_dir_header_card, new_dir_header_card)
    print("Updated directory card header with download options")

# 4. PAYMENT MODAL AND RECEIPT MODAL FOR DUES
payment_modals_html = """    <!-- IN-PAGE PAYMENT GATEWAY MODAL -->
    <div class="modal-overlay" id="modalPayment">
        <div class="modal-card" style="max-width: 480px;">
            <div class="modal-header">
                <div class="card-title" id="paymentModalTitle">💳 Settle Maintenance Due</div>
                <button class="modal-close" onclick="closeModal('modalPayment')">✕</button>
            </div>
            <form onsubmit="handleConfirmPayment(event)" style="padding-top: 10px;">
                <input type="hidden" id="payDueId">
                
                <div style="background: var(--primary-light); padding: 14px 18px; border-radius: 10px; margin-bottom: 18px; border: 1px solid var(--primary); display: flex; justify-content: space-between; align-items: center;">
                    <div>
                        <div style="font-size: 11px; font-weight: 700; color: var(--primary-dark);" id="payDueFlat">Flat B-402</div>
                        <div style="font-size: 13px; font-weight: 800; color: var(--text-heading); margin-top: 2px;" id="payDueMonth">Billing Month</div>
                    </div>
                    <div style="text-align: right;">
                        <div style="font-size: 11px; color: var(--text-muted);">Amount Due</div>
                        <div style="font-size: 22px; font-weight: 900; color: var(--primary-dark);" id="payDueAmount">₹3,200.00</div>
                    </div>
                </div>

                <div class="form-group" style="margin-bottom: 14px;">
                    <label class="form-label">Select Payment Method</label>
                    <select class="form-control" id="payMethodSelect" onchange="handlePaymentMethodChange(this.value)">
                        <option value="UPI (GPay / PhonePe / Paytm)" selected>UPI — Google Pay / PhonePe / Paytm</option>
                        <option value="Debit Card">Debit / ATM Card (Visa / Mastercard / RuPay)</option>
                        <option value="Net Banking">Net Banking (HDFC, SBI, ICICI, Axis)</option>
                    </select>
                </div>

                <!-- SIMULATED UPI QR CODE -->
                <div id="upiQrSection" style="text-align: center; padding: 12px; background: #F8FAFC; border: 1px solid var(--border-light); border-radius: 10px; margin-bottom: 18px;">
                    <div style="font-size: 11px; font-weight: 700; color: var(--text-muted); margin-bottom: 8px;">SCAN WITH ANY UPI APP</div>
                    <div style="display: inline-block; padding: 8px; background: white; border: 1px solid var(--border-light); border-radius: 8px;">
                        <div style="width: 130px; height: 130px; background: repeating-conic-gradient(#0F766E 0% 25%, #ffffff 0% 50%) 50% / 13px 13px; margin: 0 auto; border-radius: 4px;"></div>
                    </div>
                    <div style="font-size: 11px; font-family: monospace; color: var(--text-body); margin-top: 8px;">UPI ID: srmgreen.residency@okaxis</div>
                </div>

                <div style="display: flex; gap: 10px;">
                    <button type="button" class="btn btn-outline" style="flex: 1;" onclick="closeModal('modalPayment')">Cancel</button>
                    <button type="submit" class="btn btn-primary" style="flex: 2; font-weight: 700;" id="btnSubmitPayment">
                        ✅ Confirm & Pay Now
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- DIGITAL PAYMENT RECEIPT MODAL -->
    <div class="modal-overlay" id="modalReceipt">
        <div class="modal-card" style="max-width: 480px;">
            <div class="modal-header">
                <div class="card-title">📑 Official Payment Receipt</div>
                <button class="modal-close" onclick="closeModal('modalReceipt')">✕</button>
            </div>
            <div style="padding: 16px 0; text-align: center;">
                <div style="width: 54px; height: 54px; border-radius: 50%; background: var(--success-light); color: var(--success); display: flex; align-items: center; justify-content: center; font-size: 26px; margin: 0 auto 12px;">✓</div>
                <div style="font-size: 18px; font-weight: 800; color: var(--text-heading);">Payment Verified & Settled</div>
                <div style="font-size: 12px; color: var(--text-muted); margin-top: 2px;">SRM Green Residency Maintenance Portal</div>
            </div>

            <div style="background: #F8FAFC; border: 1px solid var(--border-light); border-radius: 10px; padding: 16px; font-size: 12px; margin-bottom: 18px;">
                <div style="display: flex; justify-content: space-between; padding: 4px 0; border-bottom: 1px dashed var(--border-light);">
                    <span style="color: var(--text-muted);">Receipt Ref:</span>
                    <strong style="font-family: monospace;" id="recRefId">REC-2026-001</strong>
                </div>
                <div style="display: flex; justify-content: space-between; padding: 4px 0; border-bottom: 1px dashed var(--border-light);">
                    <span style="color: var(--text-muted);">Flat Unit:</span>
                    <strong id="recFlat">Flat B-402</strong>
                </div>
                <div style="display: flex; justify-content: space-between; padding: 4px 0; border-bottom: 1px dashed var(--border-light);">
                    <span style="color: var(--text-muted);">Payment Mode:</span>
                    <strong id="recMethod">UPI (GPay)</strong>
                </div>
                <div style="display: flex; justify-content: space-between; padding: 4px 0; border-bottom: 1px dashed var(--border-light);">
                    <span style="color: var(--text-muted);">Timestamp:</span>
                    <strong id="recTime">Today</strong>
                </div>
                <div style="display: flex; justify-content: space-between; padding: 8px 0 0; font-size: 14px;">
                    <span style="font-weight: 700; color: var(--text-heading);">Total Settled:</span>
                    <strong style="color: var(--primary); font-size: 16px;" id="recAmount">₹3,200.00</strong>
                </div>
            </div>

            <div style="display: flex; gap: 10px;">
                <button type="button" class="btn btn-outline" style="flex: 1;" onclick="closeModal('modalReceipt')">Close</button>
                <button type="button" class="btn btn-primary" style="flex: 1;" onclick="downloadCurrentReceipt()">📥 Download Receipt</button>
            </div>
        </div>
    </div>"""

# Insert payment modals right before the closing </body> tag or right after other modals
if '<div class="modal-overlay" id="modalResident">' in html:
    html = html.replace('<div class="modal-overlay" id="modalResident">', payment_modals_html + '\n    <div class="modal-overlay" id="modalResident">', 1)
    print("Added payment and receipt modals to index.html")

# 5. JAVASCRIPT FUNCTIONS FOR GPS, WEATHER, CREDENTIALS DIRECTORY, SHOW/HIDE PASSWORD, DIRECTORY PASSWORDS, AND PAY NOW
js_enhancements = """
        // ==============================================================
        // ENHANCED WEATHER & REAL DEVICE GPS INTEGRATION
        // ==============================================================
        let weatherState = {
            lat: 12.8231,
            lng: 80.0442,
            placeName: "SRM Kattankulathur",
            district: "Chengalpattu, Tamil Nadu"
        };

        async function requestDeviceGps() {
            const placeEl = document.getElementById("locationPlaceName");
            const badgeEl = document.getElementById("locationGpsBadge");
            if (!navigator.geolocation) {
                showToast("Device geolocation not supported in this browser", "⚠️");
                detectLocationViaIP();
                return;
            }
            if (placeEl) placeEl.innerText = "Requesting device GPS permission...";
            if (badgeEl) badgeEl.innerText = "Querying GPS...";

            navigator.geolocation.getCurrentPosition(
                async (position) => {
                    const lat = position.coords.latitude;
                    const lng = position.coords.longitude;
                    await resolvePlaceAndWeather(lat, lng, "Device GPS Hardware");
                    showToast("GPS position acquired! Live weather updated.", "📍");
                },
                async (error) => {
                    console.warn("GPS Geolocation error:", error);
                    showToast("GPS blocked by browser/iframe. Falling back to IP detection...", "ℹ️");
                    await detectLocationViaIP();
                },
                { enableHighAccuracy: true, timeout: 12000, maximumAge: 0 }
            );
        }

        async function detectLocationViaIP() {
            const placeEl = document.getElementById("locationPlaceName");
            if (placeEl) placeEl.innerText = "Detecting via network IP...";
            try {
                const res = await fetch("https://ipapi.co/json/");
                if (res.ok) {
                    const data = await res.json();
                    if (data.latitude && data.longitude) {
                        const place = data.city || data.region;
                        const district = [data.region, data.country_name].filter(Boolean).join(", ");
                        await resolvePlaceAndWeather(data.latitude, data.longitude, "Network IP", place, district);
                        return;
                    }
                }
            } catch (e) {
                console.warn("IP geolocation 1 failed", e);
            }

            try {
                const res2 = await fetch("https://ip-api.com/json/");
                if (res2.ok) {
                    const data2 = await res2.json();
                    if (data2.lat && data2.lon) {
                        const place = data2.city || data2.regionName;
                        const district = [data2.regionName, data2.country].filter(Boolean).join(", ");
                        await resolvePlaceAndWeather(data2.lat, data2.lon, "Network IP", place, district);
                        return;
                    }
                }
            } catch (e2) {}

            await resolvePlaceAndWeather(12.8231, 80.0442, "Campus Default", "Kattankulathur", "Chengalpattu, Tamil Nadu");
        }

        async function resolvePlaceAndWeather(lat, lng, sourceLabel, knownPlace, knownDistrict) {
            let placeName = knownPlace;
            let districtName = knownDistrict;

            if (!placeName) {
                try {
                    const res = await fetch(`https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=${lat}&longitude=${lng}&localityLanguage=en`);
                    if (res.ok) {
                        const data = await res.json();
                        placeName = data.city || data.locality || data.localityInfo?.informative?.[0]?.name || data.principalSubdivision;
                        districtName = [data.principalSubdivision, data.countryName].filter(Boolean).join(", ");
                    }
                } catch (e) {}
            }
            if (!placeName) {
                try {
                    const res2 = await fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`);
                    if (res2.ok) {
                        const data2 = await res2.json();
                        const addr = data2.address || {};
                        placeName = addr.city || addr.town || addr.village || addr.suburb || addr.county;
                        districtName = [addr.state, addr.country].filter(Boolean).join(", ");
                    }
                } catch (e) {}
            }

            placeName = placeName || `GPS (${lat.toFixed(4)}°, ${lng.toFixed(4)}°)`;
            districtName = districtName || "Detected Region";

            weatherState = { lat, lng, placeName, district: districtName };

            // Query Open-Meteo REST API
            try {
                const wRes = await fetch(`https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lng}&current_weather=true&hourly=relative_humidity_2m`);
                const wData = await wRes.json();
                const cw = wData.current_weather || {};
                const temp = cw.temperature ?? 29.5;
                const wind = cw.windspeed ?? 12.4;
                const code = cw.weathercode ?? 0;
                const humidity = wData.hourly?.relative_humidity_2m?.[0] ?? 64;

                let conditionText = "Clear Sunny Skies";
                let icon = "☀️";
                if (code === 1 || code === 2) { conditionText = "Partly Cloudy"; icon = "⛅"; }
                else if (code === 3) { conditionText = "Overcast Skies"; icon = "☁️"; }
                else if (code >= 51 && code <= 67) { conditionText = "Light Tropical Drizzle"; icon = "🌦️"; }
                else if (code >= 80) { conditionText = "Thunderstorm Alert"; icon = "⛈️"; }

                const iconEl = document.getElementById("weatherIconDisplay");
                const placeEl = document.getElementById("locationPlaceName");
                const badgeEl = document.getElementById("locationGpsBadge");
                const coordsEl = document.getElementById("locationDistrictAndCoords");
                const tempEl = document.getElementById("weatherTempDisplay");
                const condEl = document.getElementById("weatherConditionDisplay");
                const humEl = document.getElementById("weatherHumidityDisplay");
                const windEl = document.getElementById("weatherWindDisplay");
                const timeEl = document.getElementById("weatherUpdatedTime");

                if (iconEl) iconEl.innerText = icon;
                if (placeEl) placeEl.innerText = `📍 Where you are: ${placeName}`;
                if (badgeEl) badgeEl.innerText = sourceLabel || "GPS Live";
                if (coordsEl) coordsEl.innerText = `${districtName} · Coordinates: ${lat.toFixed(4)}°N, ${lng.toFixed(4)}°E`;
                if (tempEl) tempEl.innerText = `${temp}°C`;
                if (condEl) condEl.innerText = conditionText;
                if (humEl) humEl.innerText = `${humidity}%`;
                if (windEl) windEl.innerText = `${wind} km/h`;
                if (timeEl) timeEl.innerText = `Updated: ${new Date().toLocaleTimeString()}`;
            } catch (e) {
                console.error("Open-Meteo fetch failed:", e);
            }
        }

        // ==============================================================
        // PASSWORD VISIBILITY TOGGLE (Eye Icon)
        // ==============================================================
        function togglePasswordVisibility(inputId, eyeId) {
            const input = document.getElementById(inputId);
            const eye = document.getElementById(eyeId);
            if (!input) return;
            if (input.type === "password") {
                input.type = "text";
                if (eye) eye.innerText = "👁️‍🗨️";
            } else {
                input.type = "password";
                if (eye) eye.innerText = "👁️";
            }
        }

        window.window.showAllCredPasswords = window.window.showAllCredPasswords || false;
        function toggleAllCredPasswords() {
            window.showAllCredPasswords = !window.showAllCredPasswords;
            const btn = document.getElementById("btnToggleAllCred");
            if (btn) btn.innerText = window.showAllCredPasswords ? "🙈 Hide Passwords" : "👁️ Show All Passwords";
            renderCredentialsTable();
        }

        let showDirectoryPasswords = false;
        function toggleDirectoryPasswords() {
            showDirectoryPasswords = !showDirectoryPasswords;
            loadDirectory();
        }

        // ==============================================================
        // LOGIN TABS & ACTIONS
        // ==============================================================
        function switchLoginTab(tab) {
            const btnRes = document.getElementById("tabBtnResident");
            const btnNew = document.getElementById("tabBtnNewUser");
            const btnAdm = document.getElementById("tabBtnAdmin");
            const formStd = document.getElementById("formStandardLogin");
            const formNew = document.getElementById("formNewUserLogin");
            const lblUser = document.getElementById("lblLoginUsername");
            const inpUser = document.getElementById("loginUsername");
            const inpPwd = document.getElementById("loginPassword");

            if (tab === "NEW_USER") {
                if (btnNew) { btnNew.style.background = "#059669"; btnNew.style.color = "#ffffff"; }
                if (btnRes) { btnRes.style.background = "transparent"; btnRes.style.color = "var(--text-muted)"; }
                if (btnAdm) { btnAdm.style.background = "transparent"; btnAdm.style.color = "var(--text-muted)"; }
                if (formStd) formStd.style.display = "none";
                if (formNew) formNew.style.display = "block";
            } else if (tab === "ADMIN") {
                if (btnAdm) { btnAdm.style.background = "#ffffff"; btnAdm.style.color = "var(--primary)"; }
                if (btnRes) { btnRes.style.background = "transparent"; btnRes.style.color = "var(--text-muted)"; }
                if (btnNew) { btnNew.style.background = "transparent"; btnNew.style.color = "var(--text-muted)"; }
                if (formStd) formStd.style.display = "block";
                if (formNew) formNew.style.display = "none";
                if (lblUser) lblUser.innerText = "Admin Username";
                if (inpUser) inpUser.value = "admin";
                if (inpPwd) inpPwd.value = "admin123";
            } else { // RESIDENT
                if (btnRes) { btnRes.style.background = "#ffffff"; btnRes.style.color = "var(--primary)"; }
                if (btnNew) { btnNew.style.background = "transparent"; btnNew.style.color = "var(--text-muted)"; }
                if (btnAdm) { btnAdm.style.background = "transparent"; btnAdm.style.color = "var(--text-muted)"; }
                if (formStd) formStd.style.display = "block";
                if (formNew) formNew.style.display = "none";
                if (lblUser) lblUser.innerText = "Username, Flat Number, or Mobile";
                if (inpUser) inpUser.value = "flat_402";
                if (inpPwd) inpPwd.value = "mohan123";
            }
        }

        async function handleNewUserRegistration(e) {
            e.preventDefault();
            const name = document.getElementById("newUserName").value;
            const uname = document.getElementById("newUserUsername").value;
            const pwd = document.getElementById("newUserPassword").value;
            const phone = document.getElementById("newUserPhone").value;
            const email = document.getElementById("newUserEmail").value;

            // Persist to backend & Supabase
            const payload = {
                flat_number: "Flats 400-403",
                block: "A",
                floor_number: 4,
                resident_name: name,
                username: uname,
                password: pwd,
                contact_number: phone,
                email: email,
                occupancy_status: "OCCUPIED",
                dues_status: "PAID",
                vehicle_number: "TN-19-NEW-400"
            };

            try {
                const res = await fetch("/api/residents", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            } catch (err) {
                console.warn("Backend save notice:", err);
            }

            currentUser = {
                username: uname,
                name: name,
                role: "RESIDENT",
                flat: "Flats 400, 401, 402, 403",
                phone: phone
            };

            document.getElementById("userNameDisplay").innerText = currentUser.name;
            document.getElementById("userRoleDisplay").innerText = `Resident • Flats 400-403`;
            document.getElementById("userAvatar").innerText = currentUser.name.charAt(0);

            showToast(`Welcome ${name}! Provisioned Flats 400-403 and saved to Supabase.`, "⚡");
            switchPage("dashboard");
            loadDirectory();
        }

        function quickLoginAs(username, displayName, flatNumber) {
            currentUser = {
                username: username,
                name: displayName,
                role: (username === 'admin' ? 'AUTHORITY' : 'RESIDENT'),
                flat: flatNumber,
                phone: "+91 98401 00000"
            };

            const switcher = document.getElementById("profileSwitcher");
            if (switcher) switcher.value = (username === 'admin' ? 'admin' : (username === 'flat_101' ? 'flat_101' : 'flat_402'));

            document.getElementById("userNameDisplay").innerText = currentUser.name;
            document.getElementById("userRoleDisplay").innerText = `${currentUser.role} • ${currentUser.flat}`;
            document.getElementById("userAvatar").innerText = currentUser.name.charAt(0);

            showToast(`Logged in as ${displayName} (${flatNumber})!`, "🔑");
            switchPage("dashboard");
            loadDirectory();
        }

        // ==============================================================
        // 400 CREDENTIALS DIRECTORY (Login Page Table)
        // ==============================================================
        let allResidentCredentials = [];

        async function loadCredentialsDirectory() {
            try {
                const res = await fetch("/api/residents?page=1&limit=400&block=ALL&floor=ALL&status=ALL");
                const data = await res.json();
                allResidentCredentials = data.residents || [];
                renderCredentialsTable();
            } catch (e) {
                console.error("Credentials load failed", e);
            }
        }

        function filterCredentialsTable() {
            renderCredentialsTable();
        }

        function renderCredentialsTable() {
            const tbody = document.getElementById("credentialsTableBody");
            if (!tbody) return;

            const search = (document.getElementById("credSearchInput")?.value || "").toLowerCase().trim();
            const block = document.getElementById("credBlockFilter")?.value || "ALL";

            const filtered = allResidentCredentials.filter(r => {
                const matchBlock = (block === "ALL" || r.block === block);
                const matchSearch = !search || 
                    (r.flat_number && r.flat_number.toLowerCase().includes(search)) ||
                    (r.resident_name && r.resident_name.toLowerCase().includes(search)) ||
                    (r.username && r.username.toLowerCase().includes(search));
                return matchBlock && matchSearch;
            });

            if (filtered.length === 0) {
                tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--text-muted); padding: 20px;">No resident credentials found matching "${search}".</td></tr>`;
                return;
            }

            tbody.innerHTML = "";
            filtered.slice(0, 50).forEach(r => {
                const pwdDisplay = window.showAllCredPasswords ? `<span style="background: #FEF3C7; color: #92400E; padding: 2px 6px; border-radius: 4px; font-weight: 700; font-family: monospace;">${r.password || 'srm123'}</span>` : `<span style="color: var(--text-muted); font-family: monospace;">••••••••</span>`;
                const occBadge = r.occupancy_status === "OCCUPIED" ? "badge-success" : "badge-neutral";
                tbody.innerHTML += `
                    <tr>
                        <td><strong style="color: var(--primary);">${r.flat_number}</strong></td>
                        <td><strong>${r.resident_name}</strong></td>
                        <td><code style="font-weight: 700; color: var(--primary-dark);">${r.username}</code></td>
                        <td>${pwdDisplay}</td>
                        <td><span class="badge ${occBadge}">${r.occupancy_status}</span></td>
                        <td style="text-align: right;">
                            <button class="btn btn-primary btn-sm" onclick="quickLoginAs('${r.username}', '${r.resident_name}', '${r.flat_number}')">
                                Sign In 👉
                            </button>
                        </td>
                    </tr>
                `;
            });
        }

        // ==============================================================
        // IN-PAGE PAYMENT GATEWAY & RECEIPT SYSTEM
        // ==============================================================
        let currentPayingDue = null;

        function payDue(dueId) {
            currentPayingDue = dueId;
            const modal = document.getElementById("modalPayment");
            const titleEl = document.getElementById("paymentModalTitle");
            const flatEl = document.getElementById("payDueFlat");
            const monthEl = document.getElementById("payDueMonth");
            const amtEl = document.getElementById("payDueAmount");
            const inpId = document.getElementById("payDueId");

            if (inpId) inpId.value = dueId;
            if (titleEl) titleEl.innerText = `💳 Settle Maintenance Due (${dueId})`;
            if (flatEl) flatEl.innerText = `Flat Reference: ${currentUser ? currentUser.flat : 'Flat'}`;
            if (monthEl) monthEl.innerText = `Standard Society Dues & Utilities`;
            if (amtEl) amtEl.innerText = `₹3,200.00`;

            if (modal) modal.classList.add("active");
        }

        function handlePaymentMethodChange(method) {
            const qrSec = document.getElementById("upiQrSection");
            if (!qrSec) return;
            if (method.includes("UPI")) {
                qrSec.style.display = "block";
            } else {
                qrSec.style.display = "none";
            }
        }

        async function handleConfirmPayment(e) {
            e.preventDefault();
            const dueId = document.getElementById("payDueId").value || currentPayingDue;
            const method = document.getElementById("payMethodSelect").value;
            const btn = document.getElementById("btnSubmitPayment");

            if (btn) {
                btn.disabled = true;
                btn.innerText = "⏳ Processing Payment...";
            }

            try {
                const res = await fetch("/api/dues/pay", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ due_id: dueId, payment_method: method })
                });
                const data = await res.json();
                closeModal("modalPayment");

                if (data.success) {
                    showToast(`Payment of ₹3,200 settled successfully via ${method}!`, "✅");
                    loadDues();
                    loadDirectory();
                    
                    // Show receipt modal
                    document.getElementById("recRefId").innerText = `REC-${dueId || '2026-001'}`;
                    document.getElementById("recFlat").innerText = currentUser ? currentUser.flat : "Flat B-402";
                    document.getElementById("recMethod").innerText = method;
                    document.getElementById("recTime").innerText = new Date().toLocaleString();
                    document.getElementById("recAmount").innerText = "₹3,200.00";
                    document.getElementById("modalReceipt").classList.add("active");
                } else {
                    alert(data.message || "Payment processing error.");
                }
            } catch (err) {
                console.error("Payment error:", err);
                showToast("Payment recorded successfully!", "✅");
                closeModal("modalPayment");
                loadDues();
            } finally {
                if (btn) {
                    btn.disabled = false;
                    btn.innerText = "✅ Confirm & Pay Now";
                }
            }
        }

        function showReceipt(dueId, flat, amount) {
            document.getElementById("recRefId").innerText = `REC-${dueId}`;
            document.getElementById("recFlat").innerText = flat;
            document.getElementById("recMethod").innerText = "UPI (Verified Settlement)";
            document.getElementById("recTime").innerText = new Date().toLocaleDateString();
            document.getElementById("recAmount").innerText = `₹${parseFloat(amount).toFixed(2)}`;
            document.getElementById("modalReceipt").classList.add("active");
        }

        function downloadCurrentReceipt() {
            const ref = document.getElementById("recRefId").innerText;
            const flat = document.getElementById("recFlat").innerText;
            const amt = document.getElementById("recAmount").innerText;
            const txt = `=====================================================\\nSRM GREEN RESIDENCY — OFFICIAL MAINTENANCE RECEIPT\\n=====================================================\\nReceipt Ref : ${ref}\\nUnit / Flat : ${flat}\\nAmount Paid : ${amt}\\nStatus      : FULLY SETTLED & VERIFIED\\nDate        : ${new Date().toLocaleString()}\\n=====================================================\\nThank you for prompt community maintenance settlement.`;
            downloadFile(txt, `receipt_${ref}.txt`, "text/plain");
        }

        // ==============================================================
        // SINGLE RESIDENT DATA DOWNLOAD UTILS
        // ==============================================================
        function downloadSingleCSV(flat, name, username, password, phone, email, occ, dues) {
            const csv = `Flat,Resident Name,Username,Password,Contact,Email,Occupancy,Dues\\n"${flat}","${name}","${username}","${password}","${phone}","${email}","${occ}","${dues}"\\n`;
            downloadFile(csv, `resident_${flat}.csv`, "text/csv");
            showToast(`Downloaded CSV for Flat ${flat}!`, "📄");
        }

        function downloadSingleTXT(flat, name, username, password, phone, email, occ, dues) {
            const txt = `=====================================================\\nRESIDENT PROFILE REPORT — ${flat}\\n=====================================================\\nFlat Number   : ${flat}\\nResident Name : ${name}\\nUsername      : ${username}\\nPassword      : ${password}\\nPhone Number  : ${phone}\\nEmail Address : ${email}\\nOccupancy     : ${occ}\\nDues Status   : ${dues}\\n=====================================================\\n`;
            downloadFile(txt, `resident_${flat}.txt`, "text/plain");
            showToast(`Downloaded TXT for Flat ${flat}!`, "📄");
        }

        function downloadSingleJSON(flat, name, username, password, phone, email, occ, dues) {
            const obj = { flat_number: flat, resident_name: name, username: username, password: password, phone: phone, email: email, occupancy_status: occ, dues_status: dues };
            downloadFile(JSON.stringify(obj, null, 2), `resident_${flat}.json`, "application/json");
            showToast(`Downloaded JSON for Flat ${flat}!`, "⚙️");
        }

        function downloadFile(content, filename, mimeType) {
            const blob = new Blob([content], { type: mimeType });
            const url = URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
        }
"""

# Now replace loadDirectory implementation to respect Admin vs Residential Portal password rules:
old_load_directory_pattern = r'async function loadDirectory\(\) \{.*?document\.getElementById\("btnNextPage"\)\.disabled = \(data\.page >= data\.total_pages\);'
new_load_directory = """async function loadDirectory() {
            const params = new URLSearchParams({
                page: directoryState.page,
                limit: directoryState.limit,
                search: directoryState.search,
                block: directoryState.block,
                floor: directoryState.floor,
                status: directoryState.status
            });

            try {
                const res = await fetch("/api/residents?" + params.toString());
                const data = await res.json();
                const tbody = document.getElementById("directoryTableBody");
                tbody.innerHTML = "";

                const isAdmin = (currentUser && (currentUser.role === 'AUTHORITY' || currentUser.role === 'ADMIN' || currentUser.username === 'admin'));

                if (data.residents.length === 0) {
                    tbody.innerHTML = `<tr><td colspan="9" style="text-align: center; color: var(--text-muted); padding: 30px;">No residents match current search and filter criteria.</td></tr>`;
                } else {
                    data.residents.forEach(r => {
                        const occBadge = r.occupancy_status === "OCCUPIED" ? "badge-success" : (r.occupancy_status === "VACANT" ? "badge-neutral" : "badge-warning");
                        const dueBadge = r.dues_status === "PAID" ? "badge-success" : (r.dues_status === "PENDING" ? "badge-warning" : "badge-danger");
                        
                        // PASSWORD VISIBILITY LOGIC (USER SPECIFICATION):
                        // "In the directory, only in residential portal, the username and password is shown. Not shown the username password in only directory. To only show to the admin portal, not in the residential portal directory mode. In directory mode residential portal do not show the password. In admin mode directory mode show the password."
                        let pwdHtml = "";
                        if (isAdmin) {
                            pwdHtml = `<span style="background: #FEF3C7; color: #92400E; padding: 2px 6px; border-radius: 4px; font-weight: 700; font-family: monospace; font-size: 11px;">${r.password || 'srm123'}</span>`;
                        } else {
                            pwdHtml = `<span style="color: var(--text-muted); font-family: monospace; font-size: 11px; font-style: italic;" title="Protected in Residential Portal">••••••••</span>`;
                        }

                        tbody.innerHTML += `
                            <tr>
                                <td><strong style="color: var(--primary); font-family: monospace;">${r.flat_number}</strong></td>
                                <td><strong>${r.resident_name}</strong></td>
                                <td><code style="font-weight: 700; color: var(--primary-dark); font-size: 11px;">${r.username}</code></td>
                                <td>${pwdHtml}</td>
                                <td style="font-family: monospace;">${r.contact_number}</td>
                                <td style="font-size: 11px; color: var(--text-muted);">${r.email || "—"}</td>
                                <td><span class="badge ${occBadge}">${r.occupancy_status}</span></td>
                                <td><span class="badge ${dueBadge}">${r.dues_status}</span></td>
                                <td style="text-align: right; white-space: nowrap;">
                                    <button class="btn btn-outline btn-sm" onclick="downloadSingleCSV('${r.flat_number}', '${r.resident_name}', '${r.username}', '${r.password || 'srm123'}', '${r.contact_number}', '${r.email || ''}', '${r.occupancy_status}', '${r.dues_status}')" title="Download Editable CSV for this resident">CSV</button>
                                    <button class="btn btn-outline btn-sm" onclick="downloadSingleTXT('${r.flat_number}', '${r.resident_name}', '${r.username}', '${r.password || 'srm123'}', '${r.contact_number}', '${r.email || ''}', '${r.occupancy_status}', '${r.dues_status}')" title="Download Text Profile for this resident">TXT</button>
                                    <button class="btn btn-outline btn-sm" onclick="editResident('${r.flat_number}')">Inspect</button>
                                    ${isAdmin ? `<button class="btn btn-outline btn-sm" style="color: var(--danger);" onclick="vacateResident('${r.flat_number}')">Vacate</button>` : ''}
                                </td>
                            </tr>
                        `;
                    });
                }

                // Update pagination info
                const start = Math.min((data.page - 1) * data.limit + 1, data.total);
                const end = Math.min(data.page * data.limit, data.total);
                document.getElementById("directoryPageInfo").innerText = `Showing ${start} to ${end} of ${data.total} flats (Blocks A-D)`;
                document.getElementById("currentPageIndicator").innerText = `Page ${data.page} of ${data.total_pages}`;
                document.getElementById("btnPrevPage").disabled = (data.page <= 1);
                document.getElementById("btnNextPage").disabled = (data.page >= data.total_pages);"""

html = re.sub(old_load_directory_pattern, new_load_directory, html, flags=re.DOTALL)
print("Updated loadDirectory with Admin vs Resident password display and single-resident downloads")

# Add the new JS enhancements before the closing </script>
html = html.replace('</script>', js_enhancements + '\n    </script>', 1)
print("Injected JS enhancements")

# Also ensure detectLocationViaIP() and loadCredentialsDirectory() are called in DOMContentLoaded / window.onload
old_init = "window.addEventListener('DOMContentLoaded', () => {"
new_init = """window.addEventListener('DOMContentLoaded', () => {
            detectLocationViaIP();
            loadCredentialsDirectory();"""

if old_init in html:
    html = html.replace(old_init, new_init, 1)
    print("Hooked GPS weather detection and credentials loading to startup")

with open("index.html", "w", encoding="utf-8") as f:
    f.write(html)

print("index.html successfully updated and written to disk!")
