# Run EcoApartment OS Web App in VS Code

This guide covers the **web application only**.

## 1. Requirements
- Node.js 20 LTS or newer: https://nodejs.org/
- Visual Studio Code: https://code.visualstudio.com/
- Git (optional): https://git-scm.com/

Check:
```bash
node --version
npm --version
```

## 2. Get the source
```bash
git clone https://github.com/mohan05713/APP-PROJECT.git
cd APP-PROJECT
```

Or download the ZIP from the GitHub repository and extract it.

## 3. Open the repository root
In VS Code choose **File → Open Folder** and select the folder containing `package.json`. Open **Terminal → New Terminal**.

## 4. Install dependencies
```bash
npm install
```

## 5. Start the frontend
```bash
npm run dev
```
Open the URL printed by Vite (normally http://localhost:5173).

## 6. Start the Node server (another terminal)
```bash
npm start
```
The current package script runs `node server.ts`; it uses `PORT` if set and otherwise defaults to port 3000. Open http://localhost:3000.

Render is configured separately to start `node server.js`. These commands are not identical.

## 7. Build and preview
```bash
npm run build
npm run preview
``
The Vite production build is written to `dist/`.

## 8. Supabase (when needed)
1. Open https://supabase.com/dashboard.
2. Review `supabase_schema.sql` before applying it.
3. Add the project URL and public anon key to the correct environment.
4. Configure and test Row Level Security (RLS) before using resident data.

Never commit `.env`, service-role keys, database passwords, private API keys or real resident credentials.

## 9. Deployment links
- Frontend: https://ecoapartment.vercel.app
- Latest Vercel deployment: https://ecoapartment-h05n57u4q-mohan05713.vercel.app
- Backend: https://ecoapartment-os-api.onrender.com
- Health endpoint: https://ecoapartment-os-api.onrender.com/api/health
- Render service dashboard: https://dashboard.render.com/web/srv-dbp3qabm8hqs73cogvc0
- Vercel project dashboard: https://vercel.com/mohan05713/ecoapartment
- GitHub: https://github.com/mohan05713/APP-PROJECT

Vercel currently reports SSO protection enabled for non-custom domains, so an authorized sign-in may be required. The Render server implements `/api/health`; the Render service's automatic health-check path is currently blank. Configure `/api/health` in Render if automatic health monitoring is desired.

## 10. Troubleshooting
- **Node/npm not found:** install Node.js LTS and reopen VS Code.
- **Port busy:** stop the process using it or choose another `PORT`.
- **API errors:** check the Vercel rewrite, Render service status and logs.
- **Health endpoint fails:** inspect Render logs, start command and route.
- **Supabase errors:** verify URL/key, schema, RLS and environment settings.
- **Build errors:** inspect the first terminal error, then run `npm install` again if dependencies are incomplete.

For the full product overview, architecture, features, roadmap and security guidance, see [README.md](README.md).
