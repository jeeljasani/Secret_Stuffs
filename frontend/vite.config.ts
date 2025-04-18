import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import tsconfigPaths from 'vite-tsconfig-paths';
import path from 'path';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');

  return {
    plugins: [react(), tsconfigPaths()],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src'), // Alias for src
      },
    },
    define: {
      global: 'window', // Define `global` as `window`
    },
    server: {
      port: 3000,
      open: true, // Automatically open the browser
      proxy: {
        '/api': {
          target: env.VITE_API_BASE_URL,
          changeOrigin: true, // Handle CORS
          secure: false, // Disable SSL verification for development
        },
      },
    },
    build: {
      sourcemap: mode === 'development', // Enable sourcemaps in development
      rollupOptions: {
        output: {
          manualChunks(id) {
            if (id.includes('node_modules')) {
              return id.split('node_modules/')[1].split('/')[0];
            }
          },
        },
      },
    },
  };
});