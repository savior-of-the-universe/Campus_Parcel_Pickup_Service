import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/stores/user'

// 创建 axios 实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    
    // 如果有 token，添加到请求头
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 添加请求时间戳，防止缓存
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now()
      }
    }
    
    console.log('请求发送:', {
      url: config.url,
      method: config.method,
      params: config.params,
      data: config.data
    })
    
    return config
  },
  (error) => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    console.log('响应接收:', {
      url: response.config.url,
      status: response.status,
      data: response.data
    })
    
    // 检查响应状态
    if (response.status === 200) {
      const { code, message } = response.data || {}
      // 保持向后兼容：返回整个 response，让调用方用 response.data.code 判定
      if (code === 200) {
        return response
      }
      // 其他业务状态码视为错误
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message || '请求失败'))
    }

    // 其他 HTTP 状态码
    ElMessage.error('请求失败')
    return Promise.reject(new Error('请求失败'))
  },

  (error) => {
    console.error('响应拦截器错误:', error)
    
    const { response } = error
    
    if (response) {
      const { status, data } = response
      
      switch (status) {
        case 401:
          // 未授权，清除认证信息并跳转到登录页
          ElMessage.error('登录已过期，请重新登录')
          const userStore = useUserStore()
          userStore.clearAuth()
          
          // 如果不在登录页，则跳转到登录页
          if (router.currentRoute.value.path !== '/login') {
            router.push('/login')
          }
          break
          
        case 403:
          // 权限不足
          ElMessage.error('权限不足，无法访问')
          break
          
        case 404:
          ElMessage.error('请求的资源不存在')
          break
          
        case 500:
          ElMessage.error('服务器内部错误，请稍后重试')
          break
          
        default:
          ElMessage.error(data?.message || `请求失败 (${status})`)
      }
    } else if (error.code === 'ECONNABORTED') {
      // 请求超时
      ElMessage.error('请求超时，请检查网络连接')
    } else if (error.message === 'Network Error') {
      // 网络错误
      ElMessage.error('网络连接失败，请检查网络')
    } else {
      // 其他错误
      ElMessage.error('请求失败，请重试')
    }
    
    return Promise.reject(error)
  }
)

// 封装常用的请求方法
const http = {
  // GET 请求
  get(url, params = {}) {
    return request.get(url, { params })
  },
  
  // POST 请求
  post(url, data = {}) {
    return request.post(url, data)
  },
  
  // PUT 请求
  put(url, data = {}) {
    return request.put(url, data)
  },
  
  // DELETE 请求
  delete(url, params = {}) {
    return request.delete(url, { params })
  },
  
  // 文件上传
  upload(url, formData) {
    return request.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  
  // 文件下载
  download(url, params = {}) {
    return request.get(url, {
      params,
      responseType: 'blob'
    })
  }
}

export default request
export { http }
