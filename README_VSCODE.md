# EcoApartment OS — Smart Facility Management & IoT Sustainability Platform

**SRM Green Residency • Visual Studio / VS Code Production Manual**  
*Complete Web Application, Supabase Database Integration, 400 Tenant Profiles, and IoT Telemetry*

---

## 🌟 Executive Summary

**EcoApartment OS** is a full-stack facility management and smart residential operating platform designed for **SRM Green Residency**. The system manages **400 flats** across **Blocks A, B, C, and D**, featuring:
1. **Full-Stack Web Application**: Responsive desktop sidebar + mobile drawer UI across 18 operational views.
2. **Supabase PostgreSQL & SQLite Hybrid Database**: Seamless cloud-to-local database architecture with automated fallback for offline Windows 11 execution.
3. **400 Resident Directory**: Complete CRUD, real-time search, block/floor filtering, and CSV export.
4. **IoT Sustainability Telemetry**: Live 10,000L Overhead Water Reservoir, Ambient Air Quality (AQI), Temperature, Relative Humidity, and Sump Drainage.
5. **Multi-Unit LPG Manifold**: Active/Standby cylinder switching with automatic booking trigger.
6. **Water Resource Anomaly Detection Engine**: Object-Oriented Python algorithm (`water_resource_tracker.py`) leveraging encapsulation and inheritance to catch flow leaks exceeding 45 L/min.
7. **Reports & Real Exports**: Official PDF operations manual, 400 Residents Master CSV, Maintenance Dues CSV, and Supabase SQL migration script.

---

## 📁 Repository Structure for Visual Studio / VS Code

```text
├── main.py                     # Universal Launcher (F5 in VS Code)
├── server.py                   # Full-Stack Web Application Server (18 Pages & REST APIs)
├── eco_apartment_python_app.py # SQLite Database Manager, Seeding Engine & Data Models
├── supabase_schema.sql         # Supabase PostgreSQL DDL, RLS Policies, Views & 400 Seed Rows
├── supabase_client.py          # Supabase Cloud Client with Zero-Config Local Fallback
├── water_resource_tracker.py   # Python OOP Anomaly Engine (Encapsulation, Inheritance)
├── export_data.py              # Real CSV, JSON, and PDF Exporter
├── test_system.py              # Automated 9-Suite System Verification Test
├── generate_pdf.py             # Self-Contained PDF Operations Manual Generator
├── requirements.txt            # Python Dependencies (Pure Python standard library)
├── .vscode/
│   └── launch.json             # F5 Debug configuration for VS Code on Windows 11
├── eco_apartment.db            # Auto-generated SQLite Database (400 Flats Pre-seeded)
├── EcoApartment_Credentials_and_Manual.pdf  # Generated Operations & Credentials Manual
└── APK_DOWNLOAD/               # Android APK & PDF exports
```

---

## 🚀 Running on Windows 11 in Visual Studio / VS Code

### Step 1: Open the Project
1. Launch **Visual Studio Code** or **Visual Studio**.
2. Click **File -> Open Folder...** and select this directory.

### Step 2: Install Dependencies
Open the built-in terminal (`Ctrl + ` `):
```bash
pip install -r requirements.txt
```
*(Note: Standard Python 3 libraries `sqlite3`, `http.server`, `urllib`, `dataclasses`, and `json` are built-in with zero mandatory external pip dependencies!)*

### Step 3: Run the Application
Press **`F5`** or run in the terminal:
```bash
python main.py
```
Then open your browser to:
👉 **`http://localhost:8088`**

### Step 4: Run Automated Tests
```bash
python test_system.py
```

---

## 🔑 Default Credentials & Preserved Accounts

### 1. Facility Authority Desk (Admin)
* **Username**: `admin` or `authority`
* **Password**: `6383898769`
* **Access**: Full administrative control across all 400 flats, technician assignment, billing reconciliation, and Supabase settings.

### 2. Preserved Resident Accounts
* **Flat B-402 (Mohan Palaniappan)**: Username `B-402` or `flat_402` | Password: `SRM@F4-402`
* **Flat B-101 (R. SHAM SUDHAN)**: Username `B-101` or `flat_101` | Password: `7708994065`
* **Flat B-102 (P. MOHAN)**: Username `B-102` or `flat_102` | Password: `6383898769`
* **Flat B-204 (T S. ABINAYA)**: Username `B-204` or `flat_204` | Password: `8825742523`
* **Flat B-105 (M. JAI MUGESH)**: Username `B-105` or `flat_105` | Password: `9361077508`
* **Flat B-108 (N. MADHUVARSHAN)**: Username `B-108` or `flat_108` | Password: `8838449468`
* **Flat B-201 (K. MANI MARAN)**: Username `B-201` or `flat_201` | Password: `7845968905`

---

## 🏢 Apartment Complex Architecture (400 Flats)

The SRM Green Residency residential complex is structured into **4 Blocks** with **100 flats each**:
- **Block A**: Floors 1 to 4 • Units 101–125, 201–225, 301–325, 401–425 (100 Flats)
- **Block B**: Floors 1 to 4 • Units 101–125, 201–225, 301–325, 401–425 (100 Flats)
- **Block C**: Floors 1 to 4 • Units 101–125, 201–225, 301–325, 401–425 (100 Flats)
- **Block D**: Floors 1 to 4 • Units 101–125, 201–225, 301–325, 401–425 (100 Flats)

**Occupancy Statistics**:
- Total Flats: **400**
- Occupied Units: **382 (95.5%)**
- Vacant / Refurbishment Units: **18 (4.5%)**

---

## 🌐 Complete 18 Operational Pages

| # | Page / View | Key Features & Actions |
|---|---|---|
| 1 | **Login & Session** | Role-based login (Admin/Resident), 1-click test credentials, session persistence in localStorage. |
| 2 | **Main Dashboard** | Executive summary cards, block occupancy progress bars, live IoT gauges, quick shortcuts. |
| 3 | **Resident Directory** | 400 tenants, real-time search, block/floor/status filters, pagination (25/page), CSV export. |
| 4 | **Resident Profile** | Flat code, occupant name, contact, email, emergency phone, vehicle tag, water/gas meter IDs. |
| 5 | **Add/Edit Resident** | Full modal form for registering new residents or updating contact details. |
| 6 | **Block & Flat Matrix** | Visual building matrix across Blocks A, B, C, D with color-coded occupancy tabs. |
| 7 | **Complaints & Helpdesk** | Service tickets (Plumbing, Electrical, Gas, Elevator), priority badges, technician dispatch, resolve. |
| 8 | **Technicians Roster** | Certified specialists (Plumber, Electrician, Gas Specialist, Otis Engineer, Security Officer) with ratings. |
| 9 | **Maintenance & Dues** | Billing register for all flats, "Mark Paid" modal with UPI receipt ID generation, dues export. |
| 10 | **Community Notices** | Pinned board with categories (Water, Green Energy, Safety, AGM), "+ Post Notice" modal. |
| 11 | **Visitor Pass Desk** | Gate pass log, 4-digit security code generation, vehicle entry tracking, check-out action. |
| 12 | **Water Reservoir** | 10,000L Overhead Tank animated level, daily consumption chart, zone flow telemetry. |
| 13 | **Environmental AQI** | Live Air Quality Index (AQI 42 Good), temperature (28.4°C), relative humidity, ambient noise. |
| 14 | **Flood & Storm Sump** | Basement sump level sensor (12%), automated submersible pump test drill. |
| 15 | **LPG Multi-Manifold** | Units 1, 2, 3 multi-cylinder manifold with interactive line switching and refill booking. |
| 16 | **Resource Anomaly History** | Demonstrates the OOP algorithm from `water_resource_tracker.py`, leak simulation button. |
| 17 | **Reports & Exports** | Direct downloads for PDF Operations Manual, 400 Residents CSV, Dues CSV, and Supabase SQL. |
| 18 | **Administrative Settings** | Supabase Project URL & Anon Key config, Cloud Sync status toggle, database reset. |

---

## ☁️ Supabase PostgreSQL Setup Guide

To link your live **Supabase Cloud Project**:

1. Go to [https://supabase.com](https://supabase.com) and create a new project.
2. Open the **SQL Editor** in the Supabase Dashboard.
3. Open `supabase_schema.sql` from this project folder, copy its contents, and click **Run**.
   - This creates all 12 tables, indexes, views, RLS security policies, and inserts all 400 flat records.
4. Copy your **Project URL** and **Anon Public Key** from **Project Settings -> API**.
5. Open the EcoApartment OS web dashboard (`http://localhost:8088`), navigate to **Supabase Cloud & DB**, paste your credentials, and click **Save & Test Supabase**.
6. The app will immediately activate cloud replication while maintaining local SQLite redundancy!

---

## 📑 OOP Architecture & Viva Concepts (`water_resource_tracker.py`)

The platform implements Object-Oriented Programming (OOP) paradigms for academic demonstration:
- **Encapsulation**: Private fields `__current_flow_rate` and `__leak_threshold` in `WaterMeter` accessed via validated `@property` getters and setters.
- **Inheritance**: `WaterMeter` inherits base sensor identification from `ResourceSensor` and alert capabilities from `Alertable`.
- **Polymorphism**: `detect_anomaly()` overrides the base method to evaluate flow rate spikes outside scheduled consumption windows.
