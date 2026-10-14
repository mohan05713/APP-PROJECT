"""
EcoApartment OS - Modern Python GUI for Visual Studio / VS Code
Built with Python 3, Tkinter & SQLite
Runs natively on Windows, macOS, and Linux
"""

try:
    import tkinter as tk
    from tkinter import ttk, messagebox
    TK_AVAILABLE = True
except ImportError:
    TK_AVAILABLE = False

import sqlite3
import datetime
import random
import os
from eco_apartment_python_app import EcoApartmentDatabase, IoTEngine, Complaint, User

# Palette
BG_COLOR = "#F8FAFC"
CARD_BG = "#FFFFFF"
PRIMARY_COLOR = "#0F766E"      # Teal 700
PRIMARY_LIGHT = "#CCFBF1"     # Teal 100
TEXT_PRIMARY = "#0F172A"      # Slate 900
TEXT_SECONDARY = "#475569"    # Slate 600
TEXT_MUTED = "#94A3B8"        # Slate 400
BORDER_COLOR = "#E2E8F0"      # Slate 200
DANGER_COLOR = "#DC2626"      # Red 600
SUCCESS_COLOR = "#16A34A"     # Green 600
WARNING_COLOR = "#D97706"     # Amber 600


class EcoApartmentGUI:
    def __init__(self, root):
        if not TK_AVAILABLE:
            raise RuntimeError("Tkinter is not installed on this system.")
        self.root = root
        self.root.title("EcoApartment OS - SRM Green Residency (Python Edition)")
        self.root.geometry("1024x720")
        self.root.minsize(900, 600)
        self.root.configure(bg=BG_COLOR)

        self.db = EcoApartmentDatabase()
        self.iot = IoTEngine()
        self.current_user = None

        self.setup_styles()
        self.show_login_screen()

    def setup_styles(self):
        self.style = ttk.Style()
        self.style.theme_use("clam")

        # Global Config
        self.style.configure(".", background=BG_COLOR, foreground=TEXT_PRIMARY, font=("Helvetica", 10))
        self.style.configure("TFrame", background=BG_COLOR)
        self.style.configure("Card.TFrame", background=CARD_BG, relief="solid", borderwidth=1)
        self.style.configure("Header.TLabel", font=("Helvetica", 16, "bold"), foreground=PRIMARY_COLOR, background=BG_COLOR)
        self.style.configure("SubHeader.TLabel", font=("Helvetica", 11), foreground=TEXT_SECONDARY, background=BG_COLOR)
        
        # Primary Action Button
        self.style.configure(
            "Primary.TButton",
            font=("Helvetica", 10, "bold"),
            background=PRIMARY_COLOR,
            foreground="#FFFFFF",
            padding=(12, 6)
        )
        self.style.map("Primary.TButton", background=[("active", "#115E59")])

        # Secondary Button
        self.style.configure(
            "Secondary.TButton",
            font=("Helvetica", 9),
            background=CARD_BG,
            foreground=TEXT_PRIMARY,
            padding=(8, 4)
        )

        # Tabs
        self.style.configure("TNotebook", background=BG_COLOR, tabmargins=[2, 5, 2, 0])
        self.style.configure("TNotebook.Tab", padding=[16, 8], font=("Helvetica", 10, "bold"), background="#E2E8F0", foreground=TEXT_SECONDARY)
        self.style.map("TNotebook.Tab", background=[("selected", CARD_BG)], foreground=[("selected", PRIMARY_COLOR)])

    def clear_screen(self):
        for widget in self.root.winfo_children():
            widget.destroy()

    # =========================================================================
    # LOGIN SCREEN
    # =========================================================================
    def show_login_screen(self):
        self.clear_screen()

        container = ttk.Frame(self.root)
        container.pack(expand=True, fill="both", padx=40, pady=30)

        # Top Branding
        title_box = ttk.Frame(container)
        title_box.pack(pady=(0, 20))
        ttk.Label(title_box, text="🌿 ECOAPARTMENT OS", style="Header.TLabel", font=("Helvetica", 20, "bold")).pack()
        ttk.Label(title_box, text="SRM Green Residency • Smart Sustainability & Facility Management", style="SubHeader.TLabel").pack(pady=4)

        # Main Login Card
        card = tk.Frame(container, bg=CARD_BG, highlightbackground=BORDER_COLOR, highlightthickness=1, padx=30, pady=25)
        card.pack(ipadx=10, ipady=10)

        # Role Selector
        self.role_var = tk.StringVar(value="RESIDENT")
        role_frame = tk.Frame(card, bg=CARD_BG)
        role_frame.pack(fill="x", pady=(0, 15))
        
        tk.Radiobutton(role_frame, text="Resident / House Login", variable=self.role_var, value="RESIDENT",
                       bg=CARD_BG, fg=TEXT_PRIMARY, font=("Helvetica", 10, "bold"), activebackground=CARD_BG).pack(side="left", padx=10)
        tk.Radiobutton(role_frame, text="Facility Authority Desk", variable=self.role_var, value="AUTHORITY",
                       bg=CARD_BG, fg=TEXT_PRIMARY, font=("Helvetica", 10, "bold"), activebackground=CARD_BG).pack(side="left", padx=10)

        # Inputs
        form_frame = tk.Frame(card, bg=CARD_BG)
        form_frame.pack(fill="x", pady=10)

        tk.Label(form_frame, text="Username / Flat Number:", bg=CARD_BG, fg=TEXT_SECONDARY, font=("Helvetica", 9, "bold")).pack(anchor="w")
        self.username_entry = tk.Entry(form_frame, font=("Helvetica", 11), bg="#F8FAFC", relief="solid", bd=1)
        self.username_entry.insert(0, "flat_402")
        self.username_entry.pack(fill="x", ipady=6, pady=(2, 10))

        tk.Label(form_frame, text="Password / Access PIN:", bg=CARD_BG, fg=TEXT_SECONDARY, font=("Helvetica", 9, "bold")).pack(anchor="w")
        self.password_entry = tk.Entry(form_frame, font=("Helvetica", 11), show="•", bg="#F8FAFC", relief="solid", bd=1)
        self.password_entry.insert(0, "SRM@F4-402")
        self.password_entry.pack(fill="x", ipady=6, pady=(2, 15))

        # Login Action Button
        btn_login = tk.Button(card, text="Secure Sign In →", bg=PRIMARY_COLOR, fg="white", font=("Helvetica", 11, "bold"),
                              relief="flat", cursor="hand2", command=self.handle_login)
        btn_login.pack(fill="x", ipady=8, pady=5)

        # Quick Autofill Buttons for Testing
        autofill_frame = tk.Frame(card, bg=CARD_BG)
        autofill_frame.pack(fill="x", pady=(15, 0))
        tk.Label(autofill_frame, text="⚡ Quick Autofill Test Accounts:", bg=CARD_BG, fg=TEXT_MUTED, font=("Helvetica", 8, "bold")).pack(anchor="w")
        
        btn_row = tk.Frame(autofill_frame, bg=CARD_BG)
        btn_row.pack(fill="x", pady=5)

        def set_acc(u, p, r):
            self.username_entry.delete(0, tk.END)
            self.username_entry.insert(0, u)
            self.password_entry.delete(0, tk.END)
            self.password_entry.insert(0, p)
            self.role_var.set(r)

        tk.Button(btn_row, text="Flat 101 (Floor 1)", bg="#F1F5F9", fg=TEXT_PRIMARY, font=("Helvetica", 8),
                  command=lambda: set_acc("flat_101", "7708994065", "RESIDENT"), relief="groove").pack(side="left", padx=2)
        tk.Button(btn_row, text="Flat 402 (Floor 4)", bg="#F1F5F9", fg=TEXT_PRIMARY, font=("Helvetica", 8),
                  command=lambda: set_acc("flat_402", "SRM@F4-402", "RESIDENT"), relief="groove").pack(side="left", padx=2)
        tk.Button(btn_row, text="Authority Desk", bg="#FEF3C7", fg="#92400E", font=("Helvetica", 8, "bold"),
                  command=lambda: set_acc("authority", "6383898769", "AUTHORITY"), relief="groove").pack(side="left", padx=2)

    def handle_login(self):
        username = self.username_entry.get().strip()
        password = self.password_entry.get().strip()
        role = self.role_var.get()

        user = self.db.authenticate(username, password, role)
        if user:
            self.current_user = user
            self.show_main_dashboard()
        else:
            messagebox.showerror("Authentication Failed", "Invalid credentials entered.\nPlease verify from the directory or autofill options.")

    # =========================================================================
    # MAIN DASHBOARD
    # =========================================================================
    def show_main_dashboard(self):
        self.clear_screen()

        # Top App Bar
        top_bar = tk.Frame(self.root, bg=CARD_BG, highlightbackground=BORDER_COLOR, highlightthickness=1, height=60, padx=20, pady=10)
        top_bar.pack(fill="x")

        # Left: Branding
        left_info = tk.Frame(top_bar, bg=CARD_BG)
        left_info.pack(side="left")
        tk.Label(left_info, text="🌿 ECOAPARTMENT OS", font=("Helvetica", 14, "bold"), fg=PRIMARY_COLOR, bg=CARD_BG).pack(anchor="w")
        role_label = f"Logged in as: {self.current_user.resident_name} • {self.current_user.flat_number}" if self.current_user.role == "RESIDENT" else "Facility Authority & Security Desk (Admin)"
        tk.Label(left_info, text=role_label, font=("Helvetica", 9), fg=TEXT_SECONDARY, bg=CARD_BG).pack(anchor="w")

        # Right: Actions (PDF Export & Logout)
        right_actions = tk.Frame(top_bar, bg=CARD_BG)
        right_actions.pack(side="right")

        tk.Button(right_actions, text="📄 Export PDF Manual", bg="#F1F5F9", fg=TEXT_PRIMARY, font=("Helvetica", 9, "bold"),
                  command=self.export_pdf, relief="solid", bd=1, padx=10, pady=4).pack(side="left", padx=5)

        tk.Button(right_actions, text="Logout", bg="#FEF2F2", fg=DANGER_COLOR, font=("Helvetica", 9, "bold"),
                  command=self.show_login_screen, relief="solid", bd=1, padx=10, pady=4).pack(side="left", padx=5)

        # Tabbed Container
        notebook = ttk.Notebook(self.root)
        notebook.pack(fill="both", expand=True, padx=15, pady=10)

        # Tab 1: IoT Environmental Sensors
        tab_iot = ttk.Frame(notebook)
        notebook.add(tab_iot, text="📊 Live IoT Sensors")
        self.build_iot_tab(tab_iot)

        # Tab 2: LPG Gas Cylinder Units
        tab_gas = ttk.Frame(notebook)
        notebook.add(tab_gas, text="🔥 LPG Gas Supply")
        self.build_gas_tab(tab_gas)

        # Tab 3: Complaints Desk
        tab_tickets = ttk.Frame(notebook)
        notebook.add(tab_tickets, text="🛠️ Complaints Desk")
        self.build_tickets_tab(tab_tickets)

        # Tab 4: Visitor Gate Pass
        tab_visitors = ttk.Frame(notebook)
        notebook.add(tab_visitors, text="🛡️ Gate Access")
        self.build_visitors_tab(tab_visitors)

        # Tab 5: Password Directory
        tab_directory = ttk.Frame(notebook)
        notebook.add(tab_directory, text="🔑 Flat Directory")
        self.build_directory_tab(tab_directory)

    # -------------------------------------------------------------------------
    # TAB 1: IOT TELEMETRY
    # -------------------------------------------------------------------------
    def build_iot_tab(self, parent):
        container = tk.Frame(parent, bg=BG_COLOR, padx=15, pady=15)
        container.pack(fill="both", expand=True)

        tk.Label(container, text="Real-Time IoT Environmental Telemetry", font=("Helvetica", 13, "bold"), fg=TEXT_PRIMARY, bg=BG_COLOR).pack(anchor="w", pady=(0, 10))

        grid = tk.Frame(container, bg=BG_COLOR)
        grid.pack(fill="x")

        sensors = self.iot.refresh_telemetry()

        self.create_sensor_card(grid, "Air Quality (AQI)", f"{sensors.aqi} AQI", sensors.aqi_status, "#059669", 0, 0)
        self.create_sensor_card(grid, "Ambient Temperature", f"{sensors.temperature_celsius}°C", "Optimized Indoor Climate", "#2563EB", 0, 1)
        self.create_sensor_card(grid, "Relative Humidity", f"{sensors.humidity_percentage}%", sensors.humidity_status, "#0D9488", 0, 2)
        self.create_sensor_card(grid, "Water Tank Level", f"{sensors.water_tank_level_pct}%", f"{sensors.water_tank_current_litres}L / {sensors.water_tank_capacity_litres}L", "#0284C7", 1, 0)
        self.create_sensor_card(grid, "Gas Line Pressure", f"{sensors.gas_pressure_mbar} mbar", sensors.gas_status, "#D97706", 1, 1)
        self.create_sensor_card(grid, "Basement Flood Alarm", "0 Litres", "Normal • Sensor Active", "#16A34A", 1, 2)

        tk.Button(container, text="🔄 Refresh Sensor Telemetry", bg=PRIMARY_COLOR, fg="white", font=("Helvetica", 10, "bold"),
                  relief="flat", padx=15, pady=6, command=lambda: self.build_iot_tab(parent)).pack(pady=15, anchor="w")

    def create_sensor_card(self, parent, title, value, subtext, color, row, col):
        card = tk.Frame(parent, bg=CARD_BG, highlightbackground=BORDER_COLOR, highlightthickness=1, padx=15, pady=12)
        card.grid(row=row, column=col, padx=8, pady=8, sticky="nsew")
        parent.grid_columnconfigure(col, weight=1)

        tk.Label(card, text=title, font=("Helvetica", 9, "bold"), fg=TEXT_MUTED, bg=CARD_BG).pack(anchor="w")
        tk.Label(card, text=value, font=("Helvetica", 16, "bold"), fg=color, bg=CARD_BG).pack(anchor="w", pady=2)
        tk.Label(card, text=subtext, font=("Helvetica", 8), fg=TEXT_SECONDARY, bg=CARD_BG).pack(anchor="w")

    # -------------------------------------------------------------------------
    # TAB 2: GAS CYLINDER UNIT MANAGEMENT
    # -------------------------------------------------------------------------
    def build_gas_tab(self, parent):
        for w in parent.winfo_children():
            w.destroy()

        container = tk.Frame(parent, bg=BG_COLOR, padx=20, pady=15)
        container.pack(fill="both", expand=True)

        tk.Label(container, text="Multi-Unit LPG Cylinder Status & Supply Switcher", font=("Helvetica", 13, "bold"), fg=TEXT_PRIMARY, bg=BG_COLOR).pack(anchor="w")
        tk.Label(container, text="Automatic low-level threshold detection & seamless backup manifold switching.", font=("Helvetica", 9), fg=TEXT_SECONDARY, bg=BG_COLOR).pack(anchor="w", pady=(2, 15))

        for cyl in self.iot.gas_cylinders:
            card = tk.Frame(container, bg=CARD_BG, highlightbackground=BORDER_COLOR, highlightthickness=1, padx=15, pady=10)
            card.pack(fill="x", pady=6)

            left = tk.Frame(card, bg=CARD_BG)
            left.pack(side="left")
            tk.Label(left, text=cyl.label, font=("Helvetica", 11, "bold"), fg=TEXT_PRIMARY, bg=CARD_BG).pack(anchor="w")
            tk.Label(left, text=f"Remaining Capacity: {cyl.current_level_pct}% ({cyl.capacity_kg} kg)", font=("Helvetica", 9), fg=TEXT_SECONDARY, bg=CARD_BG).pack(anchor="w")

            right = tk.Frame(card, bg=CARD_BG)
            right.pack(side="right")

            status_bg = SUCCESS_COLOR if cyl.status == "ACTIVE" else (WARNING_COLOR if cyl.status == "STANDBY" else DANGER_COLOR)
            tk.Label(right, text=cyl.status, font=("Helvetica", 9, "bold"), fg="white", bg=status_bg, padx=8, pady=3).pack(side="left", padx=10)

            if cyl.status != "ACTIVE":
                def switch(uid=cyl.unit_id):
                    self.iot.switch_gas_unit(uid)
                    messagebox.showinfo("Gas Supply Switch", f"Switched active gas line to Unit {uid} successfully!")
                    self.build_gas_tab(parent)

                tk.Button(right, text="Switch to this Unit", font=("Helvetica", 8, "bold"), bg="#F1F5F9", command=switch).pack(side="left")

    # -------------------------------------------------------------------------
    # TAB 3: COMPLAINTS DESK
    # -------------------------------------------------------------------------
    def build_tickets_tab(self, parent):
        for w in parent.winfo_children():
            w.destroy()

        container = tk.Frame(parent, bg=BG_COLOR, padx=15, pady=15)
        container.pack(fill="both", expand=True)

        # Header with Raise Complaint button
        header_row = tk.Frame(container, bg=BG_COLOR)
        header_row.pack(fill="x", pady=(0, 10))
        tk.Label(header_row, text="Facility Complaints & Maintenance Tickets", font=("Helvetica", 13, "bold"), fg=TEXT_PRIMARY, bg=BG_COLOR).pack(side="left")

        tk.Button(header_row, text="+ Raise New Ticket", bg=PRIMARY_COLOR, fg="white", font=("Helvetica", 9, "bold"),
                  relief="flat", padx=10, pady=4, command=lambda: self.open_raise_ticket_dialog(parent)).pack(side="right")

        # Treeview for Tickets
        columns = ("ticket_id", "flat", "category", "priority", "status", "description", "notes")
        tree = ttk.Treeview(container, columns=columns, show="headings", height=10)
        tree.heading("ticket_id", text="Ticket ID")
        tree.heading("flat", text="Flat")
        tree.heading("category", text="Category")
        tree.heading("priority", text="Priority")
        tree.heading("status", text="Status")
        tree.heading("description", text="Description")
        tree.heading("notes", text="Authority Action Notes")

        tree.column("ticket_id", width=100)
        tree.column("flat", width=70)
        tree.column("category", width=90)
        tree.column("priority", width=80)
        tree.column("status", width=90)
        tree.column("description", width=220)
        tree.column("notes", width=200)
        tree.pack(fill="both", expand=True)

        # Populate
        complaints = self.db.get_complaints()
        for c in complaints:
            tree.insert("", "end", values=(c.ticket_id, c.flat_number, c.category, c.priority, c.status, c.description, c.authority_notes))

        # Authority Action: Update selected ticket
        if self.current_user.role == "AUTHORITY":
            action_frame = tk.Frame(container, bg=BG_COLOR)
            action_frame.pack(fill="x", pady=10)

            def resolve_selected():
                sel = tree.selection()
                if not sel:
                    messagebox.showwarning("Select Ticket", "Please click on a ticket row to update.")
                    return
                item = tree.item(sel[0])
                tid = item["values"][0]
                self.db.update_complaint_status(tid, "RESOLVED", "Technician resolved issue on-site.")
                messagebox.showinfo("Ticket Updated", f"Ticket {tid} marked as RESOLVED!")
                self.build_tickets_tab(parent)

            tk.Button(action_frame, text="✅ Mark Selected as RESOLVED", bg=SUCCESS_COLOR, fg="white", font=("Helvetica", 9, "bold"),
                      command=resolve_selected, relief="flat", padx=10, pady=4).pack(side="left", padx=5)

    def open_raise_ticket_dialog(self, parent_tab):
        win = tk.Toplevel(self.root)
        win.title("Raise Maintenance Ticket")
        win.geometry("450x380")
        win.configure(bg=CARD_BG)

        tk.Label(win, text="Submit Maintenance Ticket", font=("Helvetica", 12, "bold"), bg=CARD_BG, fg=TEXT_PRIMARY).pack(pady=10)

        form = tk.Frame(win, bg=CARD_BG, padx=20)
        form.pack(fill="both", expand=True)

        tk.Label(form, text="Category:", bg=CARD_BG, font=("Helvetica", 9, "bold")).pack(anchor="w")
        cat_var = tk.StringVar(value="PLUMBING")
        ttk.Combobox(form, textvariable=cat_var, values=["PLUMBING", "ELECTRICAL", "GAS_SUPPLY", "ELEVATOR", "SECURITY"]).pack(fill="x", pady=(2, 10))

        tk.Label(form, text="Priority:", bg=CARD_BG, font=("Helvetica", 9, "bold")).pack(anchor="w")
        prio_var = tk.StringVar(value="STANDARD")
        ttk.Combobox(form, textvariable=prio_var, values=["STANDARD", "URGENT", "CRITICAL"]).pack(fill="x", pady=(2, 10))

        tk.Label(form, text="Description of Issue:", bg=CARD_BG, font=("Helvetica", 9, "bold")).pack(anchor="w")
        desc_entry = tk.Text(form, height=4, font=("Helvetica", 9), relief="solid", bd=1)
        desc_entry.pack(fill="x", pady=(2, 15))

        def submit():
            desc = desc_entry.get("1.0", tk.END).strip()
            if not desc:
                messagebox.showwarning("Incomplete", "Please describe the problem.")
                return
            
            flat = self.current_user.flat_number if self.current_user.role == "RESIDENT" else "Admin Office"
            floor = self.current_user.floor_number
            tid = f"TKT-{random.randint(100, 999)}-{cat_var.get()[:3]}"
            
            new_c = Complaint(
                ticket_id=tid, flat_number=flat, floor_number=floor,
                category=cat_var.get(), priority=prio_var.get(), description=desc,
                status="ASSIGNED", authority_notes="Logged in desk. Technician assigned.",
                timestamp=datetime.datetime.now().strftime("%Y-%m-%d %H:%M"),
                raised_by_name=self.current_user.resident_name
            )
            self.db.add_complaint(new_c)
            win.destroy()
            messagebox.showinfo("Ticket Submitted", f"Ticket {tid} logged successfully!")
            self.build_tickets_tab(parent_tab)

        tk.Button(win, text="Submit Ticket →", bg=PRIMARY_COLOR, fg="white", font=("Helvetica", 10, "bold"),
                  relief="flat", pady=6, command=submit).pack(fill="x", padx=20, pady=10)

    # -------------------------------------------------------------------------
    # TAB 4: VISITORS / GATE ACCESS
    # -------------------------------------------------------------------------
    def build_visitors_tab(self, parent):
        container = tk.Frame(parent, bg=BG_COLOR, padx=15, pady=15)
        container.pack(fill="both", expand=True)

        tk.Label(container, text="Security Desk Visitor & Delivery Verification", font=("Helvetica", 13, "bold"), fg=TEXT_PRIMARY, bg=BG_COLOR).pack(anchor="w", pady=(0, 10))

        # Sample logs
        visitors = [
            ("VIS-801", "K. Arunkumar (Swiggy)", "DELIVERY", "Flat 402", "APPROVED", "Sep 20, 18:45"),
            ("VIS-802", "Dr. V. Raghavan", "GUEST", "Flat 101", "APPROVED", "Sep 20, 17:30"),
            ("VIS-803", "Urban Company Plumber", "SERVICE", "Flat 204", "APPROVED", "Sep 20, 14:15"),
        ]

        columns = ("id", "name", "type", "flat", "status", "time")
        tree = ttk.Treeview(container, columns=columns, show="headings", height=8)
        tree.heading("id", text="Pass ID")
        tree.heading("name", text="Visitor / Service Agent")
        tree.heading("type", text="Type")
        tree.heading("flat", text="Target Flat")
        tree.heading("status", text="Pass Status")
        tree.heading("time", text="Entry Time")
        tree.pack(fill="both", expand=True)

        for v in visitors:
            tree.insert("", "end", values=v)

    # -------------------------------------------------------------------------
    # TAB 5: PASSWORD DIRECTORY
    # -------------------------------------------------------------------------
    def build_directory_tab(self, parent):
        container = tk.Frame(parent, bg=BG_COLOR, padx=15, pady=15)
        container.pack(fill="both", expand=True)

        tk.Label(container, text="Apartment Resident & Access PIN Directory", font=("Helvetica", 13, "bold"), fg=TEXT_PRIMARY, bg=BG_COLOR).pack(anchor="w", pady=(0, 10))

        columns = ("role", "flat", "name", "username", "password", "contact")
        tree = ttk.Treeview(container, columns=columns, show="headings", height=12)
        tree.heading("role", text="Role")
        tree.heading("flat", text="Flat / Office")
        tree.heading("name", text="Resident Name")
        tree.heading("username", text="Username")
        tree.heading("password", text="Default PIN")
        tree.heading("contact", text="Contact Phone")

        tree.column("role", width=90)
        tree.column("flat", width=90)
        tree.column("name", width=160)
        tree.column("username", width=100)
        tree.column("password", width=110)
        tree.column("contact", width=130)
        tree.pack(fill="both", expand=True)

        users = self.db.get_all_users()
        for u in users:
            tree.insert("", "end", values=(u.role, u.flat_number, u.resident_name, u.username, u.password, u.contact_number))

    # -------------------------------------------------------------------------
    # PDF EXPORTER ACTION
    # -------------------------------------------------------------------------
    def export_pdf(self):
        try:
            os.system("python3 generate_pdf.py")
            messagebox.showinfo("PDF Generated", "Official EcoApartment Manual & Credentials PDF generated successfully!\nFile: EcoApartment_Credentials_and_Manual.pdf")
        except Exception as e:
            messagebox.showerror("Error", f"Could not generate PDF: {e}")


def main():
    root = tk.Tk()
    app = EcoApartmentGUI(root)
    root.mainloop()


if __name__ == "__main__":
    main()
