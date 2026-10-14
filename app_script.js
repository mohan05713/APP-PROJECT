
        // Global App State
        let currentUser = {
            username: "flat_402",
            name: "Mohan Palaniappan",
            role: "RESIDENT",
            flat: "B-402",
            phone: "+91 6380244185"
        };

        let directoryState = {
            page: 1,
            limit: 25,
            total: 400,
            search: "",
            block: "ALL",
            floor: "ALL",
            status: "ALL"
        };

        let buildingMatrixData = null;

        // Initialize App on DOM Ready
        document.addEventListener("DOMContentLoaded", () => {
            loadDashboard();
            loadDirectory();
            loadBuildingMatrix();
            loadComplaints();
            loadDues();
            loadNotices();
            loadVisitors();
            loadTelemetry();
            loadAnomalies();
            loadSupabaseSchema();

            // Refresh IoT telemetry every 6 seconds
            setInterval(loadTelemetry, 6000);
        });

        // Navigation Switcher
        function switchPage(pageId) {
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

            // Update topbar title
            const titles = {
                dashboard: "Main Dashboard",
                directory: "Resident Directory (400 Flats)",
                matrix: "Block & Flat Matrix",
                complaints: "Complaints & Helpdesk",
                technician: "Technician Dispatch & Work Logs",
                dues: "Maintenance Dues & Utility Billing",
                notices: "Community Notice Board",
                visitors: "Visitor Management & Gate Pass",
                water: "Water Resource Monitoring",
                environment: "Air & Solar Telemetry",
                flood: "Flood & Stormwater Monitoring",
                lpg: "Smart LPG Manifold",
                anomalies: "Resource Anomaly Detection History",
                reports: "Reports & Data Exports",
                supabase: "Supabase Cloud Database & Schema",
                login: "Session & Authentication"
            };
            document.getElementById("topbarTitle").innerText = titles[pageId] || "EcoApartment OS";

            if (window.innerWidth <= 900) {
                document.getElementById("sidebar").classList.remove("open");
            }
        }

        function toggleSidebar() {
            document.getElementById("sidebar").classList.toggle("open");
        }

        function showToast(message, icon = "🌿") {
            const toast = document.getElementById("toastBox");
            document.getElementById("toastIcon").innerText = icon;
            document.getElementById("toastMessage").innerText = message;
            toast.classList.add("show");
            setTimeout(() => toast.classList.remove("show"), 3500);
        }

        function closeModal(id) {
            document.getElementById(id).classList.remove("active");
        }

        // PROFILE SWITCHER
        function handleProfileSwitch(val) {
            const profiles = {
                "admin": { username: "admin", name: "SRM Facility Admin", role: "AUTHORITY", flat: "Admin Office" },
                "flat_402": { username: "flat_402", name: "Mohan Palaniappan", role: "RESIDENT", flat: "B-402" },
                "flat_101": { username: "flat_101", name: "R. Sham Sudhan", role: "RESIDENT", flat: "A-101" },
                "tech_plumber": { username: "tech_plumber", name: "R. Kathir (Plumbing)", role: "TECHNICIAN", flat: "Service Bay 1" },
                "tech_electrician": { username: "tech_electrician", name: "V. Anbu (Electrical)", role: "TECHNICIAN", flat: "Service Bay 2" }
            };
            const p = profiles[val] || profiles["flat_402"];
            currentUser = p;
            document.getElementById("userNameDisplay").innerText = p.name;
            document.getElementById("userRoleDisplay").innerText = `${p.role} • ${p.flat}`;
            document.getElementById("userAvatar").innerText = p.name.charAt(0);
            showToast(`Switched active profile to ${p.name} (${p.flat})`, "👤");
        }

        // ==============================================================
        // DASHBOARD & TELEMETRY
        // ==============================================================
        async function loadDashboard() {
            try {
                const res = await fetch("/api/complaints");
                const complaints = await res.json();
                const container = document.getElementById("dashTicketsList");
                container.innerHTML = "";
                
                complaints.slice(0, 3).forEach(c => {
                    const badgeClass = c.priority === "CRITICAL" ? "badge-danger" : (c.priority === "URGENT" ? "badge-warning" : "badge-info");
                    container.innerHTML += `
                        <div style="padding: 12px; border: 1px solid var(--border-light); border-radius: var(--radius-sm); display: flex; justify-content: space-between; align-items: center;">
                            <div>
                                <div style="font-weight: 700; font-size: 13px;">${c.ticket_id} • ${c.flat_number} (${c.category})</div>
                                <div style="font-size: 11px; color: var(--text-muted);">${c.description}</div>
                            </div>
                            <span class="badge ${badgeClass}">${c.status}</span>
                        </div>
                    `;
                });
            } catch (e) {
                console.error("Dashboard load failed", e);
            }
        }

        async function loadTelemetry() {
            try {
                const res = await fetch("/api/iot");
                const data = await res.json();
                document.getElementById("dashWaterTank").innerText = data.water_tank_level_pct + "%";
                document.getElementById("dashSolar").innerText = data.solar_generation_kw + " kW";
                document.getElementById("waterOhtVal").innerText = data.water_tank_level_pct + "%";
                document.getElementById("waterSumpVal").innerText = data.sump_level_pct + "%";
                document.getElementById("inflowRateDisplay").innerText = data.inflow_rate_lpm + " L/min";
                document.getElementById("outflowRateDisplay").innerText = data.outflow_rate_lpm + " L/min";
                document.getElementById("solarValDisplay").innerText = data.solar_generation_kw + " kW";
                document.getElementById("tempValDisplay").innerText = data.temperature_celsius + "°C";
                document.getElementById("humidityValDisplay").innerText = `Humidity: ${data.humidity_percentage}% Comfortable`;
            } catch (e) {}
        }

        // ==============================================================
        // RESIDENT DIRECTORY (400 FLATS)
        // ==============================================================
        async function loadDirectory() {
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

                if (data.residents.length === 0) {
                    tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: var(--text-muted); padding: 30px;">No residents match current search and filter criteria.</td></tr>`;
                } else {
                    data.residents.forEach(r => {
                        const occBadge = r.occupancy_status === "OCCUPIED" ? "badge-success" : (r.occupancy_status === "VACANT" ? "badge-neutral" : "badge-warning");
                        const dueBadge = r.dues_status === "PAID" ? "badge-success" : (r.dues_status === "PENDING" ? "badge-warning" : "badge-danger");
                        tbody.innerHTML += `
                            <tr>
                                <td><strong style="color: var(--primary);">${r.flat_number}</strong></td>
                                <td><strong>${r.resident_name}</strong></td>
                                <td>${r.contact_number}</td>
                                <td style="font-size: 11px; color: var(--text-muted);">${r.email || "—"}</td>
                                <td><span class="badge ${occBadge}">${r.occupancy_status}</span></td>
                                <td><span class="badge ${dueBadge}">${r.dues_status}</span></td>
                                <td style="text-align: right;">
                                    <button class="btn btn-outline btn-sm" onclick="editResident('${r.flat_number}')">Edit</button>
                                    <button class="btn btn-outline btn-sm" style="color: var(--danger);" onclick="vacateResident('${r.flat_number}')">Vacate</button>
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
                document.getElementById("btnNextPage").disabled = (data.page >= data.total_pages);
            } catch (e) {
                console.error("Directory fetch error", e);
            }
        }

        function handleDirectoryFilter() {
            directoryState.search = document.getElementById("dirSearchInput").value;
            directoryState.block = document.getElementById("dirBlockFilter").value;
            directoryState.floor = document.getElementById("dirFloorFilter").value;
            directoryState.status = document.getElementById("dirStatusFilter").value;
            directoryState.page = 1;
            loadDirectory();
        }

        function resetDirectoryFilter() {
            document.getElementById("dirSearchInput").value = "";
            document.getElementById("dirBlockFilter").value = "ALL";
            document.getElementById("dirFloorFilter").value = "ALL";
            document.getElementById("dirStatusFilter").value = "ALL";
            handleDirectoryFilter();
        }

        function changeDirectoryPage(delta) {
            directoryState.page = Math.max(1, directoryState.page + delta);
            loadDirectory();
        }

        // ==============================================================
        // BUILDING MATRIX
        // ==============================================================
        async function loadBuildingMatrix() {
            try {
                const res = await fetch("/api/flats/matrix");
                buildingMatrixData = await res.json();
                renderBuildingMatrix("A");
            } catch (e) {
                console.error("Matrix load failed", e);
            }
        }

        function switchBlockTab(blockId) {
            document.querySelectorAll(".matrix-tab").forEach(t => {
                t.classList.remove("active");
                if (t.innerText.includes("Block " + blockId)) t.classList.add("active");
            });
            renderBuildingMatrix(blockId);
        }

        function renderBuildingMatrix(blockId) {
            if (!buildingMatrixData || !buildingMatrixData[blockId]) return;
            const container = document.getElementById("buildingMatrixContainer");
            container.innerHTML = "";

            [4, 3, 2, 1].forEach(floorNum => {
                const flats = buildingMatrixData[blockId][floorNum] || [];
                let flatsHtml = "";
                flats.forEach(f => {
                    const cls = f.occupancy_status === "OCCUPIED" ? "occupied" : (f.occupancy_status === "VACANT" ? "vacant" : "maint");
                    flatsHtml += `
                        <div class="flat-box ${cls}" onclick="editResident('${f.flat_number}')" title="${f.flat_number}: ${f.resident_name} (${f.occupancy_status})">
                            <div>${f.flat_number}</div>
                        </div>
                    `;
                });

                container.innerHTML += `
                    <div class="floor-row">
                        <div class="floor-title">Floor ${floorNum} • 25 Units</div>
                        <div class="flats-grid">${flatsHtml}</div>
                    </div>
                `;
            });
        }

        // ==============================================================
        // RESIDENT CRUD
        // ==============================================================
        function openAddResidentModal() {
            document.getElementById("modalResidentTitle").innerText = "➕ Add New Resident Profile";
            document.getElementById("resFlatNumber").value = "";
            document.getElementById("resName").value = "";
            document.getElementById("resPhone").value = "";
            document.getElementById("resEmail").value = "";
            document.getElementById("resVehicle").value = "";
            document.getElementById("modalResident").classList.add("active");
        }

        async function editResident(flatNumber) {
            try {
                const res = await fetch("/api/residents/" + encodeURIComponent(flatNumber));
                const r = await res.json();
                if (r) {
                    document.getElementById("modalResidentTitle").innerText = `✏️ Edit Resident Profile (${r.flat_number})`;
                    document.getElementById("resBlock").value = r.block;
                    document.getElementById("resFloor").value = r.floor_number;
                    document.getElementById("resFlatNumber").value = r.flat_number;
                    document.getElementById("resName").value = r.resident_name;
                    document.getElementById("resPhone").value = r.contact_number;
                    document.getElementById("resEmail").value = r.email;
                    document.getElementById("resOccupancy").value = r.occupancy_status;
                    document.getElementById("resVehicle").value = r.vehicle_number;
                    document.getElementById("modalResident").classList.add("active");
                }
            } catch (e) {
                showToast("Could not load resident details", "❌");
            }
        }

        async function handleSaveResident(e) {
            e.preventDefault();
            const payload = {
                block: document.getElementById("resBlock").value,
                floor_number: document.getElementById("resFloor").value,
                flat_number: document.getElementById("resFlatNumber").value,
                resident_name: document.getElementById("resName").value,
                contact_number: document.getElementById("resPhone").value,
                email: document.getElementById("resEmail").value,
                occupancy_status: document.getElementById("resOccupancy").value,
                vehicle_number: document.getElementById("resVehicle").value
            };

            const res = await fetch("/api/residents", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            const data = await res.json();
            if (data.success) {
                closeModal("modalResident");
                showToast(`Saved resident profile for Flat ${payload.flat_number}!`, "✅");
                loadDirectory();
                loadBuildingMatrix();
            } else {
                alert(data.error || "Failed to save resident.");
            }
        }

        async function vacateResident(flatNumber) {
            if (!confirm(`Are you sure you want to vacate Flat ${flatNumber}? This marks the flat as VACANT and archives the occupant.`)) return;
            const res = await fetch("/api/residents/" + encodeURIComponent(flatNumber), { method: "DELETE" });
            const data = await res.json();
            if (data.success) {
                showToast(`Flat ${flatNumber} is now marked as VACANT.`, "📦");
                loadDirectory();
                loadBuildingMatrix();
            }
        }

        // ==============================================================
        // COMPLAINTS & TICKETS
        // ==============================================================
        async function loadComplaints() {
            const cat = document.getElementById("complaintCategoryFilter") ? document.getElementById("complaintCategoryFilter").value : "ALL";
            const status = document.getElementById("complaintStatusFilter") ? document.getElementById("complaintStatusFilter").value : "ALL";

            try {
                const res = await fetch(`/api/complaints?category=${cat}&status=${status}`);
                const list = await res.json();
                const tbody = document.getElementById("complaintsTableBody");
                tbody.innerHTML = "";

                list.forEach(c => {
                    const badgeClass = c.status === "RESOLVED" ? "badge-success" : (c.status === "IN_PROGRESS" ? "badge-warning" : "badge-info");
                    tbody.innerHTML += `
                        <tr>
                            <td><strong style="color: var(--primary);">${c.ticket_id}</strong></td>
                            <td><strong>${c.flat_number}</strong></td>
                            <td>${c.category}</td>
                            <td><span class="badge ${c.priority === 'CRITICAL' ? 'badge-danger' : 'badge-warning'}">${c.priority}</span></td>
                            <td style="font-size: 12px;">${c.description}</td>
                            <td><span class="badge ${badgeClass}">${c.status}</span></td>
                            <td style="font-size: 11px; color: var(--text-muted);">${c.assigned_technician}</td>
                            <td style="text-align: right;">
                                ${c.status !== 'RESOLVED' ? `<button class="btn btn-primary btn-sm" onclick="advanceTicketStatus('${c.ticket_id}', 'RESOLVED')">Resolve</button>` : `<span style="color: var(--success); font-size: 11px; font-weight: 700;">✓ Completed</span>`}
                            </td>
                        </tr>
                    `;
                });

                // Load technician work logs
                const logsCont = document.getElementById("technicianWorkLogsContainer");
                if (logsCont) {
                    logsCont.innerHTML = "";
                    list.forEach(c => {
                        logsCont.innerHTML += `
                            <div style="padding: 14px; border: 1px solid var(--border-light); border-radius: var(--radius-sm); background: #FFFFFF; display: flex; justify-content: space-between; align-items: center;">
                                <div>
                                    <div style="font-weight: 700; font-size: 13px;">${c.ticket_id} — ${c.assigned_technician}</div>
                                    <div style="font-size: 12px; color: var(--text-muted);">${c.work_log || c.authority_notes}</div>
                                </div>
                                <span class="badge ${c.status === 'RESOLVED' ? 'badge-success' : 'badge-warning'}">${c.status}</span>
                            </div>
                        `;
                    });
                }
            } catch (e) {
                console.error("Complaints load error", e);
            }
        }

        function openRaiseComplaintModal() {
            document.getElementById("cmpFlat").value = currentUser.flat;
            document.getElementById("modalComplaint").classList.add("active");
        }

        async function handleSaveComplaint(e) {
            e.preventDefault();
            const payload = {
                flat_number: document.getElementById("cmpFlat").value,
                floor_number: parseInt(document.getElementById("cmpFloor").value),
                category: document.getElementById("cmpCategory").value,
                priority: document.getElementById("cmpPriority").value,
                description: document.getElementById("cmpDesc").value,
                raised_by_name: currentUser.name
            };

            const res = await fetch("/api/complaints", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            const data = await res.json();
            if (data.success) {
                closeModal("modalComplaint");
                showToast(`Ticket ${data.ticket_id} logged and dispatched to technician!`, "🛠️");
                loadComplaints();
                loadDashboard();
            }
        }

        async function advanceTicketStatus(ticketId, newStatus) {
            const res = await fetch("/api/complaints/" + ticketId, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ status: newStatus, notes: "Verified and closed by resident / supervisor." })
            });
            const data = await res.json();
            if (data.success) {
                showToast(`Ticket ${ticketId} status updated to ${newStatus}.`, "✅");
                loadComplaints();
                loadDashboard();
            }
        }

        // ==============================================================
        // MAINTENANCE DUES & BILLING
        // ==============================================================
        async function loadDues() {
            try {
                const res = await fetch("/api/dues");
                const dues = await res.json();
                const tbody = document.getElementById("duesTableBody");
                tbody.innerHTML = "";

                dues.forEach(d => {
                    const badgeClass = d.status === "PAID" ? "badge-success" : (d.status === "PENDING" ? "badge-warning" : "badge-danger");
                    tbody.innerHTML += `
                        <tr>
                            <td><strong style="color: var(--primary);">${d.id}</strong></td>
                            <td><strong>${d.flat_number}</strong></td>
                            <td>${d.month_year}</td>
                            <td><strong>₹${d.amount_rupees.toFixed(2)}</strong></td>
                            <td>${d.due_date}</td>
                            <td><span class="badge ${badgeClass}">${d.status}</span></td>
                            <td style="font-size: 11px;">${d.paid_date || "—"}</td>
                            <td style="font-size: 11px;">${d.payment_method || "—"}</td>
                            <td style="text-align: right;">
                                ${d.status !== 'PAID' ? `<button class="btn btn-primary btn-sm" onclick="payDue('${d.id}')">Pay Now</button>` : `<button class="btn btn-outline btn-sm" onclick="showReceipt('${d.id}', '${d.flat_number}', ${d.amount_rupees})">Receipt</button>`}
                            </td>
                        </tr>
                    `;
                });
            } catch (e) {
                console.error("Dues load failed", e);
            }
        }

        async function payDue(dueId) {
            const method = prompt("Select payment mode:\n1. UPI (GPay / PhonePe)\n2. Net Banking\n3. Debit Card", "UPI (GPay)");
            if (!method) return;

            const res = await fetch("/api/dues/pay", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ due_id: dueId, payment_method: method })
            });
            const data = await res.json();
            if (data.success) {
                showToast(`Payment successful! Receipt ref: ${dueId}`, "💳");
                loadDues();
                loadDirectory();
            }
        }

        function showReceipt(dueId, flat, amount) {
            alert(`=========================================\nSRM GREEN RESIDENCY - PAYMENT RECEIPT\n=========================================\nReceipt ID: ${dueId}\nFlat: ${flat}\nAmount Paid: ₹${amount.toFixed(2)}\nStatus: FULLY SETTLED\nThank you for prompt community maintenance!\n=========================================`);
        }

        // ==============================================================
        // COMMUNITY NOTICES
        // ==============================================================
        async function loadNotices() {
            try {
                const res = await fetch("/api/notices");
                const notices = await res.json();
                const container = document.getElementById("noticesContainer");
                container.innerHTML = "";

                notices.forEach(n => {
                    const priorityClass = n.priority === "URGENT" ? "badge-danger" : (n.priority === "HIGH" ? "badge-warning" : "badge-info");
                    container.innerHTML += `
                        <div style="padding: 20px; border: 1px solid var(--border-light); border-radius: var(--radius-md); background: #FFFFFF; position: relative; box-shadow: var(--shadow-sm);">
                            ${n.is_pinned ? `<span style="position: absolute; top: 16px; right: 16px; font-size: 14px;">📌 Pinned</span>` : ''}
                            <div style="display: flex; gap: 8px; margin-bottom: 8px;">
                                <span class="badge ${priorityClass}">${n.priority}</span>
                                <span class="badge badge-neutral">${n.category}</span>
                            </div>
                            <div style="font-weight: 800; font-size: 15px; margin-bottom: 8px; color: var(--text-heading);">${n.title}</div>
                            <div style="font-size: 12px; color: var(--text-body); line-height: 1.6; margin-bottom: 12px;">${n.description}</div>
                            <div style="font-size: 11px; color: var(--text-muted); border-top: 1px solid var(--border-subtle); padding-top: 8px;">
                                Posted by <strong>${n.author}</strong> • ${n.posted_time}
                            </div>
                        </div>
                    `;
                });
            } catch (e) {
                console.error("Notices load error", e);
            }
        }

        function openPostNoticeModal() {
            document.getElementById("modalNotice").classList.add("active");
        }

        async function handleSaveNotice(e) {
            e.preventDefault();
            const payload = {
                title: document.getElementById("notTitle").value,
                category: document.getElementById("notCategory").value,
                priority: document.getElementById("notPriority").value,
                description: document.getElementById("notDesc").value,
                author: currentUser.name
            };

            const res = await fetch("/api/notices", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            const data = await res.json();
            if (data.success) {
                closeModal("modalNotice");
                showToast("Notice broadcasted to SRM Green community board!", "📢");
                loadNotices();
            }
        }

        // ==============================================================
        // VISITOR GATE PASS
        // ==============================================================
        async function loadVisitors() {
            try {
                const res = await fetch("/api/visitors");
                const visitors = await res.json();
                const tbody = document.getElementById("visitorsTableBody");
                tbody.innerHTML = "";

                visitors.forEach(v => {
                    const badgeClass = v.approval_status === "CHECKED_IN" ? "badge-success" : (v.approval_status === "APPROVED" ? "badge-info" : "badge-neutral");
                    tbody.innerHTML += `
                        <tr>
                            <td><strong style="color: var(--primary); font-family: monospace;">${v.pass_code}</strong></td>
                            <td><strong>${v.name}</strong><br><span style="font-size: 11px; color: var(--text-muted);">${v.phone}</span></td>
                            <td><span class="badge badge-neutral">${v.visitor_type}</span></td>
                            <td><strong>${v.flat_number}</strong></td>
                            <td>${v.vehicle_number || "Walk-in"}</td>
                            <td>${v.entry_time}</td>
                            <td><span class="badge ${badgeClass}">${v.approval_status}</span></td>
                            <td style="text-align: right;">
                                ${v.approval_status === 'APPROVED' ? `<button class="btn btn-primary btn-sm" onclick="updateVisitorStatus('${v.id}', 'CHECKED_IN')">Check In</button>` : (v.approval_status === 'CHECKED_IN' ? `<button class="btn btn-outline btn-sm" onclick="updateVisitorStatus('${v.id}', 'CHECKED_OUT')">Check Out</button>` : `<span style="font-size: 11px; color: var(--text-muted);">Exited</span>`)}
                            </td>
                        </tr>
                    `;
                });
            } catch (e) {
                console.error("Visitors load error", e);
            }
        }

        function openGatePassModal() {
            document.getElementById("visFlat").value = currentUser.flat;
            document.getElementById("modalGatePass").classList.add("active");
        }

        async function handleSaveGatePass(e) {
            e.preventDefault();
            const payload = {
                name: document.getElementById("visName").value,
                phone: document.getElementById("visPhone").value,
                visitor_type: document.getElementById("visType").value,
                flat_number: document.getElementById("visFlat").value,
                vehicle_number: document.getElementById("visVehicle").value,
                purpose: document.getElementById("visPurpose").value,
                approved_by: currentUser.name
            };

            const res = await fetch("/api/visitors", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            const data = await res.json();
            if (data.success) {
                closeModal("modalGatePass");
                showToast(`Pre-approved gate pass ${data.pass_code} generated!`, "🛡️");
                loadVisitors();
            }
        }

        async function updateVisitorStatus(visitorId, status) {
            const res = await fetch(`/api/visitors/${visitorId}/check`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ status })
            });
            const data = await res.json();
            if (data.success) {
                showToast(`Visitor status updated to ${status}`, "🛡️");
                loadVisitors();
            }
        }

        // ==============================================================
        // IOT CONTROLS (PUMP, GAS, ANOMALY SCAN)
        // ==============================================================
        async function togglePumpAction() {
            const res = await fetch("/api/pump/toggle", { method: "POST" });
            const data = await res.json();
            document.getElementById("pumpModeDisplay").innerText = data.mode;
            document.getElementById("pumpStateSubtext").innerText = data.running ? "Running • Pressure 3.8 Bar" : "Standby • Motor Off";
            showToast(`Pump mode set to: ${data.mode} (${data.status})`, "💧");
        }

        async function switchGasCylinder() {
            const res = await fetch("/api/gas/switch", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ unit_id: 2 })
            });
            const data = await res.json();
            if (data.success) {
                document.getElementById("cyl1StatusBadge").innerText = "STANDBY";
                document.getElementById("cyl1StatusBadge").className = "badge badge-info";
                document.getElementById("cyl2StatusBadge").innerText = "ACTIVE";
                document.getElementById("cyl2StatusBadge").className = "badge badge-success";
                showToast("Gas supply manifold switched over to Cylinder 2 reserve!", "🔥");
            }
        }

        async function bookRefill(unitId) {
            const res = await fetch("/api/gas/book", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ unit_id: unitId })
            });
            const data = await res.json();
            if (data.success) {
                showToast(`Refill booked! Ref ID: ${data.booking_id}`, "📦");
            }
        }

        async function loadAnomalies() {
            try {
                const res = await fetch("/api/anomalies");
                const list = await res.json();
                const tbody = document.getElementById("anomaliesTableBody");
                tbody.innerHTML = "";

                list.forEach(a => {
                    tbody.innerHTML += `
                        <tr>
                            <td><strong style="color: var(--primary); font-family: monospace;">${a.sensor_id}</strong></td>
                            <td><strong>${a.zone_id}</strong></td>
                            <td><strong style="color: var(--danger);">${a.flow_rate} L/min</strong></td>
                            <td>${a.threshold} L/min</td>
                            <td><span class="badge ${a.status === 'RESOLVED' ? 'badge-success' : 'badge-danger'}">${a.status}</span></td>
                            <td style="font-size: 11px;">${a.timestamp}</td>
                            <td style="font-size: 11px; color: var(--text-muted);">${a.notes}</td>
                        </tr>
                    `;
                });
            } catch (e) {
                console.error("Anomalies load error", e);
            }
        }

        async function runLiveAnomalyScan() {
            showToast("Executing WaterMeter.detect_anomaly() algorithmic scan...", "⚙️");
            const res = await fetch("/api/anomalies/scan", { method: "POST" });
            const data = await res.json();
            if (data.anomaly_detected) {
                showToast(`🚨 Critical Leak Spike Detected in ${data.zone_id}! Flow: ${data.flow_rate} L/min`, "⚠️");
            } else {
                showToast("All 400 flats flow telemetry normal. Zero leaks detected.", "✅");
            }
            loadAnomalies();
        }

        // ==============================================================
        // SUPABASE SCHEMA & CONFIG
        // ==============================================================
        async function loadSupabaseSchema() {
            try {
                const res = await fetch("/api/supabase/schema");
                const text = await res.text();
                document.getElementById("supabaseSchemaText").value = text;
            } catch (e) {}
        }

        function copySupabaseSchema() {
            const copyText = document.getElementById("supabaseSchemaText");
            copyText.select();
            document.execCommand("copy");
            showToast("Copied full supabase_schema.sql to clipboard! Ready to paste in Supabase SQL Editor.", "📋");
        }

        async function saveSupabaseConfig() {
            const payload = {
                project_url: document.getElementById("supabaseUrlInput").value,
                anon_key: document.getElementById("supabaseKeyInput").value
            };
            const res = await fetch("/api/supabase/config", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            const data = await res.json();
            if (data.success) {
                document.getElementById("supabaseStatusBadge").innerText = "Connected to Supabase PostgreSQL";
                showToast("Supabase credentials saved and verified!", "⚡");
            }
        }

        // Manual Login
        async function handleManualLogin(e) {
            e.preventDefault();
            const uname = document.getElementById("loginUsername").value;
            const pwd = document.getElementById("loginPassword").value;
            const role = document.getElementById("loginRoleSelect").value;

            const res = await fetch("/api/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username: uname, password: pwd, role: role })
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
                document.getElementById("userNameDisplay").innerText = currentUser.name;
                document.getElementById("userRoleDisplay").innerText = `${currentUser.role} • ${currentUser.flat}`;
                document.getElementById("userAvatar").innerText = currentUser.name.charAt(0);
                showToast(`Welcome, ${currentUser.name}! Signed in successfully.`, "✅");
                switchPage("dashboard");
            } else {
                alert(data.message || "Invalid credentials.");
            }
        }
    