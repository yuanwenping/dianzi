<template>
  <div class="trace-manage">
    <!-- 返回按钮 -->
    <div class="page-header">
      <div style="display: flex; align-items: center; gap: 12px;">
        <el-button text @click="$router.push('/admin/parts')">
          <el-icon><ArrowLeft /></el-icon>
          返回零件列表
        </el-button>
        <h2 class="page-title" style="margin-bottom: 0;">溯源记录管理</h2>
      </div>
      <el-button type="primary" class="xinji-btn" @click="openAddDialog">
        <el-icon><Plus /></el-icon>
        添加溯源节点
      </el-button>
    </div>

    <!-- 零件基本信息 -->
    <div class="part-summary glass-card" v-if="part">
      <div class="summary-grid">
        <div class="summary-item">
          <label>零件编号</label>
          <span class="part-id">{{ part.partId }}</span>
        </div>
        <div class="summary-item">
          <label>零件名称</label>
          <span>{{ part.partName }}</span>
        </div>
        <div class="summary-item">
          <label>类型</label>
          <span>{{ part.partType }}</span>
        </div>
        <div class="summary-item">
          <label>厂商</label>
          <span>{{ part.manufacturer }}</span>
        </div>
        <div class="summary-item">
          <label>批次号</label>
          <span>{{ part.batchNumber }}</span>
        </div>
      </div>
    </div>

    <!-- 溯源时间线 -->
    <div class="trace-list glass-card" v-loading="loading">
      <div v-if="traces.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无溯源记录" :image-size="100">
          <el-button type="primary" @click="openAddDialog">添加第一条记录</el-button>
        </el-empty>
      </div>

      <div v-else class="timeline">
        <div
          v-for="(record, index) in traces"
          :key="record.id"
          class="timeline-item"
          :class="`timeline-${record.eventType}`"
        >
          <div class="timeline-dot">
            <el-icon :size="14">
              <component :is="getEventIcon(record.eventType)" />
            </el-icon>
          </div>
          <div v-if="index < traces.length - 1" class="timeline-line"></div>
          <div class="timeline-content">
            <div class="timeline-top">
              <el-tag :type="getEventTagType(record.eventType)" size="small" effect="dark" round>
                {{ record.eventTypeName }}
              </el-tag>
              <span class="timeline-time">{{ formatTime(record.eventTime) }}</span>
            </div>
            <div class="timeline-body">
              <p><strong>操作方：</strong>{{ record.operator }}</p>
              <p v-if="record.location"><strong>地点：</strong>{{ record.location }}</p>
              <p v-if="record.description"><strong>描述：</strong>{{ record.description }}</p>
              <p v-if="record.remark" class="remark"><strong>备注：</strong>{{ record.remark }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加溯源节点对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="添加溯源节点"
      width="550px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="traceForm" :rules="traceRules" label-width="90px">
        <el-form-item label="环节类型" prop="eventType">
          <el-select v-model="traceForm.eventType" placeholder="选择环节类型" style="width: 100%">
            <el-option label="生产" value="produce" />
            <el-option label="封装" value="pack" />
            <el-option label="分销" value="distribute" />
            <el-option label="组装" value="assemble" />
            <el-option label="销售" value="sale" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作方" prop="operator">
          <el-input v-model="traceForm.operator" placeholder="例如：深圳封装厂" />
        </el-form-item>
        <el-form-item label="事件时间" prop="eventTime">
          <el-date-picker
            v-model="traceForm.eventTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="地点" prop="location">
          <el-input v-model="traceForm.location" placeholder="例如：广东省深圳市宝安区" />
        </el-form-item>
        <el-form-item label="事件描述" prop="description">
          <el-input
            v-model="traceForm.description"
            type="textarea"
            :rows="3"
            placeholder="描述该环节的具体操作..."
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="traceForm.remark" placeholder="可选备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleAddTrace">
          确认添加
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { partsApi } from '@/api/parts'
import { traceRecordsApi } from '@/api/trace-records'
import type { PartDTO, TraceRecordDTO } from '@/types'
import type { FormInstance, FormRules } from 'element-plus'

const route = useRoute()
const partId = Number(route.params.id)

const part = ref<PartDTO | null>(null)
const traces = ref<TraceRecordDTO[]>([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const traceForm = reactive({
  eventType: '',
  operator: '',
  eventTime: '',
  location: '',
  description: '',
  remark: ''
})

const traceRules: FormRules = {
  eventType: [{ required: true, message: '请选择环节类型', trigger: 'change' }],
  operator: [{ required: true, message: '请输入操作方', trigger: 'blur' }],
  eventTime: [{ required: true, message: '请选择事件时间', trigger: 'change' }],
  location: [{ required: true, message: '请输入地点', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const [partRes, traceRes] = await Promise.all([
      partsApi.getById(partId),
      traceRecordsApi.listByPartId(partId)
    ])
    part.value = partRes.data.data
    traces.value = traceRes.data.data || []
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

function openAddDialog() {
  Object.assign(traceForm, {
    eventType: '', operator: '', eventTime: '',
    location: '', description: '', remark: ''
  })
  dialogVisible.value = true
}

async function handleAddTrace() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    await traceRecordsApi.add({
      partId: partId,
      eventType: traceForm.eventType,
      operator: traceForm.operator,
      eventTime: traceForm.eventTime,
      location: traceForm.location,
      description: traceForm.description,
      remark: traceForm.remark
    })
    ElMessage.success('溯源节点添加成功')
    dialogVisible.value = false
    loadData()
  } catch {
    // handled
  } finally {
    submitLoading.value = false
  }
}

function getEventIcon(type: string) {
  const map: Record<string, string> = {
    produce: 'Setting', pack: 'Box', distribute: 'Van',
    assemble: 'Connection', sale: 'Shop'
  }
  return map[type] || 'CircleCheck'
}

function getEventTagType(type: string) {
  const map: Record<string, string> = {
    produce: '', pack: 'info', distribute: 'success',
    assemble: 'warning', sale: 'danger'
  }
  return map[type] || ''
}

function formatTime(time: string) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.trace-manage {
  max-width: 1000px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

// 零件摘要
.part-summary {
  padding: 20px 24px;
  margin-bottom: 24px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;

  .summary-item {
    display: flex;
    flex-direction: column;
    gap: 4px;

    label {
      font-size: 12px;
      color: #909399;
    }

    span {
      font-size: 14px;
      font-weight: 500;
      color: #303133;

      &.part-id {
        font-family: 'Courier New', monospace;
        color: #409EFF;
        font-weight: 700;
      }
    }
  }
}

// 时间线
.trace-list {
  padding: 24px 32px;
  min-height: 200px;
}

.empty-state {
  padding: 40px 0;
}

.timeline {
  padding-left: 0;
}

.timeline-item {
  display: flex;
  position: relative;
  padding-bottom: 28px;

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
  margin-right: 18px;
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

.timeline-top {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
  flex-wrap: wrap;

  .timeline-time {
    font-size: 13px;
    color: #909399;
  }
}

.timeline-body {
  p {
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
    margin-bottom: 2px;

    strong {
      color: #303133;
    }
  }

  .remark {
    color: #909399;
    font-style: italic;
    font-size: 13px;
  }
}

@media (max-width: 768px) {
  .trace-list {
    padding: 16px 12px;
  }

  .timeline-content {
    padding: 12px 14px;
  }
}
</style>
