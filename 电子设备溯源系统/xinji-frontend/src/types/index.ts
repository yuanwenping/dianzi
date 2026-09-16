// ----- 通用响应 -----
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// ----- 用户 -----
export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  companyName?: string
}

export interface LoginResponse {
  token: string
  username: string
  companyName: string
  role: string
}

// ----- 零件 -----
export interface Part {
  id?: number
  partId?: string
  partName: string
  partType: string
  batchNumber: string
  manufacturer: string
  productionDate: string
  specification: string
  status?: number
  createdAt?: string
  updatedAt?: string
}

export interface PartDTO extends Part {
  traceRecords?: TraceRecordDTO[]
}

export interface PartPageResult {
  records: Part[]
  total: number
  size: number
  current: number
  pages: number
}

// ----- 溯源记录 -----
export interface TraceRecord {
  id?: number
  partId: number
  eventType: string
  operator: string
  eventTime: string
  location: string
  description: string
  remark?: string
  createdAt?: string
}

export interface TraceRecordDTO extends TraceRecord {
  eventTypeName: string
}

// ----- 看板 -----
export interface DashboardStats {
  totalParts: number
  activeParts: number
  totalTraceRecords: number
  eventTypeDistribution: Record<string, number>
}

// ----- 环节类型枚举 -----
export const EVENT_TYPES: Record<string, string> = {
  produce: '生产',
  pack: '封装',
  distribute: '分销',
  assemble: '组装',
  sale: '销售'
}
