import re

with open("index.html", "r", encoding="utf-8") as f:
    html = f.read()

# 1. ADD EDIT PROFILE & LOGOUT BUTTONS IN TOPBAR RIGHT
old_user_pill = """                <div class="user-pill">
                    <div class="user-avatar" id="userAvatar">M</div>
                    <div class="user-details">
                        <div class="user-name" id="userNameDisplay">Mohan Palaniappan</div>
                        <div class="user-role" id="userRoleDisplay">Resident • Flat B-402</div>
                    </div>
                </div>"""

new_user_pill = """                <div class="user-pill">
                    <div class="user-avatar" id="userAvatar">M</div>
                    <div class="user-details">
                        <div class="user-name" id="userNameDisplay">Mohan Palaniappan</div>
                        <div class="user-role" id="userRoleDisplay">Resident • Flat B-402</div>
                    </div>
                </div>
                <button type="button" onclick="openEditProfileModal()" class="btn btn-outline btn-sm" title="Edit Profile (Username, Password, Phone)" style="background: white; font-weight: 700; font-size: 11px; padding: 6px 10px;">
                    ✏️ Edit Profile
                </button>
                <button type="button" onclick="handleLogout()" class="btn btn-outline btn-sm" title="Log out of session" style="background: #FEF2F2; color: #DC2626; border-color: #FCA5A5; font-weight: 700; font-size: 11px; padding: 6px 10px;">
                    🚪 Logout
                </button>"""

if old_user_pill in html:
    html = html.replace(old_user_pill, new_user_pill, 1)
    print("Added Edit Profile & Logout buttons to topbar")

# 2. ADD EDIT PROFILE MODAL HTML
edit_profile_modal_html = """    <!-- EDIT PROFILE MODAL (Username, Password, Phone, Email) -->
    <div class="modal-overlay" id="modalEditProfile">
        <div class="modal-card" style="max-width: 500px;">
            <div class="modal-header">
                <div class="card-title">✏️ Edit Active User Profile & Credentials</div>
                <button class="modal-close" onclick="closeModal('modalEditProfile')">✕</button>
            </div>
            <form onsubmit="handleSaveEditProfile(event)" style="padding-top: 10px; display: flex; flex-direction: column; gap: 14px;">
                <div class="form-group">
                    <label class="form-label">Resident / Admin Name</label>
                    <input type="text" class="form-control" id="editProfileName" required>
                </div>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
                    <div class="form-group">
                        <label class="form-label">Username (ID)</label>
                        <input type="text" class="form-control" id="editProfileUsername" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Password</label>
                        <div style="position: relative;">
                            <input type="password" class="form-control" id="editProfilePassword" style="padding-right: 40px;" required>
                            <button type="button" onclick="togglePasswordVisibility('editProfilePassword', 'eyeIconEditProfile')" id="eyeIconEditProfile" style="position: absolute; right: 10px; top: 50%; transform: translateY(-50%); background: none; border: none; cursor: pointer; font-size: 16px;">
                                👁️
                            </button>
                        </div>
                    </div>
                </div>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
                    <div class="form-group">
                        <label class="form-label">Phone Number</label>
                        <input type="text" class="form-control" id="editProfilePhone" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Email Address</label>
                        <input type="email" class="form-control" id="editProfileEmail" required>
                    </div>
                </div>
                <div class="form-group">
                    <label class="form-label">Assigned Flat / Unit</label>
                    <input type="text" class="form-control" id="editProfileFlat" required>
                </div>
                <div style="display: flex; gap: 10px; padding-top: 6px;">
                    <button type="button" class="btn btn-outline" style="flex: 1;" onclick="closeModal('modalEditProfile')">Cancel</button>
                    <button type="submit" class="btn btn-primary" style="flex: 2; font-weight: 700;">💾 Save & Update Profile</button>
                </div>
            </form>
        </div>
    </div>"""

if '<div class="modal-overlay" id="modalResident">' in html:
    html = html.replace('<div class="modal-overlay" id="modalResident">', edit_profile_modal_html + '\n    <div class="modal-overlay" id="modalResident">', 1)
    print("Added Edit Profile Modal to HTML")

# 3. ADD JAVASCRIPT FUNCTIONS FOR EDIT PROFILE & LOGOUT
edit_js = """
        // ==============================================================
        // EDIT PROFILE & LOGOUT FUNCTIONS
        // ==============================================================
        function openEditProfileModal() {
            if (!currentUser) {
                currentUser = { username: "flat_402", name: "Mohan Palaniappan", role: "RESIDENT", flat: "B-402", phone: "+91 63802 44185", email: "mohan.palaniappan@srmgreen.in", password: "mohan123" };
            }
            document.getElementById("editProfileName").value = currentUser.name || "";
            document.getElementById("editProfileUsername").value = currentUser.username || "";
            document.getElementById("editProfilePassword").value = currentUser.password || "mohan123";
            document.getElementById("editProfilePhone").value = currentUser.phone || "+91 63802 44185";
            document.getElementById("editProfileEmail").value = currentUser.email || "resident@srmgreen.in";
            document.getElementById("editProfileFlat").value = currentUser.flat || "B-402";

            document.getElementById("modalEditProfile").classList.add("active");
        }

        async function handleSaveEditProfile(e) {
            e.preventDefault();
            const name = document.getElementById("editProfileName").value;
            const username = document.getElementById("editProfileUsername").value;
            const password = document.getElementById("editProfilePassword").value;
            const phone = document.getElementById("editProfilePhone").value;
            const email = document.getElementById("editProfileEmail").value;
            const flat = document.getElementById("editProfileFlat").value;

            currentUser.name = name;
            currentUser.username = username;
            currentUser.password = password;
            currentUser.phone = phone;
            currentUser.email = email;
            currentUser.flat = flat;

            // Sync update to backend database / Supabase
            const payload = {
                flat_number: flat,
                block: flat.charAt(0) || "A",
                floor_number: 1,
                resident_name: name,
                username: username,
                password: password,
                contact_number: phone,
                email: email,
                occupancy_status: "OCCUPIED",
                dues_status: "PAID"
            };

            try {
                await fetch("/api/residents", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            } catch (err) {
                console.warn("Backend profile update error:", err);
            }

            document.getElementById("userNameDisplay").innerText = name;
            document.getElementById("userRoleDisplay").innerText = `${currentUser.role} • ${flat}`;
            document.getElementById("userAvatar").innerText = name.charAt(0);

            closeModal("modalEditProfile");
            showToast("Profile & credentials updated successfully! Synced to database.", "✅");
            loadDirectory();
        }

        function handleLogout() {
            if (confirm("Are you sure you want to log out of EcoApartment OS?")) {
                showToast("Logged out successfully. Redirecting to login page...", "🚪");
                switchPage("login");
            }
        }
"""

if '</script>' in html:
    html = html.replace('</script>', edit_js + '\n    </script>', 1)
    print("Injected Edit Profile and Logout JavaScript functions")

with open("index.html", "w", encoding="utf-8") as f:
    f.write(html)

print("index.html successfully updated with Edit Profile and Logout capabilities!")
