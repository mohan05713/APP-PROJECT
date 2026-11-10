# Run EcoApartment OS Web App in VS Code

This guide covers the **web application only**. Android/Kotlin setup and APK instructions are not part of this project workflow.

## 1. Requirements
- Node.js 20 LTS or newer: https://nodejs.org/
- Visual Studio Code: https://code.visualstudio.com/
- Git (optional, for cloning): https://git-scm.com/

Check the installations in a terminal:
```bash
node --version
npm --version
```

## 2. Get the source code

Clone the repository:
```bash
git clone https://github.com/mohan05713/APP-PROJECT.git
cd APP-PROJECT
```

Or download the ZIP from https://github.com/mohan05713/APP-PROJECT and extract it.

## 3. Open the correct folder
1. Open VS Code.
2. Select **File → Open Folder**.
3. Select the repository root folder — the folder containing `package.json`.
4. Select **Terminal → New Terminal**.

## 4. Install dependencies
```bash
npm install
```

## 5. Start the web app

### React + Vite development mode
```bash
npm run dev
```
Open the URL printed in the terminal (usually http://localhost:5173).

### Node.js server mode
```bash
npm start
```
The current package script runs `node server.ts`. The server uses the `PORT` environment variable when set and otherwise defaults to port 3000. Open http://localhost:3000.

Use the mode required by the part of the application you are testing. Do not assume both modes use the same port or serve identical entry points.

## 6. Build and preview
```bash
npm run build
npm run preview
``
The Vite production build is generated in `dist/`.

## 7. Configure Supabase (when needed)
1. Create/open your project at https://supabase.com/dashboard.
2. Review `supabase_schema.sql` and apply the required schema.
3. Copy the project URL and public anon key from Supabase Project Settings → API.
4. Add the required values to your local environment configuration and to the relevant deployment environment.
5. Configure and test Row Level Security policies before using real resident data.

Never commit `.env`, service-role keys, database passwords, private API keys or real resident credentials.

## 8. Deployment links
- GitHub: https://github.com/mohan05713/APP-PROJECT
- Vercel dashboard: https://vercel.com/dashboard
- Vercel new project: https://vercel.com/new
- Render dashboard: https://dashboard.render.com/
- Render API target currently configured in `vercel.json`: https://echo-apartment-api.onrender.com
- Health check (verify the deployed service): https://echo-apartment-api.onrender.com/api/health
- Supabase dashboard: https://supabase.com/dashboard

The actual Vercel production domain must be copied from the Vercel project's Domains settings; do not guess a `.vercel.app` URL. The Render host above is the current repository rewrite target and must be checked against the active Render service.

## 9. Troubleshooting
- **Node/npm not found:** install Node.js LTS and restart VS Code.
- **Port already in use:** stop the existing process or choose a free port.
- **API errors:** check the Render service status, logs and `vercel.json` rewrite.
- **Supabase errors:** verify project URL, anon key, schema and RLS policies.
- **Build errors:** run `npm install` again and inspect the first error in the terminal.

For full architecture, feature, deployment and security documentation, see [README.md](README.md).
