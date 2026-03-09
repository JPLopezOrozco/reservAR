import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  // server: { host: '127.0.0.1', port: 5173, strictPort: true }
  server: {
    host: true,
    allowedHosts: ['macrometeorological-supervastly-larissa.ngrok-free.dev'],
  },
})