# EcoApartment OS — Smart Resident & Sustainability Portal
**SRM Green Residency • IoT Smart Community, Technical Dispatch & Facility Mesh**

![EcoApartment Banner](https://img.shields.io/badge/EcoApartment-OS%20v2.0-0F766E?style=for-the-badge&logo=leaf)
![Build Status](https://img.shields.io/badge/Build-Passing-10B981?style=for-the-badge)
![Platform](https://img.shields.io/badge/Platform-Web%20%7C%20PWA%20%7C%20Android%20APK-0284C7?style=for-the-badge)

---

## 📌 Executive Summary
**EcoApartment OS** is a modern, production-grade Web & Android IoT management ecosystem engineered for **SRM Green Residency**. The system connects 400 residential apartments across Blocks A, B, C, and D with real-time IoT water and energy monitoring, digital gate passes, technical helpdesk dispatch, maintenance billing, community notices, and a verified 1,480-person resident census directory.

---

## 👤 Primary Resident Profile
The primary default profile across the application is configured for **Mohan Palaniappan** as the main resident user:

| Parameter | Details |
| :--- | :--- |
| **Resident Name** | **Mohan Palaniappan** |
| **Flat Number** | **Flat B-402** (Block B, Floor 4) |
| **Primary Contact Number** | **`+91 63838 98769`** |
| **Login Username** | **`6383898769`** or **`flat_402`** |
| **Login Password** | **`6383898769`** or **`mohan123`** |
| **Email Address** | **`mohanpalaniappan007@gmail.com`** |
| **Designated Parking** | Slot P-B402 (Basement 1) |
| **Registered Vehicle** | TN-09-CB-1234 |
| **Emergency Contact** | +91 94441 55210 |
| **Dues Settlement** | PAID (Clean Record) |

---

## 🔐 Credentials & Authentication Directory

### 1. Main Resident Logins
| Flat / Role | Username | Password | Resident Name | Primary Phone | Email Address |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Flat B-402 (Main)** | `6383898769` / `flat_402` | `6383898769` / `mohan123` | Mohan Palaniappan | +91 63838 98769 | `mohanpalaniappan007@gmail.com` |
| **Flat A-101** | `flat_101` / `7708994065` | `7708994065` / `sham123` | R. SHAM SUDHAN | +91 77089 94065 | `shamsudhan@gmail.com` |
| **Flat A-103** | `flat_103` / `9840155432` | `9840155432` / `madhu123` | MADHU VARSHAN | +91 98401 55432 | `madhuvarshan@srmgreen.in` |
| **Flat A-105** | `flat_105` / `9361077508` | `9361077508` / `jai123` | M. JAI MUGESH | +91 93610 77508 | `jaimugesh@srmgreen.in` |
| **Flat A-108** | `flat_108` / `8838449468` | `8838449468` | N. MADHUVARSHAN | +91 88384 49468 | `madhuvarshan108@srmgreen.in` |

### 2. Facility Authority Desk
| Property | Authority Credentials |
| :--- | :--- |
| **Role** | SRM Facility Authority & Security Admin (`AUTHORITY`) |
| **Username** | `admin` or `authority` |
| **Password** | `6383898769` |
| **Admin Email** | `admin@srmgreen.in` |
| **Office Contact** | +91 63838 98769 |

---

## 🚀 Key Application Features

### 🏢 1. Clean White-Screen Login Portal
- **Focused UI**: Minimalist centered authentication card with subtle apartment watermark logo.
- **Quick Profile Switcher Toggle**: Pop-up selector allowing 1-click preset logins for Mohan Palaniappan (`6383898769`), R. Sham Sudhan, M. Jai Mugesh, Madhu Varshan, or Facility Admin.
- **Show/Hide Password**: Interactive eye button for instant password visibility.

### 🛠️ 2. Technical Dispatch & Helpdesk
- **Ticket Logging**: Residents can log complaints under Plumbing, Electrical, Elevator, Carpentry, and Water lines with High/Critical/Urgent priority tags.
- **Technician Assignment**: Real-time technician assignment (e.g., R. Kathir - Plumbing Specialist, K. Selvam - Wireman, S. Arumugam - Lift Engineer).
- **Status Progression**: Live workflow tracking from `OPEN` ➔ `ASSIGNED` ➔ `IN_PROGRESS` ➔ `RESOLVED`.

### 🛡️ 3. Visitor Pass & Gate Control
- **Digital Gate Pass**: Generate instant visitor codes (`GP-98401`, `GP-77089`) for Swiggy/Zomato deliveries, guests, and cab arrivals.
- **Approval Log**: Real-time check-in notifications and vehicle registry (`TN-09-CB-4892`).

### 📢 4. Community Notices & Announcements
- **Pinned RWA Bulletins**: Annual General Body meetings, RWA eco audits, and overhead water tank sanitization schedules.
- **Category Filter**: Filter announcements by Community, Maintenance, Rules, and Urgency.

### 💳 5. Maintenance Billing & Dues
- **Itemized Ledger**: Breakdowns for Water Charges (₹450), Piped LPG Gas (₹350), Common Area Electricity (₹600), and Residential Maintenance (₹1,050).
- **Instant Settlement**: Interactive payment gateway popup with digital payment receipt export.

### 🌱 6. IoT & Sustainability Hub
- **Dual-Stage Pump Controller**: Inflow (450 L/min) and distribution outflow (380 L/min) telemetry with auto/manual motor toggle.
- **Environmental Telemetry**: Air Quality Index (42 AQI Good, PM2.5 / PM10 levels), 18.4 kW rooftop solar array output, ambient temperature and humidity.
- **Smart Piped LPG Manifold**: Dual 14.2 kg cylinder status tracking with automated switchover and booking.
- **Water Anomaly Engine**: Automatic leak detection algorithm alerting residents of continuous unmonitored inflow.

### 👥 7. 400 Flats Resident Census & Family Directory
- **Complete Population Record**: 400 flats cataloged across Blocks A, B, C, D totaling 1,480 residents (480 Gents, 460 Ladies, 540 Children).
- **Family Inspector**: Detailed family member rosters with blood group, age, occupation/school, and emergency contacts.

### 📊 8. Data Export & PDF Credentials Directory
- **CSV & JSON Export**: One-click download of full resident rosters (`residents_400_export.csv`) and IoT telemetry datasets (`iot_telemetry_export.json`).
- **Official PDF Manual**: Built-in server generator outputting a formatted multi-page PDF manual and credentials sheet (`EcoApartment_Credentials_Directory.pdf`).

---

## 📄 Documentation & PDF Files
- **PDF Credentials & Operations Guide**: `public/EcoApartment_Credentials_Directory.pdf`
- **Markdown Credentials Sheet**: `README_CREDENTIALS.md`
- **Android Debug APK**: `APK_DOWNLOAD/app-debug.apk` (22 MB)

---

## 🛠️ Tech Stack & Server Architecture
- **Frontend**: HTML5, Modern CSS3 (CSS Variables, Flexbox/Grid), Vanilla JavaScript (ES6+), FontAwesome icons, Plus Jakarta Sans typography.
- **Backend**: Native Node.js HTTP Server (`server.js`) with RESTful API endpoints.
- **Database**: In-memory JSON stores + Room SQLite native database for Android offline sync.
- **Port**: `3000` (Dev Server)

---

## 🏃 Getting Started

### Run Server Locally
```bash
npm install
npm start
# Server runs at http://localhost:3000
```

### PDF Generation
To regenerate the official PDF credentials document:
```bash
python3 generate_pdf.py
```

---
*EcoApartment OS v2.0 • SRM Green Residency Maintenance & Facility Mesh*
