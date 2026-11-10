# EcoApartment OS
### Smart Resident, Facility Management & Sustainability Web Platform

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Web%20Application-2563EB?style=for-the-badge" alt="Web application" />
  <img src="https://img.shields.io/badge/Frontend-React%20%7C%20TypeScript-3178C6?style=for-the-badge" alt="React and TypeScript" />
  <img src="https://img.shields.io/badge/Backend-Node.js-339933?style=for-the-badge" alt="Node.js" />
  <img src="https://img.shields.io/badge/Database-Supabase-3ECF8E?style=for-the-badge" alt="Supabase" />
  <img src="https://img.shields.io/badge/Deployment-Vercel%20%7C%20Render-111827?style=for-the-badge" alt="Vercel and Render" />
  <img src="https://img.shields.io/badge/Android-Not%20included-lightgrey?style=for-the-badge" alt="Web only" />
</p>

**EcoApartment OS** is a web-based apartment and community operations platform designed to bring resident services, facility workflows, visitor management, maintenance operations and sustainability information into one browser-accessible system.

> **Platform scope:** This repository is maintained as a **web application**. Android/Kotlin application development is outside the current project scope.

---

## Contents
- [Project at a glance](#project-at-a-glance)
- [Features](#features)
- [Technology stack](#technology-stack)
- [Architecture](#architecture)
- [Repository structure](#repository-structure)
- [Run locally](#run-locally)
- [Environment configuration](#environment-configuration)
- [Supabase setup](#supabase-setup)
- [Deployment](#deployment)
- [Live links and health checks](#live-links-and-health-checks)
- [Testing and quality checks](#testing-and-quality-checks)
- [Security and data handling](#security-and-data-handling)
- [Troubleshooting](#troubleshooting)
- [Roadmap](#roadmap)
- [Contributing](#contributing)

## Project at a glance

| Area | Description |
|---|---|
| Product | EcoApartment OS |
| Application type | Responsive web application |
| Frontend | React, TypeScript/TSX, HTML5, CSS3 |
| Web tooling | Vite |
| Server/API | Node.js |
| Data services | Supabase/PostgreSQL; local/sample data utilities are also present |
| Supporting utilities | Python scripts and SQL schema |
| Local development | Node.js and npm |
| Deployment targets | Vercel (web frontend) and Render (API service) |

EcoApartment OS brings common apartment operations into a single digital workspace. The repository contains both the React/Vite web frontend and a Node.js HTTP server, along with supporting Python utilities, Supabase integration files and database schema.

## Features

### Resident experience
- Resident login and profile views.
- Resident directory and family information views.
- Community notices and announcements.
- Visitor pass and gate-entry workflows.
- Maintenance dues and payment-status views.
- Complaint submission and status tracking.

### Facility and administration
- Facility dashboard and resident administration.
- Complaint assignment and technician workflow.
- Visitor and parking management.
- Maintenance billing and reports.
- CSV/JSON export utilities where implemented.
- Administrative views for community operations.

### Sustainability and monitoring
- Water and environmental monitoring views.
- IoT telemetry and anomaly-related screens.
- LPG and energy-related information screens.
- Sustainability data and reporting views.

**Implementation note:** Some screens use demonstration or seeded data. A screen or UI control should not be interpreted as a live hardware integration or real payment gateway unless the corresponding external integration is configured and tested.

## Technology stack

| Technology / language | Role in this repository |
|---|---|
| **TypeScript / TSX** | React web application, components, pages, types and frontend utilities |
| **JavaScript (ES6+)** | Node.js server, browser scripts and utility scripts |
| **HTML5** | Web document structure and application entry page |
| **CSS3** | Layout, responsive styling and visual presentation |
| **Python** | Supporting scripts, data utilities, exports and legacy/demo backend modules |
| **SQL** | Supabase/PostgreSQL schema and database setup |

### Frameworks and services
- **React** — component-based web UI.
- **Vite** — frontend development server and production build tooling.
- **Node.js** — HTTP server and API handling.
- **Supabase** — hosted PostgreSQL and client integration, when configured.
- **Tailwind CSS** — utility styling dependencies/configuration present in the project.
- **GitHub** — source control.
- **Vercel** — intended web frontend hosting.
- **Render** — configured external API hosting target.

**Not part of the current web stack:** Kotlin, Android Studio, Gradle Android builds or APK distribution.

## Architecture

```text
Resident / Facility Staff
          |
          v
Responsive Web Browser
          |
          v
React + TypeScript / HTML + CSS
          |
          +----------------------+
          |                      |
          v                      v
       Vite build          Node.js HTTP / API
                                 |
                   +-------------+-------------+
                   |                           |
                   v                           v
            Supabase / PostgreSQL       Local / demo data
                   |
                   v
          Persistent application data

Deployment:
Web frontend  -> Vercel
API service   -> Render
Source code   -> GitHub
```

The exact request path depends on the selected run mode and deployment configuration. The root `vercel.json` currently contains an `/api/*` rewrite to the Render host documented below. Confirm that this target matches the active Render service before production use.

## Repository structure

```text
APP-PROJECT/
├── src/
│   ├── components/       # Reusable React UI components
│   ├── pages/            # Application pages
│   ├── lib/              # Supabase, types, data and utilities
│   ├── App.tsx           # Main React application
│   └── main.tsx          # React entry point
├── app/
│   └── applet/           # Supporting web utility package/scripts
├── index.html            # Web entry document
├── server.js             # Node.js HTTP server
├── server.ts             # Node server source copy/entry used by npm start
├── server.py             # Supporting Python server/demo implementation
├── supabase_schema.sql   # Database schema
├── .env.example          # Environment variable template
├── package.json          # npm scripts and dependencies
├── vite.config.ts        # Vite configuration
├── tsconfig.json         # TypeScript configuration
├── vercel.json           # Vercel routing configuration
└── README.md             # Project documentation
```

## Run locally

### 1. Install prerequisites
- Install **Node.js 20 LTS or newer** (includes npm): https://nodejs.org/
- Install **Visual Studio Code**: https://code.visualstudio.com/
- Download Git if you want to clone the repository: https://git-scm.com/

### 2. Get the project

**Option A — Clone with Git**
```bash
git clone https://github.com/mohan05713/APP-PROJECT.git
cd APP-PROJECT
```

**Option B — Download ZIP**
1. Open https://github.com/mohan05713/APP-PROJECT
2. Select **Code → Download ZIP**.
3. Extract the ZIP to a folder on your computer.

### 3. Open the folder in VS Code
1. Launch VS Code.
2. Select **File → Open Folder**.
3. Choose the extracted `APP-PROJECT` folder (the folder containing `package.json`).
4. Select **Terminal → New Terminal**.

### 4. Install dependencies
Run from the repository root:
```bash
npm install
```

### 5. Start the web application

**React + Vite development mode**
```bash
npm run dev
```
Open the local URL printed by Vite in the terminal (normally http://localhost:5173).

**Node.js web server mode**
```bash
npm start
```
The current npm start script runs `node server.ts`. The server defaults to port 3000; open http://localhost:3000.

Use the mode that matches the feature or workflow you are testing. Do not start both on the same port. If the terminal reports that a port is already in use, stop the other process or choose an available port.

### 6. Create a production frontend build
```bash
npm run build
```
Vite writes the production output to `dist/`. To preview the built frontend locally:
```bash
npm run preview
```

### 7. Stop the local server
Press `Ctrl + C` in the VS Code terminal.

## Environment configuration

1. Copy `.env.example` to a local `.env` file (or use the environment-file name required by the specific tool).
2. Replace placeholder values with credentials from your own Supabase project.
3. Never commit real secrets to GitHub.

Variables present in the repository template:
- `PORT` — Node server port (defaults to 3000).
- `NODE_ENV` — runtime mode.
- `SUPABASE_URL` — Supabase project URL.
- `SUPABASE_ANON_KEY` — public/anon client key; configure Row Level Security (RLS) correctly.
- `GEMINI_API_KEY` — only required if a corresponding Gemini-powered feature is enabled; keep secret keys server-side.

Do not paste service-role keys, database passwords, private API keys or resident credentials into source code, README files, screenshots or public issues.

## Supabase setup

1. Create or open your Supabase project: https://supabase.com/dashboard
2. Open the SQL Editor.
3. Review and run `supabase_schema.sql` against the intended project.
4. Copy the project URL and the appropriate client key from **Project Settings → API**.
5. Add the values to your local environment file and to the deployment platform's environment settings.
6. Configure Row Level Security policies for every table containing resident or operational information.
7. Test create, read, update and delete operations with non-sensitive sample data before using real records.

Supabase is only fully active when valid project credentials, schema, policies and application integration are configured. Do not assume that local demo data is automatically synchronized to a hosted database.

## Deployment

### Vercel — web frontend

1. Push the latest code to GitHub.
2. Open https://vercel.com/new
3. Import **mohan05713/APP-PROJECT**.
4. Select the repository's web frontend configuration. For the Vite app, use the repository root unless your Vercel project is intentionally configured for a subdirectory.
5. Confirm the build command is `npm run build` and output directory is `dist`.
6. Add required frontend environment variables in **Project → Settings → Environment Variables**. Only expose values intended for browser use (for example, a Supabase anon key protected by RLS).
7. Deploy and open the production domain shown by Vercel.
8. After changing environment variables, trigger a new deployment.

Vercel dashboard: https://vercel.com/dashboard

### Render — API / web server

The repository's `vercel.json` currently rewrites `/api/*` requests to:

`https://echo-apartment-api.onrender.com/api/*`

This is the **configured backend target in the repository**, not proof that the Render service is currently healthy or that the service name is still correct.

To deploy or verify it:
1. Open https://dashboard.render.com/
2. Select the intended EcoApartment API web service.
3. Confirm the linked GitHub repository and branch.
4. Confirm the service's root directory, build command and start command match the actual Node.js service entry point.
5. Ensure the service listens on the Render-provided `PORT` environment variable.
6. Add required secrets in Render's Environment settings, not in Git.
7. Deploy and inspect the service logs.
8. Verify the health endpoint before connecting the frontend.

Render dashboard: https://dashboard.render.com/

## Live links and health checks

| Resource | Link / status |
|---|---|
| GitHub repository | https://github.com/mohan05713/APP-PROJECT |
| Vercel dashboard | https://vercel.com/dashboard |
| Vercel new project/import | https://vercel.com/new |
| Production frontend URL | **Not verified in the repository** — copy the active domain from Vercel → Project → Settings → Domains |
| Render dashboard | https://dashboard.render.com/ |
| Render backend target configured in `vercel.json` | https://echo-apartment-api.onrender.com |
| Render API health endpoint (supported by Node server) | https://echo-apartment-api.onrender.com/api/health |
| Alternate health endpoint | https://echo-apartment-api.onrender.com/health |
| Local Vite frontend | http://localhost:5173 |
| Local Node server | http://localhost:3000 |
| Supabase dashboard | https://supabase.com/dashboard |

**Health-check response:** The Node server is coded to return JSON with a healthy status, service name, platform, flat count and timestamp for `/api/health`, `/health`, `/healthz` and `/ping`. The public URL above must still be opened to verify the deployed service. If it fails, check the active Render service URL and its logs, then update `vercel.json` to the correct backend host.

**Important:** A stable Vercel production URL cannot be inferred from a repository name. Do not use a guessed `.vercel.app` address. Add the actual domain from the Vercel project dashboard once deployment is confirmed.

## Testing and quality checks

Run the available checks before publishing changes:
```bash
npm install
npm run build
```

Where applicable, run the repository's Python test/support scripts separately:
```bash
python -m pytest
```
The repository also contains `test_system.py`; inspect its dependencies and test configuration before assuming it is a pytest suite.

Recommended manual smoke tests:
- Application loads without a blank screen.
- Navigation works across the main pages.
- Login and logout flows behave as expected using test accounts.
- Resident, complaint, notice and visitor views load.
- API requests reach the intended backend.
- Supabase reads/writes work with RLS enabled.
- Exports return the expected file format.
- Health endpoint returns HTTP 200 and healthy JSON.
- Browser console and server logs contain no unhandled errors.

## Security and data handling

- Never publish real resident names together with phone numbers, email addresses, flat details or passwords.
- Remove demo passwords from public documentation; use private test credentials stored in deployment secrets or a secure password manager.
- Do not commit `.env` files. Keep only placeholder templates such as `.env.example`.
- Use Supabase Row Level Security and least-privilege access.
- Validate and authorize every server-side operation; hiding a button in the UI is not access control.
- Do not store plaintext passwords. Use a secure authentication provider or salted password hashing.
- Treat resident census, family, visitor, payment and location data as sensitive.
- Use synthetic records for screenshots, demos and academic submissions.
- Configure CORS to allow only trusted frontend origins in production.
- Rotate any credential that has previously been committed to a public repository.

## Troubleshooting

| Problem | What to check |
|---|---|
| `npm: command not found` | Install Node.js LTS and reopen the terminal. |
| Dependency installation fails | Check Node/npm versions, network access and lockfile consistency; retry `npm install`. |
| Vite page does not open | Use the exact local URL printed by `npm run dev`; check port conflicts. |
| Port 3000 is busy | Stop the other process or set an available `PORT` before starting the Node server. |
| API returns 404/502 | Verify the Render service URL, service status, route and Vercel rewrite. |
| Health check fails | Inspect Render logs, start command, listening port and the health route. |
| Supabase connection fails | Verify URL/key, schema, RLS policies and network access. Never solve this by exposing a service-role key in the browser. |
| Changes are not visible after deploy | Confirm the correct Git branch was deployed and inspect the latest deployment build logs. |

## Roadmap

The following are **future enhancements**, not claims that they are already implemented:

- [ ] Consolidate the React/Vite frontend and Node API into a clearly documented production architecture.
- [ ] Add automated CI checks for linting, type checking, tests and production builds.
- [ ] Add role-based access control and secure authentication.
- [ ] Complete Supabase schema, migrations, RLS policies and backup procedures.
- [ ] Add audit logs for administrative actions and sensitive data access.
- [ ] Add robust API validation, rate limiting and structured error responses.
- [ ] Add real-time updates for complaints, notices and facility events.
- [ ] Connect verified IoT devices and clearly distinguish live telemetry from simulated data.
- [ ] Add accessibility checks and responsive browser testing.
- [ ] Add uptime monitoring, structured logs and deployment health alerts.
- [ ] Add privacy-conscious analytics and performance monitoring.
- [ ] Add automated backup/restore drills and documented disaster recovery.

## Contributing

1. Fork the repository or create a feature branch.
2. Keep changes focused and document new environment variables.
3. Run the relevant build and tests.
4. Never include secrets or real resident data in commits.
5. Open a pull request describing the change and how it was tested.

## License and project status

No license grant is stated here. Unless a LICENSE file is added, do not assume that the repository is open for unrestricted reuse.

**Repository:** https://github.com/mohan05713/APP-PROJECT  
**Maintained as:** Web-only EcoApartment OS project
