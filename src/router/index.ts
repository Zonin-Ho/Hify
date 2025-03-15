import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/home.vue'),
    meta: {
      title: '首页'
    }
  },
  // {
  //   path: '/about',
  //   name: 'About',
  //   component: () => import('@/views/AboutView.vue'),
  //   meta: {
  //     title: '关于'
  //   }
  // }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})
router.beforeEach((to, _from, next) => {
  document.title = (to.meta.title as string) || 'Hify智能问答系统';
  next();
});
export default router