import http from './index'
import type { ApiResponse, TraceRecord, TraceRecordDTO } from '@/types'

export const traceRecordsApi = {
  listByPartId(partId: number) {
    return http.get<ApiResponse<TraceRecordDTO[]>>(`/trace-records/part/${partId}`)
  },
  add(data: TraceRecord) {
    return http.post<ApiResponse<TraceRecord>>('/trace-records', data)
  }
}
