/** @type {import('tailwindcss').Config} */
export default {
  content: process.env.NODE_ENV == 'production' 
    ? ["./public/js/main.js"] 
    : ["./src/app/**/*.cljs", "./public/js/cljs-runtime/*.js"],
  theme: {
    extend: {},
    fontFamily: {
      "sans": ["Untitled Sans", "ui-sans-serif", "system-ui", "sans-serif", "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol", "Noto Color Emoji"],
      "serif": ["IBM Plex Serif", "ui-serif", "Georgia", "Cambria", "Times New Roman", "Times", "serif"]
    }
  },
  plugins: [],
}