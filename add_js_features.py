with open('index.html', 'r', encoding='utf-8') as f:
    html = f.read()

js_addition = '''
        // ==============================================================
        // ENHANCED ROLE-BASED ACCESS & SECURE AUTHENTICATION
        // ==============================================================
        function openAuthModal() {
            const modal = document.getElementById("modalAuthLogin");
            if (modal) {
                modal.classList.add("active");
                document.getElementById("authModalUsername").focus();
            }
        }

        function fillAuthForm(username, password) {
            document.getElementById("authModalUsername").value = username;
            document.getElementById("authModalPassword").value = password;
        }

        async function handleSecureModalLogin(e) {
            e.preventDefault();
            const uname = document.getElementById("authModalUsername").value.trim();
            const pwd = document.getElementById("authModalPassword").value.trim();

            if (!uname || !pwd) {
                alert("Please enter both username and password.");
                return;
            }

            try {
                const res = await fetch("/api/login", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ username: uname, password: pwd })
                });
                const data = await res.json();
                if (data.success) {
                    currentUser = {
                        username: data.user.username,
                        name: data.user.resident_name,
                        role: data.user.role,
                        flat: data.user.flat_number,
                        phone: data.user.contact_number
                    };
                    closeModal("modalAuthLogin");
                    applyRolePermissions();
                    showToast(`Signed in securely as ${currentUser.name} (${currentUser.role})`, "🔐");
                    switchPage("dashboard");
                } else {
                    alert(data.message || "Invalid username or password. Please verify credentials.");
                }
            } catch (err) {
                console.error("Login error:", err);
                alert("Error connecting to server. Please try again.");
            }
        }

        function applyRolePermissions() {
            const isAdmin = (currentUser && (currentUser.role === 'AUTHORITY' || currentUser.role === 'ADMIN' || currentUser.username === 'admin'));
            
            // 1. Update Topbar
            const roleBadgeText = document.getElementById("roleBadgeText");
            if (roleBadgeText) {
                if (isAdmin) {
                    roleBadgeText.innerText = "⚡ Admin Authority Mode";
                    roleBadgeText.style.background = "#FEF3C7";
                    roleBadgeText.style.color = "#92400E";
                } else {
                    roleBadgeText.innerText = `🔒 Resident Read-Only View (${currentUser.flat || 'A-101'})`;
                    roleBadgeText.style.background = "#E0E7FF";
                    roleBadgeText.style.color = "#3730A3";
                }
            }

            const nameEl = document.getElementById("userNameDisplay");
            if (nameEl) nameEl.innerText = currentUser.name || "Resident";
            const roleEl = document.getElementById("userRoleDisplay");
            if (roleEl) roleEl.innerText = `${currentUser.role} • ${currentUser.flat || 'Flat'}`;
            const avatarEl = document.getElementById("userAvatar");
            if (avatarEl) avatarEl.innerText = (currentUser.name || "R").charAt(0);

            // 2. Toggle Admin-Only UI Elements
            document.querySelectorAll(".admin-only-ui").forEach(el => {
                el.style.display = isAdmin ? "" : "none";
            });

            // 3. Hide mutation buttons for Resident
            const btnAddResident = document.getElementById("btnOpenAddResident") || document.querySelector("button[onclick*='openAddResidentModal']");
            if (btnAddResident) btnAddResident.style.display = isAdmin ? "" : "none";

            const btnPostNotice = document.getElementById("btnOpenPostNotice") || document.querySelector("button[onclick*='openPostNoticeModal']");
            if (btnPostNotice) btnPostNotice.style.display = isAdmin ? "" : "none";

            // 4. Resident-specific Card in Directory
            const ownCard = document.getElementById("residentOwnFamilyCard");
            if (ownCard) {
                ownCard.style.display = isAdmin ? "none" : "block";
                if (!isAdmin) {
                    loadResidentOwnFamilyProfile();
                }
            }

            // 5. Refresh current active page with role filters
            const activePage = document.querySelector(".page-view.active");
            if (activePage) {
                const pageId = activePage.id.replace("page-", "");
                if (pageId === "dues") loadDues();
                if (pageId === "visitors") loadVisitors();
                if (pageId === "parking") loadParking();
            }
        }

        // ==============================================================
        // FAMILY CENSUS LOGIC (ALL 400 FLATS & SPECIFIC RESIDENT)
        // ==============================================================
        async function loadResidentOwnFamilyProfile() {
            const flat = currentUser.flat || "A-101";
            try {
                const res = await fetch(`/api/family?flat=${encodeURIComponent(flat)}`);
                const data = await res.json();
                
                document.getElementById("resCardFlatBadge").innerText = `Flat ${data.flat_number || flat}`;
                document.getElementById("resCardHeadName").innerText = `${data.family_head || currentUser.name} (Head of Family)`;
                document.getElementById("resCardMeta").innerText = `Flat ${data.flat_number || flat} · Primary Resident & Owner · Contact: ${currentUser.phone || '+91 77089 94065'}`;
                
                document.getElementById("resBadgeGents").innerText = `👨 ${data.gents_count || 1} Gent${(data.gents_count || 1) > 1 ? 's' : ''}`;
                document.getElementById("resBadgeLadies").innerText = `👩 ${data.ladies_count || 1} Lad${(data.ladies_count || 1) > 1 ? 'ies' : 'y'}`;
                document.getElementById("resBadgeChildren").innerText = `👶 ${data.children_count || 0} Child${(data.children_count || 0) !== 1 ? 'ren' : ''}`;
                document.getElementById("resBadgeTotal").innerText = `👨‍👩‍👧‍👦 ${data.total_members || 4} Members Total`;

                document.getElementById("resCardParkingSlot").innerText = `Slot P-${data.flat_number || flat} (Basement ${['A','B'].includes((data.flat_number||'A')[0]) ? '1' : '2'})`;
                
                const tbody = document.getElementById("resOwnFamilyTableBody");
                if (tbody) {
                    tbody.innerHTML = "";
                    (data.members || []).forEach(m => {
                        const tr = document.createElement("tr");
                        tr.innerHTML = `
                            <td><strong>${m.name}</strong></td>
                            <td><span class="badge ${m.relation.includes('Head') ? 'badge-primary' : 'badge-neutral'}">${m.relation}</span></td>
                            <td>${m.gender}</td>
                            <td>${m.age} yrs</td>
                            <td>${m.school || m.occupation || m.phone || 'Family Member'}</td>
                            <td><strong>${m.blood_group || 'O+'}</strong></td>
                        `;
                        tbody.appendChild(tr);
                    });
                }
            } catch (e) {
                console.error("Error loading own family profile:", e);
            }
        }

        async function openFamilyModal(flatNumber) {
            try {
                const res = await fetch(`/api/family?flat=${encodeURIComponent(flatNumber)}`);
                const data = await res.json();

                document.getElementById("familyModalFlatTitle").innerText = `Flat ${data.flat_number} — Family Census Roster`;
                document.getElementById("familyModalSubtitle").innerText = `Verified SRM Green Residency Census · Flat ${data.flat_number}`;
                document.getElementById("famHeadName").innerText = data.family_head || "Resident";
                document.getElementById("famGentsCount").innerText = data.gents_count || 0;
                document.getElementById("famLadiesCount").innerText = data.ladies_count || 0;
                document.getElementById("famChildrenCount").innerText = data.children_count || 0;
                document.getElementById("famTotalCount").innerText = data.total_members || 0;

                const tbody = document.getElementById("familyMembersTableBody");
                tbody.innerHTML = "";
                (data.members || []).forEach(m => {
                    const tr = document.createElement("tr");
                    tr.innerHTML = `
                        <td><strong>${m.name}</strong></td>
                        <td><span class="badge ${m.relation.includes('Head') ? 'badge-primary' : 'badge-neutral'}">${m.relation}</span></td>
                        <td><span style="font-weight:700; color:${m.gender.includes('Gent') ? '#1D4ED8' : m.gender.includes('Lady') ? '#BE185D' : '#D97706'}">${m.gender}</span></td>
                        <td>${m.age} yrs</td>
                        <td>${m.school || m.occupation || m.phone || 'Resident'}</td>
                        <td><strong>${m.blood_group || 'O+'}</strong></td>
                    `;
                    tbody.appendChild(tr);
                });

                document.getElementById("modalFamilyRoster").classList.add("active");
            } catch (e) {
                console.error("Error fetching family roster:", e);
                alert("Failed to load family roster.");
            }
        }

        // ==============================================================
        // PARKING ALLOTMENTS LOGIC
        // ==============================================================
        let allParkingData = [];

        async function loadParking() {
            try {
                const isAdmin = (currentUser && (currentUser.role === 'AUTHORITY' || currentUser.role === 'ADMIN' || currentUser.username === 'admin'));
                const flat = currentUser ? currentUser.flat : "A-101";

                // Update highlight card for current flat
                const pRes = await fetch(`/api/parking?flat=${encodeURIComponent(flat)}`);
                const myParkingList = await pRes.json();
                if (myParkingList && myParkingList.length > 0) {
                    const p = myParkingList[0];
                    document.getElementById("myParkingSlotId").innerText = `Slot ${p.slot_id}`;
                    document.getElementById("myParkingLocation").innerText = `${p.basement} · ${p.bay} · Reserved for Flat ${p.flat_number}`;
                    document.getElementById("myParkingVehicle").innerText = p.vehicle_number;
                    document.getElementById("myParkingVehicleType").innerText = p.vehicle_type;
                    document.getElementById("myParkingEvStatus").innerText = p.ev_charging;
                    document.getElementById("myParkingRfid").innerText = p.rfid_tag;
                }

                // If Resident, hide full directory or keep it viewable
                const res = await fetch("/api/parking");
                allParkingData = await res.json();
                renderParkingTable(allParkingData);
            } catch (e) {
                console.error("Error loading parking data:", e);
            }
        }

        function filterParkingTable() {
            const q = (document.getElementById("parkingSearchInput").value || "").toLowerCase().trim();
            const basement = document.getElementById("parkingBasementFilter").value;

            const filtered = allParkingData.filter(p => {
                const matchQ = !q || p.flat_number.toLowerCase().includes(q) || p.resident_name.toLowerCase().includes(q) || (p.vehicle_number && p.vehicle_number.toLowerCase().includes(q));
                const matchB = basement === "ALL" || p.basement === basement;
                return matchQ && matchB;
            });
            renderParkingTable(filtered);
        }

        function renderParkingTable(data) {
            const tbody = document.getElementById("parkingTableBody");
            if (!tbody) return;
            tbody.innerHTML = "";

            if (!data || data.length === 0) {
                tbody.innerHTML = `<tr><td colspan="9" style="text-align:center; padding: 24px; color: var(--text-muted);">No parking allotments found matching filters.</td></tr>`;
                return;
            }

            data.slice(0, 50).forEach(p => {
                const tr = document.createElement("tr");
                const isMyFlat = (currentUser && currentUser.flat === p.flat_number);
                if (isMyFlat) tr.style.background = "#F0FDF4";

                tr.innerHTML = `
                    <td><strong>${p.slot_id}</strong> ${isMyFlat ? '<span class="badge badge-success">MY SLOT</span>' : ''}</td>
                    <td><span class="badge badge-primary">${p.flat_number}</span></td>
                    <td>${p.basement} · ${p.bay}</td>
                    <td>${p.resident_name}</td>
                    <td><strong>${p.vehicle_number}</strong></td>
                    <td><span style="font-size:12px;">${p.vehicle_type}</span></td>
                    <td><span class="badge ${p.ev_charging.includes('Yes') ? 'badge-success' : 'badge-neutral'}">${p.ev_charging}</span></td>
                    <td><code style="font-size:11px;">${p.rfid_tag}</code></td>
                    <td>
                        <button type="button" onclick="switchPage('cctv'); switchCctvCam('${p.basement.includes('1') ? 'CAM-01' : 'CAM-02'}');" class="btn btn-outline btn-sm" style="font-size:11px; padding:3px 8px;">
                            📹 View Bay
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }

        // ==============================================================
        // 24/7 CCTV SURVEILLANCE & ANIMATED CANVAS STREAM
        // ==============================================================
        let activeCamId = "CAM-01";
        let isNightVision = false;
        let cctvInterval = null;
        let cctvFrame = 0;

        const cctvCamerasList = [
            { id: "CAM-01", name: "CAM-01: Basement 1 Parking Lot", zone: "PARKING & EV CHARGING BAYS", icon: "🅿️", desc: "Covers Block A/B Driveway & 67 EV bays" },
            { id: "CAM-02", name: "CAM-02: Basement 2 Parking Lot", zone: "RESIDENT PARKING & TWO-WHEELER BAY", icon: "🛵", desc: "Covers Block C/D Entry Ramp" },
            { id: "CAM-03", name: "CAM-03: Main Gate & ANPR Boom Barrier", zone: "SOCIETY MAIN GATE & PERIMETER", icon: "🚧", desc: "Automated Number Plate Recognition" },
            { id: "CAM-04", name: "CAM-04: Tower A/B Entrance Lobby", zone: "TOWER A/B FOYER & ELEVATORS", icon: "🏢", desc: "Intercom Desk & High-Speed Lifts" },
            { id: "CAM-05", name: "CAM-05: Tower C/D Ground Lobby", zone: "TOWER C/D LOBBY & PARCEL HUB", icon: "📦", desc: "Digital Locker & Delivery Counter" },
            { id: "CAM-06", name: "CAM-06: Play Park & Garden", zone: "CENTRAL COURTYARD & PLAYGROUND", icon: "🌳", desc: "Eco-Park, Jogging Track & Gazebo" },
            { id: "CAM-07", name: "CAM-07: Rooftop Solar Array & Tank", zone: "BLOCK A-D ROOFTOP MICROGRID", icon: "☀️", desc: "120kW Solar Farm & 50KL Water Tanks" },
            { id: "CAM-08", name: "CAM-08: Clubhouse & Swimming Pool", zone: "SRM GREEN CLUB & GYMNASIUM", icon: "🏊", desc: "Pool Deck & Fitness Center Corridor" }
        ];

        function loadCCTV() {
            renderCctvGrid();
            startCctvStream();
        }

        function switchCctvCam(camId) {
            activeCamId = camId;
            const cam = cctvCamerasList.find(c => c.id === camId) || cctvCamerasList[0];
            document.getElementById("cctvHudTitle").innerText = cam.name;
            document.getElementById("cctvZoneLabel").innerText = cam.zone;

            document.querySelectorAll(".cctv-tab-btn").forEach(btn => {
                if (btn.getAttribute("data-cam") === camId) {
                    btn.classList.add("active", "btn-primary");
                    btn.classList.remove("btn-outline");
                } else {
                    btn.classList.remove("active", "btn-primary");
                    btn.classList.add("btn-outline");
                }
            });
        }

        function toggleNightVision() {
            isNightVision = !isNightVision;
            const btn = document.getElementById("btnNightVision");
            if (btn) {
                btn.style.background = isNightVision ? "#10B981" : "";
                btn.style.color = isNightVision ? "#ffffff" : "";
                btn.innerText = isNightVision ? "☀️ Daytime Filter" : "🌓 Night Vision (IR)";
            }
            showToast(`CCTV Mode: ${isNightVision ? 'Night Vision (Infrared IR)' : 'Daytime High Contrast'}`, "📹");
        }

        function takeCctvSnapshot() {
            const canvas = document.getElementById("cctvCanvas");
            if (!canvas) return;
            const link = document.createElement("a");
            link.download = `CCTV_Snapshot_${activeCamId}_${new Date().toISOString().replace(/[:.]/g, "-")}.png`;
            link.href = canvas.toDataURL("image/png");
            link.click();
            showToast(`Security snapshot saved for ${activeCamId}`, "📸");
        }

        function ptzAction(action) {
            showToast(`PTZ Executed: ${action} on ${activeCamId}`, "🧭");
        }

        function renderCctvGrid() {
            const grid = document.getElementById("cctvGridContainer");
            if (!grid) return;
            grid.innerHTML = "";

            cctvCamerasList.forEach(cam => {
                const div = document.createElement("div");
                div.style.background = "#1E293B";
                div.style.color = "white";
                div.style.borderRadius = "10px";
                div.style.padding = "14px";
                div.style.cursor = "pointer";
                div.style.border = (cam.id === activeCamId) ? "2px solid #10B981" : "1px solid #334155";
                div.onclick = () => switchCctvCam(cam.id);

                div.innerHTML = `
                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
                        <span style="font-weight:800; font-size:12px;">${cam.icon} ${cam.id}</span>
                        <span style="font-size:10px; background:#064E3B; color:#34D399; font-weight:700; padding:2px 6px; border-radius:4px;">● LIVE</span>
                    </div>
                    <div style="font-size:12px; font-weight:700; margin-bottom:4px;">${cam.name.split(':')[1]}</div>
                    <div style="font-size:11px; color:#94A3B8;">${cam.desc}</div>
                `;
                grid.appendChild(div);
            });
        }

        function startCctvStream() {
            if (cctvInterval) clearInterval(cctvInterval);
            const canvas = document.getElementById("cctvCanvas");
            if (!canvas) return;
            const ctx = canvas.getContext("2d");

            cctvInterval = setInterval(() => {
                cctvFrame++;
                const now = new Date();
                const timeStr = now.toISOString().replace("T", " ").substring(0, 19);
                const clockEl = document.getElementById("cctvLiveClock");
                if (clockEl) clockEl.innerText = timeStr;

                // Render simulated camera view
                ctx.clearRect(0, 0, canvas.width, canvas.height);

                if (isNightVision) {
                    ctx.fillStyle = "#0A2518"; // IR green tint
                } else {
                    ctx.fillStyle = "#0F172A"; // Dark slate
                }
                ctx.fillRect(0, 0, canvas.width, canvas.height);

                // Grid lines / perspective
                ctx.strokeStyle = isNightVision ? "rgba(16, 185, 129, 0.2)" : "rgba(148, 163, 184, 0.15)";
                ctx.lineWidth = 1;
                for (let x = 0; x < canvas.width; x += 60) {
                    ctx.beginPath();
                    ctx.moveTo(x, 0);
                    ctx.lineTo(x, canvas.height);
                    ctx.stroke();
                }
                for (let y = 0; y < canvas.height; y += 40) {
                    ctx.beginPath();
                    ctx.moveTo(0, y);
                    ctx.lineTo(canvas.width, y);
                    ctx.stroke();
                }

                // Render dynamic scene based on camera
                if (activeCamId === "CAM-01" || activeCamId === "CAM-02") {
                    // Parking Bay lines & animated car
                    ctx.fillStyle = isNightVision ? "#10B981" : "#3B82F6";
                    ctx.font = "14px 'JetBrains Mono', monospace";
                    ctx.fillText("BAY A-01 [EV READY]", 100, 180);
                    ctx.fillText("BAY A-02 [ALLOTTED: P-A101]", 360, 180);
                    ctx.fillText("BAY A-03 [ALLOTTED: P-B402]", 620, 180);

                    // Moving car simulation
                    const carX = (100 + (cctvFrame * 2) % 650);
                    ctx.fillStyle = isNightVision ? "#34D399" : "#10B981";
                    ctx.fillRect(carX, 260, 140, 60);
                    ctx.fillStyle = "white";
                    ctx.font = "11px 'JetBrains Mono', monospace";
                    ctx.fillText("TN-09-BE-4451", carX + 15, 295);

                    // Motion detection box
                    ctx.strokeStyle = "#EF4444";
                    ctx.lineWidth = 2;
                    ctx.strokeRect(carX - 10, 250, 160, 80);
                    ctx.fillStyle = "#EF4444";
                    ctx.fillText("MOTION: VEHICLE MOVING", carX - 10, 242);
                } else if (activeCamId === "CAM-03") {
                    // Main Gate & Barrier
                    ctx.strokeStyle = "#F59E0B";
                    ctx.lineWidth = 6;
                    const barrierAngle = Math.sin(cctvFrame * 0.05) > 0 ? 0 : -30;
                    ctx.beginPath();
                    ctx.moveTo(400, 300);
                    ctx.lineTo(600, 300 + barrierAngle);
                    ctx.stroke();

                    ctx.fillStyle = "#10B981";
                    ctx.font = "14px 'JetBrains Mono', monospace";
                    ctx.fillText("ANPR CAMERA: ACTIVE · SCANNING VEHICLE PLATES", 260, 140);
                } else {
                    // Lobby / General
                    ctx.fillStyle = isNightVision ? "#10B981" : "#E2E8F0";
                    ctx.font = "16px 'JetBrains Mono', monospace";
                    ctx.fillText(`${activeCamId}: SURVEILLANCE FEED ACTIVE · ALL RISERS SECURED`, 240, 240);
                }

                // Scanline effect
                ctx.fillStyle = "rgba(255, 255, 255, 0.03)";
                const scanY = (cctvFrame * 4) % canvas.height;
                ctx.fillRect(0, scanY, canvas.width, 4);

            }, 50);
        }

        // Initialize CCTV on document ready if on CCTV page
        window.addEventListener("DOMContentLoaded", () => {
            applyRolePermissions();
            loadCCTV();
        });
'''

# Replace right before the end of the script tag
html = html.replace('</script>', js_addition + '\n</script>', 1)
print("Injected JavaScript additions into index.html")

with open('index.html', 'w', encoding='utf-8') as f:
    f.write(html)
print("Successfully saved complete index.html")
