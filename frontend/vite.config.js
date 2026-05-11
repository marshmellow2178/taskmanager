import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
// Vite 8+ 는 Windows에서 rolldown 네이티브 바인딩(@rolldown/binding-win32-x64-msvc) 설치 이슈가 잦아,
// 포트폴리오/로컬 개발 안정성을 위해 Vite 6 + 표준 @vitejs/plugin-react 만 사용합니다.
export default defineConfig({
  plugins: [react()],
  // 개발 시 브라우저는 Vite(예: 5173)로만 요청하고, /api 는 Spring(8080)으로 넘김
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
