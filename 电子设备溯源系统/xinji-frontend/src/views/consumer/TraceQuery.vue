<template>
  <div class="query-page">
    <!-- 顶部导航 -->
    <header class="query-header">
      <div class="header-inner">
        <div class="logo" @click="$router.push('/')">
          <svg viewBox="0 0 48 48" width="36" height="36">
            <defs>
              <linearGradient id="chipGrad2" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#409EFF"/>
                <stop offset="100%" style="stop-color:#8B5CF6"/>
              </linearGradient>
            </defs>
            <rect x="8" y="8" width="32" height="32" rx="4" fill="url(#chipGrad2)" opacity="0.15"/>
            <rect x="12" y="12" width="24" height="24" rx="2" fill="none" stroke="url(#chipGrad2)" stroke-width="2"/>
            <rect x="18" y="18" width="12" height="12" rx="1" fill="url(#chipGrad2)"/>
          </svg>
          <span class="logo-text">芯迹溯源</span>
        </div>
        <el-button text @click="$router.push('/login')" class="admin-link">
          <el-icon><User /></el-icon>
          管理员入口
        </el-button>
      </div>
    </header>

    <!-- 搜索区 -->
    <section class="search-section">
      <div class="search-card glass-card">
        <h2 class="search-title">零件溯源查询</h2>
        <p class="search-desc">输入零件编号，追溯全生命周期流转轨迹</p>
        <div class="search-bar">
          <el-input
            v-model="partId"
            placeholder="请输入零件编号，如 XJ-20260630-0001"
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" size="large" class="search-btn" :loading="loading" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </div>
      </div>
    </section>

    <!-- 结果区 -->
    <section class="result-section" v-if="searched && !loading">
      <!-- 未找到 -->
      <div v-if="!partData" class="empty-result glass-card">
        <el-empty description="未找到该零件，请检查编号是否正确" :image-size="120" />
      </div>

      <!-- 零件信息卡片 -->
      <template v-if="partData">
        <div class="part-info-card glass-card">
          <div class="card-header">
            <h3>
              <el-icon color="#409EFF"><Cpu /></el-icon>
              零件基本信息
            </h3>
            <el-tag :type="partData.status === 1 ? 'success' : 'danger'" effect="light" round>
              {{ partData.status === 1 ? '有效' : '已停用' }}
            </el-tag>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <label>零件编号</label>
              <span class="highlight">{{ partData.partId }}</span>
            </div>
            <div class="info-item">
              <label>零件名称</label>
              <span>{{ partData.partName }}</span>
            </div>
            <div class="info-item">
              <label>零件类型</label>
              <span>{{ partData.partType }}</span>
            </div>
            <div class="info-item">
              <label>批次号</label>
              <span>{{ partData.batchNumber }}</span>
            </div>
            <div class="info-item">
              <label>生产厂商</label>
              <span>{{ partData.manufacturer }}</span>
            </div>
            <div class="info-item">
              <label>生产日期</label>
              <span>{{ partData.productionDate }}</span>
            </div>
          </div>
          <div v-if="partData.specification" class="info-spec">
            <label>规格描述</label>
            <p>{{ partData.specification }}</p>
          </div>
        </div>

        <!-- 溯源时间线 -->
        <div class="trace-timeline glass-card" v-if="partData.traceRecords && partData.traceRecords.length > 0">
          <div class="card-header">
            <h3>
              <el-icon color="#8B5CF6"><Timer /></el-icon>
              溯源轨迹（共 {{ partData.traceRecords.length }} 个节点）
            </h3>
          </div>
          <div class="timeline">
            <div
              v-for="(record, index) in partData.traceRecords"
              :key="record.id"
              class="timeline-item"
              :class="`timeline-${record.eventType}`"
            >
              <div class="timeline-dot">
                <el-icon :size="16">
                  <component :is="getEventIcon(record.eventType)" />
                </el-icon>
              </div>
              <div v-if="index < partData.traceRecords!.length - 1" class="timeline-line"></div>
              <div class="timeline-content">
                <div class="timeline-header">
                  <el-tag :type="getEventTagType(record.eventType)" size="small" effect="dark" round>
                    {{ record.eventTypeName }}
                  </el-tag>
                  <span class="timeline-time">{{ formatTime(record.eventTime) }}</span>
                </div>
                <p class="timeline-operator">
                  <el-icon><OfficeBuilding /></el-icon>
                  {{ record.operator }}
                  <span v-if="record.location" class="timeline-location">
                    <el-icon><Location /></el-icon>
                    {{ record.location }}
                  </span>
                </p>
                <p v-if="record.description" class="timeline-desc">{{ record.description }}</p>
                <p v-if="record.remark" class="timeline-remark">备注：{{ record.remark }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 无溯源记录 -->
        <div v-if="!partData.traceRecords || partData.traceRecords.length === 0" class="empty-result glass-card">
          <el-empty description="暂无溯源记录" :image-size="100" />
        </div>
      </template>
    </section>

    <!-- 页脚 -->
    <footer class="query-footer">
      <p>芯迹 · 电子设备零件溯源系统 © 2026</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Search, Cpu, Timer, OfficeBuilding, Location, User } from '@element-plus/icons-vue'
import { partsApi } from '@/api/parts'
import type { PartDTO } from '@/types'

const partId = ref('')
const loading = ref(false)
const searched = ref(false)
const partData = ref<PartDTO | null>(null)

async function handleSearch() {
  const value = partId.value.trim()
  if (!value) return

  loading.value = true
  searched.value = true
  partData.value = null

  try {
    const res = await partsApi.queryByPartId(value)
    partData.value = res.data.data
  } catch {
    partData.value = null
  } finally {
    loading.value = false
  }
}

function getEventIcon(eventType: string) {
  const map: Record<string, string> = {
    produce: 'Setting',
    pack: 'Box',
    distribute: 'Van',
    assemble: 'Connection',
    sale: 'Shop'
  }
  return map[eventType] || 'CircleCheck'
}

function getEventTagType(eventType: string) {
  const map: Record<string, string> = {
    produce: '',
    pack: 'info',
    distribute: 'success',
    assemble: 'warning',
    sale: 'danger'
  }
  return map[eventType] || ''
}

function formatTime(time: string) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}
</script>

<style lang="scss" scoped>
.query-page {
  min-height: 100vh;
  padding-bottom: 60px;
}

// 顶部导航
.query-header {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 0 24px;

  .header-inner {
    max-width: 900px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 60px;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;

    .logo-text {
      font-size: 20px;
      font-weight: 700;
      background: linear-gradient(135deg, #409EFF, #8B5CF6);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .admin-link {
    color: #909399;
    font-size: 13px;
  }
}

// 搜索区域
.search-section {
  max-width: 700px;
  margin: 60px auto 0;
  padding: 0 20px;
}

.search-card {
  padding: 48px 40px;
  text-align: center;
}

.search-title {
  font-size: 26px;
  font-weight: 700;
  background: linear-gradient(135deg, #409EFF, #8B5CF6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
}

.search-desc {
  color: #909399;
  font-size: 14px;
  margin-bottom: 32px;
}

.search-bar {
  display: flex;
  gap: 12px;

  :deep(.el-input__wrapper) {
    border-radius: 12px;
  }

  .search-btn {
    border-radius: 12px;
    background: linear-gradient(135deg, #409EFF, #8B5CF6);
    border: none;
    padding: 0 32px;

    &:hover {
      background: linear-gradient(135deg, #337ECC, #7C4DFF);
    }
  }
}

// 结果区
.result-section {
  max-width: 900px;
  margin: 32px auto 0;
  padding: 0 20px;
}

.empty-result {
  padding: 60px 20px;
}

// 零件信息卡片
.part-info-card {
  padding: 28px 32px;
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;

  h3 {
    font-size: 18px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 8px;
    color: #303133;
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;

  .info-item {
    display: flex;
    flex-direction: column;
    gap: 4px;

    label {
      font-size: 12px;
      color: #909399;
    }

    span {
      font-size: 15px;
      color: #303133;
      font-weight: 500;
      word-break: break-all;

      &.highlight {
        color: #409EFF;
        font-weight: 700;
        font-family: 'Courier New', monospace;
      }
    }
  }
}

.info-spec {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;

  label {
    font-size: 12px;
    color: #909399;
    display: block;
    margin-bottom: 6px;
  }

  p {
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
  }
}

// 时间线
.trace-timeline {
  padding: 28px 32px;
}

.timeline {
  padding-left: 0;
}

.timeline-item {
  display: flex;
  position: relative;
  padding-bottom: 24px;

  &:last-child { padding-bottom: 0; }
}

.timeline-dot {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--dot-color, #409EFF);
  color: #fff;
  flex-shrink: 0;
  margin-right: 16px;
  z-index: 1;
}

.timeline-line {
  position: absolute;
  left: 15px;
  top: 32px;
  bottom: 0;
  width: 2px;
  background: linear-gradient(to bottom, var(--dot-color, #409EFF), #e8e8e8);
}

.timeline-content {
  flex: 1;
  background: #fafbfc;
  border-radius: 12px;
  padding: 16px 20px;
  border: 1px solid #f0f0f0;
}

.timeline-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;

  .timeline-time {
    font-size: 13px;
    color: #909399;
  }
}

.timeline-operator {
  font-size: 14px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;

  .timeline-location {
    margin-left: 12px;
    color: #909399;
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.timeline-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin-top: 4px;
}

.timeline-remark {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  font-style: italic;
}

// 页脚
.query-footer {
  text-align: center;
  padding: 40px 0 20px;
  color: #C0C4CC;
  font-size: 13px;
}

@media (max-width: 768px) {
  .search-card {
    padding: 32px 20px;
  }

  .search-bar {
    flex-direction: column;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .part-info-card, .trace-timeline {
    padding: 20px 16px;
  }

  .timeline-content {
    padding: 12px 14px;
  }
}
</style>
