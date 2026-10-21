import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 开发：npm run dev 访问 http://localhost:5173
// 构建：npm run build 直接输出到 Spring Boot 的 static/admin 目录，随 jar 一起部署
export default defineConfig({
  plugins: [vue()],
  base: '/admin/',
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: '../server/src/main/resources/static/admin',
    emptyOutDir: true,
    chunkSizeWarningLimit: 1500
  }
})
