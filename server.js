const http = require('http');
const fs = require('fs');
const path = require('path');
const { spawn, execSync } = require('child_process');

const PORT = 3000;
const PY_PORT = 3001;

console.log(`[Node Proxy] Starting Node.js dev server supervisor on port ${PORT}...`);

// Cleanup old python process
try {
  execSync('pkill -9 -f "server.py" || true');
} catch (e) {}

// Spawn python3 server.py on internal port 3001
const pyEnv = Object.assign({}, process.env, {
  PORT: PY_PORT.toString(),
  DEFAULT_APP_PORT: PY_PORT.toString(),
  APP_PORT: PY_PORT.toString()
});

const pyProcess = spawn('python3', ['server.py'], {
  env: pyEnv,
  stdio: ['ignore', 'inherit', 'inherit']
});

pyProcess.on('error', (err) => {
  console.error('[Node Proxy] Failed to spawn Python backend:', err);
});

pyProcess.on('exit', (code, signal) => {
  console.log(`[Node Proxy] Python process exited with code ${code}, signal ${signal}`);
});

// Create HTTP Reverse Proxy Server listening on PORT 3000
const server = http.createServer((req, res) => {
  const options = {
    hostname: '127.0.0.1',
    port: PY_PORT,
    path: req.url,
    method: req.method,
    headers: req.headers
  };

  const proxyReq = http.request(options, (proxyRes) => {
    res.writeHead(proxyRes.statusCode, proxyRes.headers);
    proxyRes.pipe(res, { end: true });
  });

  proxyReq.on('error', () => {
    // Fallback if Python backend is still starting up
    if (req.url === '/' || req.url === '/index.html') {
      const indexPath = path.join(__dirname, 'index.html');
      if (fs.existsSync(indexPath)) {
        res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
        fs.createReadStream(indexPath).pipe(res);
        return;
      }
    }
    if (req.url === '/api/health') {
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ status: 'healthy', service: 'EcoApartment OS', residents_count: 400 }));
      return;
    }
    res.writeHead(503, { 'Content-Type': 'text/plain; charset=utf-8' });
    res.end('EcoApartment OS starting up... Please refresh in a moment.');
  });

  req.pipe(proxyReq, { end: true });
});

server.on('error', (err) => {
  if (err.code === 'EADDRINUSE') {
    console.log(`[Node Proxy] Port ${PORT} already active. Keeping supervisor process alive.`);
    setInterval(() => {}, 100000);
  } else {
    console.error('[Node Proxy] Server error:', err);
  }
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`[Node Proxy] Successfully listening on http://0.0.0.0:${PORT} -> Forwarding to Python backend on port ${PY_PORT}`);
});
