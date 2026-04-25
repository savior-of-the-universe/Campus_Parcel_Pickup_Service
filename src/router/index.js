import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

// 导入页面组件
import Login from '@/views/Login.vue'
import OrderList from '@/views/admin/OrderList.vue'
import ChatCenter from '@/views/admin/ChatCenter.vue'
import UserList from '@/views/admin/UserList.vue'

// 定义路由
const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: {
      title: '用户中心',
      requiresAuth: true,
      roles: ['USER', 'RUNNER']
    }
  },
  {
    path: '/admin',
    redirect: '/admin/users',
    meta: {
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
    path: '/admin/chat',
    name: 'ChatCenter',
    component: ChatCenter,
    meta: {
      title: '聊天中心',
      requiresAuth: true,
      roles: ['ADMIN']
    }
  },
  {
    path: '/user/orders',
    name: 'UserOrders',
    component: () => import('@/views/user/OrderList.vue'),
    meta: {
      title: '我的订单',
      requiresAuth: true,
      roles: ['USER', 'RUNNER']
    }
  },
  {
    path: '/user/tasks',
    name: 'UserTasks',
    component: () => import('@/views/user/TaskList.vue'),
    meta: {
      title: '任务管理',
      requiresAuth: true,
      roles: ['USER', 'RUNNER']
    }
  },
  {
    path: '/user/chat',
    name: 'UserChat',
    component: () => import('@/views/user/Chat.vue'),
    meta: {
      title: '在线客服',
      requiresAuth: true,
      roles: ['USER', 'RUNNER']
    }
  },
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('@/views/user/Profile.vue'),
    meta: {
      title: '个人中心',
      requiresAuth: true,
      roles: ['USER', 'RUNNER']
    }
  },
  {
    path: '/unauthorized',
    name: 'Unauthorized',
    component: () => import('@/views/Unauthorized.vue'),
    meta: {
      title: '权限不足',
      requiresAuth: false
    }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: {
      title: '页面不存在',
      requiresAuth: false
    }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 校园代取服务`
  }
  
  // 如果路由不需要认证，直接通过
  if (!to.meta.requiresAuth) {
    // 如果已登录用户访问登录页，重定向到对应首页
    if (to.path === '/login' && userStore.isLoggedIn) {
      if (userStore.isAdmin) {
        next('/admin/users')
      } else {
        next('/dashboard')
      }
      return
    }
    next()
    return
  }
  
  // 检查是否有 token
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    next('/login')
    return
  }
  
  // 如果用户信息为空，尝试获取当前用户信息
  if (!userStore.userInfo.id) {
    try {
      await userStore.getCurrentUser()
    } catch (error) {
      console.error('获取用户信息失败:', error)
      ElMessage.error('获取用户信息失败，请重新登录')
      userStore.clearAuth()
      next('/login')
      return
    }
  }
  
  // 检查角色权限
  if (to.meta.roles && Array.isArray(to.meta.roles)) {
    if (!to.meta.roles.includes(userStore.userRole)) {
      ElMessage.error('权限不足，无法访问该页面')
      next('/unauthorized')
      return
    }
  }
  
  next()
})

// 全局后置钩子
router.afterEach((to, from) => {
  // 可以在这里添加页面访问统计等逻辑
  console.log(`导航到: ${to.path}`)
})

export default router
