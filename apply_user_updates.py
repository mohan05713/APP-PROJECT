import re

with open('index.html', 'r', encoding='utf-8') as f:
    html = f.read()

# 1. UPDATE QUICK TEST PROFILES in modalAuthLogin
old_chips = r'<div style="display: flex; gap: 8px; flex-wrap: wrap;">\s*<button type="button" onclick="fillAuthForm\(\'flat_101\', \'sham123\'\)".*?</form>'

new_chips = '''<div style="display: flex; gap: 8px; flex-wrap: wrap;">
                            <button type="button" onclick="fillAuthForm('flat_101', 'sham123')" style="background: white; border: 1.5px solid #0F766E; padding: 6px 12px; border-radius: 6px; font-size: 11px; font-weight: 800; cursor: pointer; color: #0F766E;">
                                👤 R. SHAM SUDHAN (A-101) <span style="font-weight:400; opacity:0.8;">[pwd: sham123]</span>
                            </button>
                            <button type="button" onclick="fillAuthForm('flat_105', 'jai123')" style="background: white; border: 1.5px solid #0284C7; padding: 6px 12px; border-radius: 6px; font-size: 11px; font-weight: 800; cursor: pointer; color: #0284C7;">
                                👤 M. JAI MUGESH (A-105) <span style="font-weight:400; opacity:0.8;">[pwd: jai123]</span>
                            </button>
                            <button type="button" onclick="fillAuthForm('flat_103', 'madhu123')" style="background: white; border: 1.5px solid #7C3AED; padding: 6px 12px; border-radius: 6px; font-size: 11px; font-weight: 800; cursor: pointer; color: #7C3AED;">
                                👤 MADHU VARSHAN (A-103) <span style="font-weight:400; opacity:0.8;">[pwd: madhu123]</span>
                            </button>
                            <button type="button" onclick="fillAuthForm('flat_402', 'mohan123')" style="background: white; border: 1.5px solid #0F766E; padding: 6px 12px; border-radius: 6px; font-size: 11px; font-weight: 800; cursor: pointer; color: #0F766E;">
                                👤 Mohan P. (B-402) <span style="font-weight:400; opacity:0.8;">[pwd: mohan123]</span>
                            </button>
                            <button type="button" onclick="fillAuthForm('admin', 'admin123')" style="background: #FEF3C7; border: 1.5px solid #F59E0B; padding: 6px 12px; border-radius: 6px; font-size: 11px; font-weight: 800; cursor: pointer; color: #92400E;">
                                ⚡ Admin Authority <span style="font-weight:400; opacity:0.8;">[pwd: admin123]</span>
                            </button>
                        </div>
                    </div>

                    <div class="form-group" style="margin-bottom: 14px;">
                        <label class="form-label" style="font-weight: 700; font-size: 12px;">Username / Flat Number / Phone</label>
                        <input type="text" class="form-control" id="authModalUsername" placeholder="e.g. flat_101, flat_105, flat_103, flat_402, or admin" required style="font-size: 13px;">
                    </div>

                    <div class="form-group" style="margin-bottom: 20px;">
                        <label class="form-label" style="font-weight: 700; font-size: 12px;">Password</label>
                        <div style="position: relative;">
                            <input type="password" class="form-control" id="authModalPassword" placeholder="Enter password (e.g. sham123, jai123, madhu123, admin123)" required style="font-size: 13px; padding-right: 40px;">
                            <button type="button" onclick="togglePasswordVisibility('authModalPassword', 'eyeIconModal')" id="eyeIconModal" style="position: absolute; right: 10px; top: 50%; transform: translateY(-50%); background: none; border: none; cursor: pointer; font-size: 16px;">👁️</button>
                        </div>
                        <div style="font-size: 11px; color: #64748B; margin-top: 4px;">Verified strictly against database credentials.</div>
                    </div>

                    <div style="display: flex; gap: 10px; justify-content: flex-end;">
                        <button type="button" class="btn btn-outline btn-sm" onclick="closeModal('modalAuthLogin')">Cancel</button>
                        <button type="submit" class="btn btn-primary btn-sm" style="font-weight: 800; padding: 9px 18px;">🔓 Authenticate & Log In</button>
                    </div>
                </form>'''

html = re.sub(old_chips, new_chips, html, flags=re.DOTALL)

# 2. UPDATE DEFAULT TOPBAR USER TO R. SHAM SUDHAN
html = html.replace('R. Shyam Sundar', 'R. SHAM SUDHAN')

# 3. UPDATE RESIDENT PROFILE EDIT BUTTON IN residentOwnFamilyCard
res_card_search = '<div>💳 <strong>Maintenance Status:</strong> <span id="resCardDuesStatus" class="badge badge-success">PAID</span></div>'
res_card_replace = '''<div>💳 <strong>Maintenance Status:</strong> <span id="resCardDuesStatus" class="badge badge-success">PAID</span></div>
                        <button type="button" onclick="openEditResidentProfileModal()" class="btn btn-primary btn-sm" style="font-size: 12px; font-weight: 800; background: #0F766E;">
                            ✏️ Edit My Profile & Family Info
                        </button>'''

if res_card_search in html and 'openEditResidentProfileModal' not in html:
    html = html.replace(res_card_search, res_card_replace, 1)
    print("Added Edit My Profile button to resident own family card")

# 4. ADD MODAL FOR EDITING RESIDENT PROFILE
edit_profile_modal = '''
    <!-- RESIDENT EDIT PROFILE & FAMILY CONFIGURATION MODAL -->
    <div class="modal-overlay" id="modalEditResidentProfile">
        <div class="modal-dialog" style="max-width: 640px;">
            <div class="modal-header" style="background: linear-gradient(135deg, #0F766E 0%, #115E59 100%); color: white;">
                <div>
                    <h3 style="color: white; margin: 0; font-size: 18px; font-weight: 800;">✏️ Edit Profile & Family Configuration</h3>
                    <p style="margin: 4px 0 0; font-size: 12px; opacity: 0.9;" id="editModalFlatSubtitle">Update resident contact details, vehicle, and family roster</p>
                </div>
                <button type="button" class="modal-close" style="color: white;" onclick="closeModal('modalEditResidentProfile')">✕</button>
            </div>
            <div class="modal-body" style="padding: 24px;">
                <form id="formEditResidentProfile" onsubmit="handleSaveProfileUpdate(event)">
                    <input type="hidden" id="editProfileFlatNumber">
                    
                    <div style="font-size: 12px; font-weight: 800; color: #0F766E; text-transform: uppercase; margin-bottom: 12px; border-bottom: 1px solid #E2E8F0; padding-bottom: 6px;">
                        1. Primary Resident Information
                    </div>

                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 14px; margin-bottom: 14px;">
                        <div class="form-group">
                            <label class="form-label" style="font-weight: 700; font-size: 12px;">Resident Name (Head of Family)</label>
                            <input type="text" class="form-control" id="editProfileName" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" style="font-weight: 700; font-size: 12px;">Contact Mobile Number</label>
                            <input type="text" class="form-control" id="editProfilePhone" required>
                        </div>
                    </div>

                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 14px; margin-bottom: 14px;">
                        <div class="form-group">
                            <label class="form-label" style="font-weight: 700; font-size: 12px;">Email Address</label>
                            <input type="email" class="form-control" id="editProfileEmail">
                        </div>
                        <div class="form-group">
                            <label class="form-label" style="font-weight: 700; font-size: 12px;">Emergency Contact</label>
                            <input type="text" class="form-control" id="editProfileEmergency">
                        </div>
                    </div>

                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 14px; margin-bottom: 18px;">
                        <div class="form-group">
                            <label class="form-label" style="font-weight: 700; font-size: 12px;">Registered Vehicle Number</label>
                            <input type="text" class="form-control" id="editProfileVehicle">
                        </div>
                        <div class="form-group">
                            <label class="form-label" style="font-weight: 700; font-size: 12px;">New Password (Optional)</label>
                            <input type="text" class="form-control" id="editProfilePassword" placeholder="Leave empty to keep existing">
                        </div>
                    </div>

                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; border-bottom: 1px solid #E2E8F0; padding-bottom: 6px;">
                        <div style="font-size: 12px; font-weight: 800; color: #0F766E; text-transform: uppercase;">
                            2. Family Members Configuration
                        </div>
                        <button type="button" onclick="addFamilyMemberRow()" class="btn btn-outline btn-sm" style="font-size: 11px; padding: 4px 10px;">
                            + Add Family Member
                        </button>
                    </div>

                    <div style="max-height: 220px; overflow-y: auto; margin-bottom: 18px;">
                        <table class="data-table" style="font-size: 12px;">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Relation</th>
                                    <th>Gender</th>
                                    <th>Age</th>
                                    <th>Blood Group</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody id="editFamilyRowsTbody">
                                <!-- Dynamic editable rows -->
                            </tbody>
                        </table>
                    </div>

                    <div style="display: flex; gap: 10px; justify-content: flex-end; border-top: 1px solid #E2E8F0; padding-top: 16px;">
                        <button type="button" class="btn btn-outline btn-sm" onclick="closeModal('modalEditResidentProfile')">Cancel</button>
                        <button type="submit" class="btn btn-primary btn-sm" style="font-weight: 800; padding: 8px 18px;">💾 Save & Update Profile</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
'''

if 'modalEditResidentProfile' not in html:
    html = html.replace('</body>', edit_profile_modal + '\n</body>', 1)
    print("Added modalEditResidentProfile")

# 5. UPDATE GAS SECTION FROM PIPELINE TO TAMIL NADU DOMESTIC LPG CYLINDERS
# In IoT Hub section 4
old_lpg_header = '<h3 style="margin: 0; font-size: 18px; font-weight: 800; color: #EA580C;">🔥 4. Smart LPG Piped Gas Manifold</h3>'
new_lpg_header = '<h3 style="margin: 0; font-size: 18px; font-weight: 800; color: #EA580C;">🛢️ 4. Smart Domestic LPG Cylinder System (Tamil Nadu Model)</h3>'
html = html.replace(old_lpg_header, new_lpg_header)

old_lpg_stat1 = '<div class="stat-label">Manifold Pressure</div>\n                                <div class="stat-value">1.85 Bar</div>\n                                <div class="stat-subtext">Regulated safe pipeline supply</div>'
new_lpg_stat1 = '<div class="stat-label">Active Domestic Cylinder #1</div>\n                                <div class="stat-value">14.2 kg (78% Fill)</div>\n                                <div class="stat-subtext">Indane LPG · ~24 Days Remaining</div>'
html = html.replace(old_lpg_stat1, new_lpg_stat1)

old_lpg_stat2 = '<div class="stat-label">Bank A (Active Bank)</div>\n                                <div class="stat-value">84% Fill</div>\n                                <div class="stat-subtext">4 x 47.5 kg Industrial Cylinders</div>'
new_lpg_stat2 = '<div class="stat-label">Standby Reserve Cylinder #2</div>\n                                <div class="stat-value">100% Sealed Full</div>\n                                <div class="stat-subtext">Auto-Switchover Regulator Armed</div>'
html = html.replace(old_lpg_stat2, new_lpg_stat2)

old_lpg_stat3 = '<div class="stat-label">Bank B (Reserve Bank)</div>\n                                <div class="stat-value">100% Full</div>\n                                <div class="stat-subtext">Auto-Switchover Valve Arm</div>'
new_lpg_stat3 = '<div class="stat-label">LPG Agency & Booking Hub</div>\n                                <div class="stat-value">Refill Active</div>\n                                <div class="stat-subtext">Sri Krishna Indane (IVRS/1906)</div>'
html = html.replace(old_lpg_stat3, new_lpg_stat3)

with open('index.html', 'w', encoding='utf-8') as f:
    f.write(html)
print("Updated index.html markup successfully!")
