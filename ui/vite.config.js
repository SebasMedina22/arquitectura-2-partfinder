import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api/aggregator': { target: 'http://localhost:8081', changeOrigin: true, rewrite: p => p.replace(/^\/api\/aggregator/, '') },
      '/api/inventory':  { target: 'http://localhost:8082', changeOrigin: true, rewrite: p => p.replace(/^\/api\/inventory/, '') },
      '/api/trends':     { target: 'http://localhost:8083', changeOrigin: true, rewrite: p => p.replace(/^\/api\/trends/, '') },
    }
  }
})
