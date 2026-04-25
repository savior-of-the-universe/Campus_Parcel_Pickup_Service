import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const loading = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed(() => userInfo.value.role || '')
  const isAdmin = computed(() => userRole.value === 'ADMIN')
  const isUser = computed(() => userRole.value === 'USER')
  const isRunner = computed(() => userRole.value === 'RUNNER')

  // 设置认证信息
  const setAuth = (authToken, user) => {
    token.value = authToken
    userInfo.value = user
    
    // 持久化到 localStorage
    localStorage.setItem('token', authToken)
    localStorage.setItem('userInfo', JSON.stringify(user))
  }

  // 清除认证信息
  const clearAuth = () => {
    token.value = ''
    userInfo.value = {}
    
    // 清除 localStorage
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 登录
  const login = async (credentials) => {
    try {
      loading.value = true
      
      const response = await request.post('/api/auth/login', credentials)
      
      if (response.code === 200 && response.data) {
        const { token: authToken, userInfo: user } = response.data
        setAuth(authToken, user)
        return { success: true, userInfo: user }
      } else {
        return { success: false, message: response.message || '登录失败' }
      }
    } catch (error) {
      console.error('登录错误:', error)
      let errorMessage = '登录失败'
      
      if (error.response) {
        const { status, data } = error.response
        if (status === 401) {
          errorMessage = data?.message || '用户名或密码错误'
        } else if (status === 500) {
          errorMessage = '服务器错误，请稍后重试'
        } else {
          errorMessage = data?.message || `请求失败 (${status})`
        }
      } else if (error.request) {
        errorMessage = '网络连接失败，请检查网络'
      }
      
      return { success: false, message: errorMessage }
    } finally {
      loading.value = false
    }
  }

  // 退出登录
  const logout = async () => {
    try {
      // 调用后端退出接口（可选）
      await request.post('/api/auth/logout')
    } catch (error) {
      console.warn('调用退出接口失败:', error)
    } finally {
      // 无论后端接口是否成功，都清除本地认证信息
      clearAuth()
    }
  }

  // 获取当前用户信息
  const getCurrentUser = async () => {
    try {
      if (!token.value) {
        throw new Error('未登录')
      }
      
      const response = await request.get('/api/auth/me')
      
      if (response.code === 200 && response.data) {
        userInfo.value = response.data
        localStorage.setItem('userInfo', JSON.stringify(response.data))
        return response.data
      } else {
        throw new Error(response.message || '获取用户信息失败')
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      
      // 如果是认证错误，清除本地认证信息
      if (error.response?.status === 401) {
        clearAuth()
      }
      
      throw error
    }
  }

  // 检查权限
  const hasPermission = (requiredRole) => {
    if (!token.value) return false
    
    if (Array.isArray(requiredRole)) {
      return requiredRole.includes(userRole.value)
    }
    
    return userRole.value === requiredRole
  }

  // 检查是否为管理员或指定角色
  const hasAnyRole = (roles) => {
    if (!token.value) return false
    return roles.some(role => userRole.value === role)
  }

  // 更新用户信息
  const updateUserInfo = (newUserInfo) => {
    userInfo.value = { ...userInfo.value, ...newUserInfo }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  return {
    // 状态
    token,
    userInfo,
    loading,
    
    // 计算属性
    isLoggedIn,
    userRole,
    isAdmin,
    isUser,
    isRunner,
    
    // 方法
    setAuth,
    clearAuth,
    login,
    logout,
    getCurrentUser,
    hasPermission,
    hasAnyRole,
    updateUserInfo
  }
})
