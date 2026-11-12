<!--
  EcoApartment OS · Project README
  Keep feature claims aligned with the implementation.
-->

<div align="center">

# 🌿 EcoApartment OS

### One connected dashboard for smarter apartment living.

**Resident services · IoT monitoring · CCTV · Facility operations · AI-assisted workflows**

<p>
  <a href="https://ecoapartment-e4uzoqtj2-mohan05713.vercel.app/"><img alt="Open preview" src="https://img.shields.io/badge/🚀_LIVE_PREVIEW-Open_Dashboard-0F766E?style=for-the-badge"></a>
</p>

![Web](https://img.shields.io/badge/Platform-Web%20Application-2563EB?style=flat-square)
![React](https://img.shields.io/badge/React-18-61DAFB?style=flat-square&logo=react&logoColor=111827)
![TypeScript](https://img.shields.io/badge/TypeScript-5-3178C6?style=flat-square&logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-6-646CFF?style=flat-square&logo=vite&logoColor=white)
![Python](https://img.shields.io/badge/Python-Supporting%20Backend-3776AB?style=flat-square&logo=python&logoColor=white)

*A unified apartment-management experience that brings the original EcoApartment dashboard and Smart Operations workspace together.*

</div>

---

## ✨ At a glance

EcoApartment OS is a web-based smart apartment and facility-management platform. It combines the established green dashboard, resident and facility workflows, IoT monitoring views and CCTV page with a new Smart Operations workspace — all accessible from one website and one left-side navigation.

The integration is designed to **extend the existing experience, not replace it**. Existing dashboard pages and their original browser data keys are retained; the new workspace is loaded as an embedded page when a Smart Operations menu item is selected.

> **Release status:** The Smart Operations integration has been merged into the production branch through Pull Request #12. Complete the remaining desktop and mobile interactive testing before treating every workflow as production-verified.

## 🚀 Open the project

| Resource | Link |
|---|---|
| **EcoApartment website** | [Open website](https://ecoapartment-e4uzoqtj2-mohan05713.vercel.app/) |
| **Backend API (Render)** | [Open backend service](https://ecoapartment-os-api.onrender.com/) |

The website link opens the integrated dashboard preview. The backend link opens the Render-hosted API service; it is not a separate user-facing website.

## 🧭 What's inside

### 🏡 Original EcoApartment dashboard

The existing dashboard remains the main entry point, including its established apartment-management and IoT experience.

- Resident and community-management screens.
- Complaints, notices, dues and visitor workflows.
- IoT and infrastructure monitoring pages.
- Existing CCTV page and navigation.
- Dashboard AI Assistant panel.
- Existing responsive navigation and page switching.

### ⚙️ Smart Operations workspace

The new modules are presented in the same website through the original sidebar.

| Module | What it provides | Implementation note |
|---|---|---|
| 🏢 **3D Building Twin** | Interactive visual building representation | Illustrative 3D-style view; not a BIM or live digital twin feed |
| 🤖 **AI Maintenance Insights** | Maintenance-risk demonstration using sample telemetry and a locally trained logistic-regression model | Demonstration model trained on synthetic data; not a safety-critical prediction |
| 📅 **Facility Booking** | Create and track facility booking requests | Browser-local storage in this version |
| 💼 **Finance & Assets** | Asset register and expense-entry workspace | Browser-local storage in this version |
| 🎙️ **Voice AI Assistant** | Voice input and spoken responses for supported browsers | Uses browser speech capabilities; microphone permission may be required |
| 🚨 **Emergency & Safety** | Record and track incident reports | Does not contact emergency services or dispatch responders |

The dashboard AI Assistant also includes a microphone shortcut to open the Voice AI Assistant module.

## 🧩 How the integration works

- The original root `index.html` remains the main dashboard and keeps its existing pages.
- Smart Operations is served by the React application at `smart-operations.html`.
- The original dashboard opens each module inside an embedded workspace and passes the selected module in the URL.
- The embedded React view hides its own duplicate navigation so the user stays within the original dashboard experience.
- The desktop and mobile sidebar toggle remains available.

```text
                         ECOAPARTMENT OS
                                |
                  Original green dashboard
                       (index.html)
                                |
                    One shared left sidebar
                                |
          +---------------------+----------------------+
          |                     |                      |
     Existing pages       Smart Operations       Emergency & Safety
          |                     |                 (last menu item)
   Residents / IoT /      Embedded React app
   CCTV / facility        smart-operations.html
          |                     |
          |        +------------+-------------+
          |        |            |             |
          |      3D Twin   Maintenance     Booking
          |        |            |             |
          |      Assets     Voice AI      Incidents
          |
    Existing dashboard data remains separate
    from new module browser-local data
```

## 🛠️ Technology

| Area | Technology |
|---|---|
| Main dashboard | HTML, CSS and JavaScript |
| Smart Operations UI | React 18, TypeScript |
| Frontend build | Vite 6 |
| Styling | Tailwind CSS 4 and project CSS |
| Icons | Lucide React |
| Browser voice | Web Speech API where supported |
| Existing backend/supporting services | Project Python/Node service files |
| Data integration | Supabase client is included; availability depends on project configuration |
| Version control | Git and GitHub |
| Preview hosting | Vercel |

## 💾 Data handling and limitations

**Preserving existing records is a core integration requirement.** The Smart Operations integration does not intentionally reset or migrate the original dashboard's browser data.

The original dashboard uses its existing storage keys, including:

- `eco_os_residents_v3`
- `eco_os_complaints_v3`
- `eco_os_dues_v3`
- `eco_os_notices_v3`
- `eco_os_visitors_v3`
- `eco_os_iot_v3`
- `eco_os_gas_v3`
- `eco_os_anomalies_v3`

The new modules currently use separate browser-local keys:

- `eco_os_v2_bookings`
- `eco_os_v2_incidents`
- `eco_os_v2_assets`
- `eco_os_v2_expenses`

**Important:** Browser-local records are tied to the browser/device and are not automatically shared across users or devices. The new module records are not described as Supabase-synchronized. Do not use this demo as the sole record system for real emergency response, payments, security or critical facility operations.

### Demo and safety boundaries

- Some IoT readings, seeded profiles and anomaly examples may be sample or simulated data.
- The maintenance model is trained at runtime on synthetic examples and should be treated as a demonstration only.
- The 3D building view is illustrative, not a verified live model of the physical property.
- Voice features depend on browser support, permissions and device audio settings.
- Emergency incident entry is an in-app workflow only. For a real emergency, contact the appropriate local emergency services directly.
- Do not publish real resident personal data, passwords, access codes or private operational details.

## 💻 Run locally in VS Code

### Requirements

- Node.js (LTS recommended)
- npm
- Visual Studio Code
- Git (optional, for cloning)

### 1. Clone the repository

```bash
git clone https://github.com/mohan05713/Eco-Apartment.git
cd Eco-Apartment
```

### 2. Switch to the integration branch

```bash
git checkout feat/integrated-smart-operations-dashboard
```

### 3. Install dependencies

```bash
npm install
```

### 4. Start the Vite development server

```bash
npm run dev
```

Open the local URL printed in the terminal. Vite commonly uses `http://localhost:5173`.

### 5. Build and preview

```bash
npm run build
npm run preview
```

The production build is generated in `dist/`.

### Available scripts

| Command | Purpose |
|---|---|
| `npm install` | Install dependencies |
| `npm run dev` | Start Vite development server |
| `npm run build` | Build the web application |
| `npm run preview` | Preview the production build |
| `npm start` | Run the package's configured Node entry command |

Check the relevant server file and deployment settings before starting or deploying the backend; local and hosted start commands may differ.

## 🔐 Configuration and security

- Review `.env.example` and the actual source code to identify required environment variables.
- Never commit `.env` files, private API keys, service-role keys, passwords or real resident credentials.
- If Supabase is configured, enable and test Row Level Security (RLS) and least-privilege access before storing real data.
- Do not expose service-role credentials in browser code.
- Validate and authorize sensitive operations on the server, not only by hiding UI controls.
- Use synthetic data for screenshots, demos and public documentation.

## ✅ Integration verification checklist

- [x] Preserve the original dashboard as the homepage.
- [x] Keep existing CCTV and IoT pages in the original dashboard.
- [x] Add Smart Operations modules to the existing sidebar.
- [x] Keep Emergency & Safety at the bottom of the sidebar.
- [x] Add a Voice AI shortcut to the dashboard AI Assistant.
- [x] Support the sidebar toggle on desktop and mobile.
- [x] Keep original dashboard storage keys separate from new module keys.
- [x] Create a branch preview deployment.
- [ ] Complete final interactive testing across desktop and mobile browsers.
- [x] Review and approve [Pull Request #12](https://github.com/mohan05713/Eco-Apartment/pull/12) — merged on November 12, 2026.
- [x] Merge the integration into the production branch.

## 🗺️ Future roadmap

Planned improvements — these are future goals, not claims that the features are already complete.

| Priority | Planned enhancement |
|---|---|
| **Data platform** | Secure shared persistence for bookings, incidents, assets and expenses, with migrations, validation and tested RLS |
| **Live IoT** | Connect verified physical sensors and clearly distinguish live telemetry from demo readings |
| **Emergency response** | Escalation policies, responder roles, acknowledgements, audit history and verified notification integrations |
| **CCTV & security** | Authorized live camera integrations, access controls and privacy-conscious retention |
| **AI maintenance** | Train and evaluate against validated historical maintenance records; show confidence and explainability |
| **Voice assistant** | Improve multilingual support, accessibility and robust speech fallback |
| **Resident experience** | Mobile-first resident portal, notifications and multilingual interface |
| **Facility workflows** | Booking approvals, conflict checks, recurring schedules and role-based permissions |
| **Finance & assets** | Approval workflows, receipts, vendor records, service reminders and audit trails |
| **Reliability** | Automated tests, CI checks, error monitoring, backups and recovery drills |

## 🧪 Before production use

1. Test every existing dashboard page and workflow.
2. Test each embedded Smart Operations module on desktop and mobile.
3. Verify that the original resident, complaint, dues, notice, visitor and IoT records remain intact.
4. Confirm which data is local, simulated or truly connected to a backend.
5. Configure authentication, authorization, backups and audit logs before using real resident or financial data.
6. Review the pull request and deployment before merging.

## 🤝 Contributing

1. Create a feature branch.
2. Make a focused change and preserve existing data flows.
3. Run `npm run build` and test the affected workflows.
4. Document configuration changes and known limitations.
5. Open a pull request with screenshots or a concise test summary.

---

<div align="center">

### 🌱 Smarter spaces. Safer communities. Sustainable living.

**EcoApartment OS** · Built as a connected web experience.

[Open EcoApartment website](https://ecoapartment-e4uzoqtj2-mohan05713.vercel.app/)

</div>
