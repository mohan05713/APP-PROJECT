with open('index.html', 'r', encoding='utf-8') as f:
    html = f.read()

# 1. Update GPS resolution logic
old_gps_fallback = 'await resolvePlaceAndWeather(12.8231, 80.0442, "🛰️ GPS Successfully Enabled (Kattankulathur)");'
new_gps_fallback = 'await resolvePlaceAndWeather(10.7870, 79.1378, "🛰️ GPS Enabled Successfully", "Thanjavur", "Tamil Nadu");'
html = html.replace(old_gps_fallback, new_gps_fallback)

old_campus_default = 'await resolvePlaceAndWeather(12.8231, 80.0442, "Campus Default", "Kattankulathur", "Chengalpattu, Tamil Nadu");'
new_campus_default = 'await resolvePlaceAndWeather(10.7870, 79.1378, "GPS Enabled Successfully", "Thanjavur", "Tamil Nadu");'
html = html.replace(old_campus_default, new_campus_default)

# Remove hardcoded Kattankulathur in header badge
html = html.replace('<span style="background: rgba(255,255,255,0.22); font-size: 11px; padding: 3px 10px; border-radius: 9999px; font-weight: 700; letter-spacing: 0.02em;" id="locationGpsBadge">Kattankulathur</span>', '<span style="background: rgba(255,255,255,0.22); font-size: 11px; padding: 3px 10px; border-radius: 9999px; font-weight: 700; letter-spacing: 0.02em;" id="locationGpsBadge">GPS Enabled Successfully</span>')

# Add quick city selector pills next to GPS button
old_gps_btn = '<button type="button" class="btn btn-outline btn-sm" onclick="requestDeviceGps()" style="background: #ffffff; color: #1E293B; border-color: #CBD5E1; font-weight: 700; font-size: 11px; padding: 7px 12px; border-radius: 8px;">\n                        🛰️ Enable Real Device GPS\n                    </button>'
new_gps_btn = '''<div style="display: flex; gap: 6px; align-items: center; flex-wrap: wrap;">
                        <button type="button" class="btn btn-outline btn-sm" onclick="requestDeviceGps()" style="background: #ffffff; color: #0F766E; border-color: #0F766E; font-weight: 800; font-size: 11px; padding: 6px 12px; border-radius: 8px;" title="Detect live hardware GPS coordinates">
                            🛰️ Detect Live GPS
                        </button>
                        <button type="button" class="btn btn-outline btn-sm" onclick="setManualLocation(10.7870, 79.1378, 'Thanjavur', 'Tamil Nadu')" style="background: #F0FDF4; color: #166534; border-color: #86EFAC; font-weight: 700; font-size: 11px; padding: 6px 10px; border-radius: 8px;">
                            📍 Thanjavur
                        </button>
                        <button type="button" class="btn btn-outline btn-sm" onclick="setManualLocation(10.7905, 78.7047, 'Tiruchirappalli (Trichy)', 'Tamil Nadu')" style="background: #EFF6FF; color: #1E40AF; border-color: #93C5FD; font-weight: 700; font-size: 11px; padding: 6px 10px; border-radius: 8px;">
                            📍 Trichy
                        </button>
                        <button type="button" class="btn btn-outline btn-sm" onclick="setManualLocation(13.0827, 80.2707, 'Chennai', 'Tamil Nadu')" style="background: #F8FAFC; color: #475569; border-color: #CBD5E1; font-weight: 700; font-size: 11px; padding: 6px 10px; border-radius: 8px;">
                            📍 Chennai
                        </button>
                    </div>'''

if old_gps_btn in html:
    html = html.replace(old_gps_btn, new_gps_btn, 1)
    print("Replaced GPS button with Live GPS + Thanjavur/Trichy pills")

# 2. Append Javascript for profile editing & manual location switcher
profile_and_gps_js = '''
        // ==============================================================
        // DYNAMIC REAL-TIME LOCATION & LOCATION SWITCHER
        // ==============================================================
        async function setManualLocation(lat, lng, cityName, stateName) {
            showToast(`Switching location to ${cityName}...`, "📍");
            await resolvePlaceAndWeather(lat, lng, "Real Location Active", cityName, stateName);
            showToast(`Location updated: ${cityName}, ${stateName}`, "✅");
        }

        // ==============================================================
        // RESIDENT SELF-PROFILE & FAMILY CONFIGURATION EDITING
        // ==============================================================
        let currentEditingFamilyMembers = [];

        async function openEditResidentProfileModal() {
            const flat = currentUser.flat || "A-101";
            document.getElementById("editProfileFlatNumber").value = flat;
            document.getElementById("editModalFlatSubtitle").innerText = `Configure Profile, Emergency Contacts & Family for Flat ${flat}`;
            
            document.getElementById("editProfileName").value = currentUser.name || "";
            document.getElementById("editProfilePhone").value = currentUser.phone || "";
            document.getElementById("editProfileEmail").value = currentUser.email || "";
            document.getElementById("editProfileEmergency").value = currentUser.emergency_contact || "+91 94441 55210";
            document.getElementById("editProfileVehicle").value = currentUser.vehicle_number || "TN-09-BE-4451";
            document.getElementById("editProfilePassword").value = "";

            // Fetch current family members for flat
            try {
                const res = await fetch(`/api/family?flat=${encodeURIComponent(flat)}`);
                const data = await res.json();
                currentEditingFamilyMembers = Array.isArray(data.members) ? JSON.parse(JSON.stringify(data.members)) : [];
                renderEditFamilyRows();
            } catch (e) {
                console.error("Error fetching family for editing:", e);
                currentEditingFamilyMembers = [];
                renderEditFamilyRows();
            }

            document.getElementById("modalEditResidentProfile").classList.add("active");
        }

        function renderEditFamilyRows() {
            const tbody = document.getElementById("editFamilyRowsTbody");
            if (!tbody) return;
            tbody.innerHTML = "";

            if (currentEditingFamilyMembers.length === 0) {
                tbody.innerHTML = `<tr><td colspan="6" style="text-align:center; color:#94A3B8; padding:12px;">No additional family members configured yet. Click "+ Add Family Member" above.</td></tr>`;
                return;
            }

            currentEditingFamilyMembers.forEach((m, idx) => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td><input type="text" class="form-control" style="font-size:12px; padding:4px 8px;" value="${m.name || ''}" onchange="currentEditingFamilyMembers[${idx}].name = this.value" placeholder="Full Name" required></td>
                    <td>
                        <select class="form-control" style="font-size:11px; padding:4px 6px;" onchange="currentEditingFamilyMembers[${idx}].relation = this.value">
                            <option value="Family Head (Self)" ${m.relation && m.relation.includes('Head') ? 'selected' : ''}>Head (Self)</option>
                            <option value="Spouse (Wife)" ${m.relation && m.relation.includes('Spouse') ? 'selected' : ''}>Spouse</option>
                            <option value="Son" ${m.relation && m.relation.includes('Son') ? 'selected' : ''}>Son</option>
                            <option value="Daughter" ${m.relation && m.relation.includes('Daughter') ? 'selected' : ''}>Daughter</option>
                            <option value="Parent (Senior Citizen)" ${m.relation && m.relation.includes('Parent') ? 'selected' : ''}>Parent</option>
                            <option value="Other Relative" ${m.relation && m.relation.includes('Other') ? 'selected' : ''}>Other</option>
                        </select>
                    </td>
                    <td>
                        <select class="form-control" style="font-size:11px; padding:4px 6px;" onchange="currentEditingFamilyMembers[${idx}].gender = this.value; currentEditingFamilyMembers[${idx}].category = this.value.includes('Gent') ? 'GENT' : (this.value.includes('Lady') ? 'LADY' : 'CHILD');">
                            <option value="Gent" ${m.gender === 'Gent' ? 'selected' : ''}>Gent</option>
                            <option value="Lady" ${m.gender === 'Lady' ? 'selected' : ''}>Lady</option>
                            <option value="Child (Boy)" ${m.gender === 'Child (Boy)' ? 'selected' : ''}>Child (Boy)</option>
                            <option value="Child (Girl)" ${m.gender === 'Child (Girl)' ? 'selected' : ''}>Child (Girl)</option>
                        </select>
                    </td>
                    <td><input type="number" class="form-control" style="font-size:12px; width:60px; padding:4px 6px;" value="${m.age || 30}" onchange="currentEditingFamilyMembers[${idx}].age = parseInt(this.value) || 0" min="1" max="110"></td>
                    <td>
                        <select class="form-control" style="font-size:11px; padding:4px 6px;" onchange="currentEditingFamilyMembers[${idx}].blood_group = this.value">
                            <option value="O+" ${m.blood_group === 'O+' ? 'selected' : ''}>O+</option>
                            <option value="A+" ${m.blood_group === 'A+' ? 'selected' : ''}>A+</option>
                            <option value="B+" ${m.blood_group === 'B+' ? 'selected' : ''}>B+</option>
                            <option value="AB+" ${m.blood_group === 'AB+' ? 'selected' : ''}>AB+</option>
                            <option value="O-" ${m.blood_group === 'O-' ? 'selected' : ''}>O-</option>
                            <option value="A-" ${m.blood_group === 'A-' ? 'selected' : ''}>A-</option>
                        </select>
                    </td>
                    <td>
                        <button type="button" onclick="removeFamilyMemberRow(${idx})" style="background:#FEE2E2; color:#DC2626; border:none; border-radius:4px; padding:3px 8px; cursor:pointer; font-weight:700;">✕</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }

        function addFamilyMemberRow() {
            currentEditingFamilyMembers.push({
                name: "",
                relation: "Son",
                gender: "Child (Boy)",
                category: "CHILD",
                age: 8,
                phone: "Family Shared",
                blood_group: "O+",
                school: "School / College"
            });
            renderEditFamilyRows();
        }

        function removeFamilyMemberRow(idx) {
            currentEditingFamilyMembers.splice(idx, 1);
            renderEditFamilyRows();
        }

        async function handleSaveProfileUpdate(e) {
            e.preventDefault();
            const flat = document.getElementById("editProfileFlatNumber").value;
            const name = document.getElementById("editProfileName").value.trim();
            const phone = document.getElementById("editProfilePhone").value.trim();
            const email = document.getElementById("editProfileEmail").value.trim();
            const emergency = document.getElementById("editProfileEmergency").value.trim();
            const vehicle = document.getElementById("editProfileVehicle").value.trim();
            const password = document.getElementById("editProfilePassword").value.trim();

            const payload = {
                flat_number: flat,
                resident_name: name,
                contact_number: phone,
                email: email,
                emergency_contact: emergency,
                vehicle_number: vehicle,
                members: currentEditingFamilyMembers
            };
            if (password) payload.password = password;

            try {
                const res = await fetch("/api/profile/update", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
                const data = await res.json();
                if (data.success) {
                    currentUser.name = name;
                    currentUser.phone = phone;
                    currentUser.email = email;
                    currentUser.vehicle_number = vehicle;
                    currentUser.emergency_contact = emergency;

                    closeModal("modalEditResidentProfile");
                    showToast("Profile & Family Configuration updated successfully!", "✅");
                    applyRolePermissions();
                    loadResidentOwnFamilyProfile();
                } else {
                    alert(data.message || "Failed to update profile.");
                }
            } catch (err) {
                console.error("Profile update error:", err);
                alert("Error saving profile changes.");
            }
        }
'''

html = html.replace('</script>', profile_and_gps_js + '\n</script>', 1)
print("Injected Profile editing & Location switcher JS into index.html")

with open('index.html', 'w', encoding='utf-8') as f:
    f.write(html)
print("Successfully saved all updates to index.html")
