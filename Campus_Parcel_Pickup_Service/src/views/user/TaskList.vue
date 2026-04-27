<template>
  <div class="task-page">
    <!-- 发布表单 -->
    <el-card class="form-card" shadow="never">
      <template #header>
        <span class="card-title">发布代取任务</span>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" status-icon>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="取件码" prop="pickupCode">
              <el-input v-model="form.pickupCode" placeholder="请输入取件码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="快递点" prop="deliveryPoint">
              <el-select v-model="form.deliveryPoint" placeholder="请选择快递点" style="width:100%">
                <el-option v-for="item in deliveryOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="重量(kg)" prop="weight">
              <el-input v-model="form.weight" placeholder="可选，数字" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="悬赏积分" prop="rewardPoints">
              <el-input-number v-model="form.rewardPoints" :min="1" :max="999999" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3"
            maxlength="100" show-word-limit placeholder="可选，最多100字" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">发布任务</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 我的发布列表 -->
    <el-card class="list-card" shadow="never">
      <template #header>
        <div class="list-header">
          <span class="card-title">我的发布</span>
          <el-button :icon="RefreshRight" circle size="small" :loading="listLoading" @click="refreshList" />
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="onTabChange">
        <el-tab-pane label="进行中" name="in_progress">
          <template #label>
            <span>进行中
              <el-badge v-if="inProgressTotal > 0" :value="inProgressTotal" type="warning" class="tab-badge" />
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="已完成" name="completed" />
      </el-tabs>

      <div v-loading="listLoading">
        <!-- 卡片列表 -->
        <div v-if="tasks.length > 0" class="task-grid">
          <div v-for="task in tasks" :key="task.id" class="task-card" @click="goDetail(task.id)">
            <div class="task-card-header">
              <el-tag :type="statusType(task.status)" size="small">{{ statusText(task.status) }}</el-tag>
              <span class="task-time">{{ formatDate(task.createTime) }}</span>
            </div>
            <div class="task-card-body">
              <div class="task-info-row">
                <el-icon><Location /></el-icon>
                <span class="task-label">快递点</span>
                <span class="task-value">{{ task.deliveryPoint }}</span>
              </div>
              <div class="task-info-row">
                <el-icon><Medal /></el-icon>
                <span class="task-label">悬赏积分</span>
                <span class="task-value reward">{{ task.rewardPoints }} 分</span>
              </div>
              <div v-if="task.weight" class="task-info-row">
                <el-icon><Box /></el-icon>
                <span class="task-label">重量</span>
                <span class="task-value">{{ formatWeight(task.weight) }} kg</span>
              </div>
              <div v-if="task.runnerNickname" class="task-info-row">
                <el-icon><User /></el-icon>
                <span class="task-label">跑腿员</span>
                <span class="task-value">{{ task.runnerNickname }}
                  <span v-if="task.runnerPhoneMasked" class="phone-masked">{{ task.runnerPhoneMasked }}</span>
                </span>
              </div>
              <div v-if="task.remark" class="task-remark">
                <el-icon><ChatLineRound /></el-icon>
                {{ task.remark }}
              </div>
            </div>
            <div class="task-card-footer">
              <span class="task-code">取件码：{{ task.pickupCode }}</span>
              <el-icon class="arrow-icon"><ArrowRight /></el-icon>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty v-else :description="activeTab === 'in_progress' ? '暂无进行中的任务' : '暂无已完成的任务'" />

        <!-- 分页 -->
        <div v-if="total > 0" class="pagination-wrapper">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :total="total"
            :page-sizes="[5, 10, 20]"
            layout="total, sizes, prev, pager, next"
            @current-change="fetchTasks"
            @size-change="onSizeChange"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  RefreshRight, Location, Medal, Box, User,
  ChatLineRound, ArrowRight
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()

// -------- 发布表单 --------
const submitting = ref(false)
const formRef = ref()
const form = reactive({
  pickupCode: '',
  deliveryPoint: '',
  weight: '',
  rewardPoints: 1,
  remark: ''
})

const deliveryOptions = [
  '菜鸟驿站（南门）',
  '菜鸟驿站（北门）',
  '校内快递中心',
  '丰巢柜'
]

const rules = {
  pickupCode: [{ required: true, message: '请输入取件码', trigger: 'blur' }],
  deliveryPoint: [{ required: true, message: '请选择快递点', trigger: 'change' }],
  rewardPoints: [
    { required: true, message: '请输入悬赏积分', trigger: 'change' },
    { type: 'number', min: 1, message: '积分需大于等于1', trigger: 'change' }
  ],
  remark: [{ min: 0, max: 100, message: '备注最多100字', trigger: 'blur' }]
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const resp = await request.post('/api/task/publish', { ...form })
      const { code, message } = resp?.data || {}
      if (code === 200) {
        ElMessage.success(message || '发布成功')
        handleReset()
        // 刷新进行中列表
        if (activeTab.value !== 'in_progress') {
          activeTab.value = 'in_progress'
        }
        page.value = 1
        fetchTasks()
      } else {
        ElMessage.error(message || '发布失败')
      }
    } catch (error) {
      ElMessage.error(error?.response?.data?.message || '发布失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleReset = () => {
  if (formRef.value) formRef.value.resetFields()
  form.rewardPoints = 1
}

// -------- 任务列表 --------
const listLoading = ref(false)
const activeTab = ref('in_progress')
const tasks = ref([])
const total = ref(0)
const inProgressTotal = ref(0)
const page = ref(1)
const size = ref(10)

const fetchTasks = async () => {
  listLoading.value = true
  try {
    const resp = await request.get('/api/task/my-published', {
      params: { status: activeTab.value, page: page.value, size: size.value }
    })
    const { code, data, message } = resp?.data || {}
    if (code === 200 && data) {
      tasks.value = data.records || []
      total.value = data.total || 0
    } else {
      ElMessage.error(message || '获取任务列表失败')
    }
  } catch (error) {
    console.error('获取任务列表失败', error)
    ElMessage.error('获取任务列表失败')
  } finally {
    listLoading.value = false
  }
}

// 获取进行中数量（用于 Tab badge）
const fetchInProgressCount = async () => {
  try {
    const resp = await request.get('/api/task/my-published', {
      params: { status: 'in_progress', page: 1, size: 1 }
    })
    const { code, data } = resp?.data || {}
    if (code === 200 && data) {
      inProgressTotal.value = data.total || 0
    }
  } catch (_) { /* ignore */ }
}

const refreshList = () => {
  page.value = 1
  fetchTasks()
  if (activeTab.value === 'completed') fetchInProgressCount()
}

const onTabChange = (tab) => {
  activeTab.value = tab
  page.value = 1
  fetchTasks()
}

const onSizeChange = (newSize) => {
  size.value = newSize
  page.value = 1
  fetchTasks()
}

const goDetail = (id) => {
  router.push(`/user/tasks/${id}`)
}

// -------- 工具函数 --------
const formatDate = (value) => {
  if (!value) return '-'
  return new Date(value).toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

const statusText = (status) => {
  const map = { PENDING: '待接单', ACCEPTED: '已接单', IN_TRANSIT: '配送中', COMPLETED: '已完成', CANCELLED: '已取消' }
  return map[status] || status || '-'
}

const statusType = (status) => {
  const map = { PENDING: 'warning', ACCEPTED: 'primary', IN_TRANSIT: 'primary', COMPLETED: 'success', CANCELLED: 'danger' }
  return map[status] || 'info'
}

const formatWeight = (value) => {
  if (value === null || value === undefined) return '-'
  const num = Number(value)
  return Number.isNaN(num) ? value : num.toFixed(2)
}

// -------- 轮询：进行中状态自动刷新（30s） --------
let pollingTimer = null
const startPolling = () => {
  pollingTimer = setInterval(() => {
    if (activeTab.value === 'in_progress') {
      fetchTasks()
    }
  }, 30000)
}

onMounted(() => {
  fetchTasks()
  fetchInProgressCount()
  startPolling()
})

onUnmounted(() => {
  if (pollingTimer) clearInterval(pollingTimer)
})
</script>

<style scoped>
.task-page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-title {
  font-weight: 600;
  font-size: 15px;
}

.form-card,
.list-card {
  border: 1px solid #ebeef5;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tab-badge {
  margin-left: 4px;
}

/* 卡片网格 */
.task-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 12px;
  padding: 8px 0;
}

.task-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 14px 16px;
  cursor: pointer;
  transition: box-shadow 0.2s, border-color 0.2s;
  background: #fff;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.task-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.task-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-time {
  font-size: 12px;
  color: #909399;
}

.task-card-body {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.task-info-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #606266;
}

.task-label {
  color: #909399;
  min-width: 52px;
}

.task-value {
  color: #303133;
  font-weight: 500;
}

.task-value.reward {
  color: #e6a23c;
  font-weight: 600;
}

.phone-masked {
  margin-left: 6px;
  color: #909399;
  font-size: 12px;
}

.task-remark {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  font-size: 12px;
  color: #909399;
  padding-top: 2px;
}

.task-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #f0f0f0;
  padding-top: 8px;
}

.task-code {
  font-size: 12px;
  color: #909399;
  font-family: monospace;
}

.arrow-icon {
  color: #c0c4cc;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
