const defaultTheme = require('tailwindcss/defaultTheme');

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', ...defaultTheme.fontFamily.sans],
      },
      colors: {
        // Dark Premium specific background colors
        zinc: {
          850: '#1f1f22', // Card bg
          900: '#18181b', // Main bg
          950: '#09090b', // Deep bg
        },
        // Primary accent color (Violet/Indigo based)
        primary: {
          50: '#f5f3ff',
          100: '#ede9fe',
          200: '#ddd6fe',
          300: '#c4b5fd',
          400: '#a78bfa',
          500: '#8b5cf6',
          600: '#7c3aed',
          700: '#6d28d9',
          800: '#5b21b6',
          900: '#4c1d95',
          950: '#2e1065',
        },
        // Danger/Error
        danger: {
          500: '#ef4444',
          600: '#dc2626',
        },
        // Success
        success: {
          500: '#22c55e',
          600: '#16a34a',
        }
      },
      boxShadow: {
        'glow': '0 0 20px rgba(139, 92, 246, 0.15)',
        'glow-sm': '0 0 10px rgba(139, 92, 246, 0.1)',
      }
    },
  },
  plugins: [
    require('@tailwindcss/forms')({
      strategy: 'class',
    }),
    require('@tailwindcss/line-clamp'),
  ],
}
