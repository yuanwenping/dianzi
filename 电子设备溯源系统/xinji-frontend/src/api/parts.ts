import http from './index'
import type { ApiResponse, Part, PartDTO, PartPageResult } from '@/types'

export const partsApi = {
  list(params: { page: number; pageSize: number; keyword?: string }) {
    return http.get<ApiResponse<PartPageResult>>('/parts', { params })
  },
  getById(id: number) {
    return http.get<ApiResponse<PartDTO>>(`/parts/${id}`)
  },
  queryByPartId(partId: string) {
    return http.get<ApiResponse<PartDTO>>(`/parts/query/${partId}`)
  },
  add(data: Part) {
    return http.post<ApiResponse<Part>>('/parts', data)
  },
  update(id: number, data: Part) {
    return http.put<ApiResponse<Part>>(`/parts/${id}`, data)
  },
  delete(id: number) {
    return http.delete<ApiResponse<void>>(`/parts/${id}`)
  }
}
