import axios from 'axios'
import type { ApiResponse } from '@/types'
import { ElMessage } from 'element-plus'

const http = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器 — 自动携带 token
http.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('xinji-token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器 — 统一错误处理
http.interceptors.response.use(
  (response) => {
    const res = response.data as ApiResponse
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('xinji-token')
      localStorage.removeItem('xinji-user')
      ElMessage.error('登录已过期，请重新登录')
      // 不在登录页才跳转
      if (!window.location.hash.includes('login')) {
        window.location.href = '/#/login'
      }
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default http
