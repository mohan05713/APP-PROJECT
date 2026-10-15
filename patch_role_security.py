import re

with open("index.html", "r", encoding="utf-8") as f:
    html = f.read()

# Update openAddResidentModal, editResident, vacateResident
old_crud = """        function openAddResidentModal() {
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

        async function vacateResident(flatNumber) {
            if (!confirm(`Are you sure you want to vacate Flat ${flatNumber}? This marks the flat as VACANT and archives the occupant.`)) return;
            const res = await fetch("/api/residents/" + encodeURIComponent(flatNumber), { method: "DELETE" });
            const data = await res.json();
            if (data.success) {
                showToast(`Flat ${flatNumber} is now marked as VACANT.`, "📦");
                loadDirectory();
                loadBuildingMatrix();
            }
        }"""

new_crud = """        function checkAdminAccess() {
            const isAdmin = (currentUser && (currentUser.role === 'AUTHORITY' || currentUser.role === 'ADMIN' || currentUser.username === 'admin'));
            if (!isAdmin) {
                showToast("🔒 Access Restricted: Residential portal is read-only. Only Admin Office can add or edit residents.", "⛔");
                return false;
            }
            return true;
        }

        function openAddResidentModal() {
            if (!checkAdminAccess()) return;
            document.getElementById("modalResidentTitle").innerText = "➕ Add New Resident Profile";
            document.getElementById("resFlatNumber").value = "";
            document.getElementById("resName").value = "";
            document.getElementById("resPhone").value = "";
            document.getElementById("resEmail").value = "";
            document.getElementById("resVehicle").value = "";
            document.getElementById("modalResident").classList.add("active");
        }

        async function editResident(flatNumber) {
            const isAdmin = (currentUser && (currentUser.role === 'AUTHORITY' || currentUser.role === 'ADMIN' || currentUser.username === 'admin'));
            if (!isAdmin) {
                showToast("🔒 Access Restricted: Residential portal is read-only. Only Admin Office can edit resident profiles.", "ℹ️");
                return;
            }
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

        async function vacateResident(flatNumber) {
            if (!checkAdminAccess()) return;
            if (!confirm(`Are you sure you want to vacate Flat ${flatNumber}? This marks the flat as VACANT and archives the occupant.`)) return;
            const res = await fetch("/api/residents/" + encodeURIComponent(flatNumber), { method: "DELETE" });
            const data = await res.json();
            if (data.success) {
                showToast(`Flat ${flatNumber} is now marked as VACANT.`, "📦");
                loadDirectory();
                loadBuildingMatrix();
            }
        }"""

if old_crud in html:
    html = html.replace(old_crud, new_crud, 1)
    print("Updated CRUD functions to enforce Admin-only permissions and Resident read-only mode")

with open("index.html", "w", encoding="utf-8") as f:
    f.write(html)

print("index.html successfully updated with Role-Based Access Control!")
