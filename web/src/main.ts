import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import './style.css'
import App from './App.vue'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import "tailwindcss/tailwind.css";
import Particles from "@tsparticles/vue3";
import { loadSlim } from "@tsparticles/slim";


const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(Antd);
app.use(Particles, {
    init: async engine => {
      // await loadFull(engine); // you can load the full tsParticles library from "tsparticles" if you need it
      await loadSlim(engine); // or you can load the slim version from "@tsparticles/slim" if don't need Shapes or Animations
    },
  })
app.mount('#app')
