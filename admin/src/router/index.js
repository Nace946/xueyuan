import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  {
    path: '/',
    component: () => import('../layout/index.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '概览' } },
      { path: 'house', name: 'HouseList', component: () => import('../views/house/List.vue'), meta: { title: '房源管理' } },
      { path: 'job', name: 'JobList', component: () => import('../views/job/List.vue'), meta: { title: '招聘管理' } },
      { path: 'banner', name: 'BannerList', component: () => import('../views/banner/List.vue'), meta: { title: '首页轮播' } }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin_token')
  if (to.path !== '/login' && !token) {
    next({ path: '/login' })
  } else {
    next()
  }
})

export default router
