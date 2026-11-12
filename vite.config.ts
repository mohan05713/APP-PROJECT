import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 3000,
    strictPort: true,
    host: true,
    watch: {
      ignored: ['**/build/**', '**/app/build/**', '**/*.keystore*'],
    },
  },
  build: {
    rollupOptions: {
      input: {
        main: 'index.html',
        smartOperations: 'smart-operations.html',
      },
    },
  },
  optimizeDeps: {
    entries: ['index.html'],
  },
});
