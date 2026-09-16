<template>
  <div class="part-manage">
    <div class="page-header">
      <h2 class="page-title">零件管理</h2>
      <el-button type="primary" class="xinji-btn" @click="openAddDialog">
        <el-icon><Plus /></el-icon>
        新增零件
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <el-input
        v-model="keyword"
        placeholder="搜索零件编号 / 名称 / 批次号 / 厂商..."
        clearable
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
    </div>

    <!-- 零件表格 -->
    <div class="table-card glass-card">
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="partId" label="零件编号" min-width="160">
          <template #default="{ row }">
            <span class="part-id">{{ row.partId }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="partName" label="零件名称" min-width="120" />
        <el-table-column prop="partType" label="类型" width="90" />
        <el-table-column prop="batchNumber" label="批次号" width="110" />
        <el-table-column prop="manufacturer" label="生产厂商" min-width="120" />
        <el-table-column prop="productionDate" label="生产日期" width="110" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small" effect="light" round>
              {{ row.status === 1 ? '有效' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEditDialog(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="info" link size="small" @click="goToTraces(row)">
              <el-icon><Timer /></el-icon> 溯源
            </el-button>
            <el-button type="warning" link size="small" @click="handleToggleStatus(row)"
                       v-if="row.status === 1">
              <el-icon><VideoPause /></el-icon> 停用
            </el-button>
            <el-button type="success" link size="small" @click="handleToggleStatus(row)"
                       v-else>
              <el-icon><VideoPlay /></el-icon> 启用
            </el-button>
            <el-popconfirm title="确定删除该零件吗？" @confirm="handleDelete(row.id!)">
              <template #reference>
                <el-button type="danger" link size="small">
                  <el-icon><Delete /></el-icon> 删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑零件' : '新增零件'"
      width="600px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px" label-position="right">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="零件名称" prop="partName">
              <el-input v-model="form.partName" placeholder="例如：STM32F407VET6" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="零件类型" prop="partType">
              <el-select v-model="form.partType" placeholder="选择类型" style="width: 100%">
                <el-option label="芯片" value="芯片" />
                <el-option label="电阻" value="电阻" />
                <el-option label="电容" value="电容" />
                <el-option label="连接器" value="连接器" />
                <el-option label="PCB板" value="PCB板" />
                <el-option label="传感器" value="传感器" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="批次号" prop="batchNumber">
              <el-input v-model="form.batchNumber" placeholder="例如：B20260630" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生产厂商" prop="manufacturer">
              <el-input v-model="form.manufacturer" placeholder="例如：意法半导体" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="生产日期" prop="productionDate">
          <el-date-picker
            v-model="form.productionDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="规格描述" prop="specification">
          <el-input
            v-model="form.specification"
            type="textarea"
            :rows="3"
            placeholder="填写零件的详细规格参数..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ isEdit ? '保存' : '新增' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Plus, Edit, Timer, VideoPause, VideoPlay, Delete } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { partsApi } from '@/api/parts'
import type { Part } from '@/types'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()

// 列表数据
const tableData = ref<Part[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<Part>({
  partName: '',
  partType: '',
  batchNumber: '',
  manufacturer: '',
  productionDate: '',
  specification: ''
})

const formRules: FormRules = {
  partName: [{ required: true, message: '请输入零件名称', trigger: 'blur' }],
  partType: [{ required: true, message: '请选择零件类型', trigger: 'change' }],
  batchNumber: [{ required: true, message: '请输入批次号', trigger: 'blur' }],
  manufacturer: [{ required: true, message: '请输入生产厂商', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await partsApi.list({
      page: page.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined
    })
    const data = res.data.data
    tableData.value = data.records
    total.value = data.total
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  loadData()
}

function openAddDialog() {
  isEdit.value = false
  Object.assign(form, {
    id: undefined, partId: undefined, partName: '', partType: '',
    batchNumber: '', manufacturer: '', productionDate: '', specification: ''
  })
  dialogVisible.value = true
}

function openEditDialog(row: Part) {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value && form.id) {
      await partsApi.update(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await partsApi.add(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {
    // handled
  } finally {
    submitLoading.value = false
  }
}

async function handleToggleStatus(row: Part) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await partsApi.update(row.id!, { ...row, status: newStatus })
    ElMessage.success(newStatus === 1 ? '已启用' : '已停用')
    loadData()
  } catch {
    // handled
  }
}

async function handleDelete(id: number) {
  try {
    await partsApi.delete(id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // handled
  }
}

function goToTraces(row: Part) {
  router.push(`/admin/parts/${row.id}/traces`)
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.part-manage {
  max-width: 1300px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;

  .page-title { margin-bottom: 0; }
}

.search-bar {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  margin-bottom: 20px;

  :deep(.el-input__wrapper) {
    border-radius: 10px;
  }
}

.table-card {
  padding: 8px;

  .part-id {
    font-family: 'Courier New', monospace;
    font-weight: 600;
    color: #409EFF;
    font-size: 13px;
  }
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  padding: 16px 16px 8px;
}

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
