<template>
  <div class="dashboard">
    <h2 class="page-title">数据看板</h2>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card glass-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF20, #409EFF40); color: #409EFF;">
          <el-icon :size="28"><Cpu /></el-icon>
        </div>
        <div class="stat-body">
          <span class="stat-value">{{ stats?.totalParts ?? '-' }}</span>
          <span class="stat-label">零件总数</span>
        </div>
      </div>

      <div class="stat-card glass-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #67C23A20, #67C23A40); color: #67C23A;">
          <el-icon :size="28"><CircleCheck /></el-icon>
        </div>
        <div class="stat-body">
          <span class="stat-value">{{ stats?.activeParts ?? '-' }}</span>
          <span class="stat-label">有效零件</span>
        </div>
      </div>

      <div class="stat-card glass-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C20, #E6A23C40); color: #E6A23C;">
          <el-icon :size="28"><Timer /></el-icon>
        </div>
        <div class="stat-body">
          <span class="stat-value">{{ stats?.totalTraceRecords ?? '-' }}</span>
          <span class="stat-label">溯源记录</span>
        </div>
      </div>

      <div class="stat-card glass-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #8B5CF620, #8B5CF640); color: #8B5CF6;">
          <el-icon :size="28"><Connection /></el-icon>
        </div>
        <div class="stat-body">
          <span class="stat-value">{{ eventTypesCount }}</span>
          <span class="stat-label">涉及环节类型</span>
        </div>
      </div>
    </div>

    <!-- 图表 -->
    <div class="chart-card glass-card" v-if="stats">
      <h3 class="chart-title">
        <el-icon color="#8B5CF6"><PieChart /></el-icon>
        溯源环节分布
      </h3>
      <div ref="chartRef" class="chart-container"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { Cpu, CircleCheck, Timer, Connection, PieChart } from '@element-plus/icons-vue'
import { dashboardApi } from '@/api/dashboard'
import type { DashboardStats } from '@/types'
import * as echarts from 'echarts'

const stats = ref<DashboardStats | null>(null)
const chartRef = ref<HTMLElement>()
let chart: echarts.ECharts | null = null

const eventTypesCount = computed(() => {
  if (!stats.value) return 0
  const dist = stats.value.eventTypeDistribution
  return Object.values(dist).filter(v => v > 0).length
})

async function loadStats() {
  try {
    const res = await dashboardApi.getStats()
    stats.value = res.data.data
    await nextTick()
    renderChart()
  } catch {
    // handled by interceptor
  }
}

function renderChart() {
  if (!chartRef.value || !stats.value) return

  if (chart) chart.dispose()
  chart = echarts.init(chartRef.value)

  const dist = stats.value.eventTypeDistribution
  const data = Object.entries(dist)
    .filter(([, v]) => v > 0)
    .map(([name, value]) => ({ name, value }))

  const colors = ['#409EFF', '#8B5CF6', '#67C23A', '#E6A23C', '#F56C6C']

  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      bottom: 0,
      textStyle: { fontSize: 12 }
    },
    series: [{
      type: 'pie',
      radius: ['50%', '75%'],
      center: ['50%', '48%'],
      roseType: 'area',
      itemStyle: {
        borderRadius: 6,
        borderColor: '#fff',
        borderWidth: 3
      },
      color: colors,
      label: {
        show: true,
        formatter: '{b}\n{c}条'
      },
      data
    }]
  })
}

function handleResize() {
  chart?.resize()
}

onMounted(() => {
  loadStats()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard {
  max-width: 1100px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: default;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-body {
  display: flex;
  flex-direction: column;

  .stat-value {
    font-size: 28px;
    font-weight: 700;
    color: #303133;
  }

  .stat-label {
    font-size: 13px;
    color: #909399;
    margin-top: 2px;
  }
}

.chart-card {
  padding: 24px 28px;
}

.chart-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.chart-container {
  width: 100%;
  height: 380px;
}

@media (max-width: 900px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 560px) {
  .stats-row {
    grid-template-columns: 1fr;
  }

  .chart-container {
    height: 280px;
  }
}
</style>
