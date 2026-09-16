import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      redirect: '/query'
    },
    // ----- 消费者查询（无需登录） -----
    {
      path: '/query',
      name: 'TraceQuery',
      component: () => import('@/views/consumer/TraceQuery.vue'),
      meta: { title: '溯源查询' }
    },
    // ----- 登录/注册 -----
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { title: '管理员登录' }
    },
    // ----- 管理后台 -----
    {
      path: '/admin',
      component: () => import('@/views/admin/Layout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: '/admin/dashboard'
        },
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/admin/Dashboard.vue'),
          meta: { title: '数据看板' }
        },
        {
          path: 'parts',
          name: 'PartManage',
          component: () => import('@/views/admin/PartManage.vue'),
          meta: { title: '零件管理' }
        },
        {
          path: 'parts/:id/traces',
          name: 'TraceManage',
          component: () => import('@/views/admin/TraceManage.vue'),
          meta: { title: '溯源记录管理' }
        }
      ]
    },
    // 404
    {
      path: '/:pathMatch(.*)*',
      redirect: '/query'
    }
  ]
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  document.title = (to.meta.title as string)
    ? `${to.meta.title} - 芯迹溯源`
    : '芯迹 - 电子设备零件溯源系统'

  if (to.matched.some(r => r.meta.requiresAuth)) {
    const token = localStorage.getItem('xinji-token')
    if (!token) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router
