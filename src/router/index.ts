import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'layout',
    component: () => import('@/views/layout.vue'),
    redirect: '/chat',
    // meta: {
    //   title: '首页'
    // }
    children: [
      {
        path: '/chat',
        name: 'chat',
        component: () => import('@/views/chat.vue'),
        meta: {
          title: 'chat'
        }
      },
      {
        path: '/datasets',
        name: 'datasets',
        component: () => import('@/views/datasets.vue'),
        meta: {
          title: '知识库'
        }
      },
      {
        path: '/setting',
        name: 'setting',
        component: () => import('@/views/setting.vue'),
        meta: {
          title: '设置'
        }
      }
    ]
  },
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