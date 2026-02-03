/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        'neon-blue': '#00f3ff',
        'neon-pink': '#ff00ff',
        'neon-purple': '#bc13fe',
        'dark-bg': '#0f0f13',
        'glass-white': 'rgba(255, 255, 255, 0.1)',
        'glass-border': 'rgba(255, 255, 255, 0.2)',
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
        'hero-pattern': "url('/assets/bg-hero.jpg')",
      },
      dropShadow: {
        'neon': '0 0 5px rgba(255, 255, 255, 0.8)',
      },
      boxShadow: {
        'neon-purple': '0 0 15px rgba(188, 19, 254, 0.5)',
      }
    },
  },
  plugins: [],
}
