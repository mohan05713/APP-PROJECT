with open("index.html", "r", encoding="utf-8") as f:
    html = f.read()

old_logout = """        function handleLogout() {
            if (confirm("Are you sure you want to log out of EcoApartment OS?")) {
                showToast("Logged out successfully. Redirecting to login page...", "🚪");
                switchPage("login");
            }
        }"""

new_logout = """        function handleLogout() {
            currentUser = null;
            if (document.getElementById("userNameDisplay")) document.getElementById("userNameDisplay").innerText = "Guest User";
            if (document.getElementById("userRoleDisplay")) document.getElementById("userRoleDisplay").innerText = "Sign In Required";
            if (document.getElementById("userAvatar")) document.getElementById("userAvatar").innerText = "👤";
            showToast("Logged out successfully. Redirecting to login page...", "🚪");
            switchPage("login");
        }"""

if old_logout in html:
    html = html.replace(old_logout, new_logout)
    print("Updated handleLogout in index.html")

with open("index.html", "w", encoding="utf-8") as f:
    f.write(html)

print("index.html successfully updated!")
