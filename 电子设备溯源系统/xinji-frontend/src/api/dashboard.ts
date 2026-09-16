import http from './index'
import type { ApiResponse, DashboardStats } from '@/types'

export const dashboardApi = {
  getStats() {
    return http.get<ApiResponse<DashboardStats>>('/dashboard/stats')
  }
}
