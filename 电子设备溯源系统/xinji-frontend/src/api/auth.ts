import http from './index'
import type { ApiResponse, LoginRequest, LoginResponse, RegisterRequest } from '@/types'

export const authApi = {
  login(data: LoginRequest) {
    return http.post<ApiResponse<LoginResponse>>('/auth/login', data)
  },
  register(data: RegisterRequest) {
    return http.post<ApiResponse<void>>('/auth/register', data)
  }
}
