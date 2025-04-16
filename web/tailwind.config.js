/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx,vue}",
  ],
  theme: {
    extend: {},
  },
  plugins: [
  //   function ({ addVariant }) {
  //     addVariant('child', '& > ');
  //     addVariant('child-hover', '& > *:hover');
  // }
  function({ addVariant }) {
    addVariant('last-child', '&>*:last-child');
  }
  ],
}