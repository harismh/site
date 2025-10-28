/** @type {import('tailwindcss').Config} */
export default {
  content:
    process.env.NODE_ENV == "production"
      ? ["./public/js/main.js"]
      : ["./src/app/**/*.cljs", "./public/js/cljs-runtime/*.js"],
  theme: {
    extend: {
      colors: {
        sky: {
          400: "#55A5DA",
        },
        zinc: {
          100: "#fffff8",
          500: "#777777",
          900: "#10100e",
        },
      },
      typography: () => {
        const makeTheme = (color) => ({
          color,
          "--tw-prose-body": color,
          "--tw-prose-headings": color,
          "--tw-prose-lead": color,
          "--tw-prose-links": color,
          "--tw-prose-bold": color,
          "--tw-prose-counters": color,
          "--tw-prose-bullets": color,
          "--tw-prose-hr": color,
          "--tw-prose-quotes": color,
          "--tw-prose-quote-borders": color,
          "--tw-prose-captions": color,
          "--tw-prose-code": color,
          "--tw-prose-pre-code": color,
          "--tw-prose-th-borders": color,
          "--tw-prose-td-borders": color,
        });

        return {
          DEFAULT: {
            css: makeTheme("#10100e"),
          },
          invert: {
            css: makeTheme("#fffff8"),
          },
        };
      },
    },
    fontFamily: {
      sans: [
        "Satoshi",
        "ui-sans-serif",
        "system-ui",
        "sans-serif",
        "Apple Color Emoji",
        "Segoe UI Emoji",
        "Segoe UI Symbol",
        "Noto Color Emoji",
      ],
      serif: [
        "Erode",
        "ui-serif",
        "Georgia",
        "Cambria",
        "Times New Roman",
        "Times",
        "serif",
      ],
    },
  },
  plugins: [require("@tailwindcss/typography")],
};
