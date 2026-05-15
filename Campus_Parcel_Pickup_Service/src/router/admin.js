import { createRouter, createWebHistory } from 'vue-router'
import OrderList from '@/views/admin/OrderList.vue'
import ChatCenter from '@/views/admin/ChatCenter.vue'
import UserList from '@/views/admin/UserList.vue'

const routes = [
  {
    path: '/admin/orders',
    name: 'OrderList',
    component: OrderList,
    meta: {
      title: '订单管理',
      requiresAuth: true,
      roles: ['ADMIN']
    }
  },
  {
    path: '/admin/users',
    name: 'UserList',
    component: UserList,
    meta: {
      title: '用户管理',
      requiresAuth: true,
      roles: ['ADMIN']
    }
  },
  {
    path: '/admin/chat',
    name: 'ChatCenter',
    component: ChatCenter,
    meta: {
      title: '聊天中心',
      requiresAuth: true,
      roles: ['ADMIN']
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 校园代取服务`
  }
  
  // 检查认证和权限
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      // 重定向到登录页
      next('/login')
      return
    }
    
    // 检查角色权限
    if (to.meta.roles) {
      // 这里需要根据实际的用户信息获取方式进行调整
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      if (!to.meta.roles.includes(userInfo.role)) {
        // 权限不足，重定向到首页或显示错误
        next('/unauthorized')
        return
      }
    }
  }
  
  next()
})

export default router
