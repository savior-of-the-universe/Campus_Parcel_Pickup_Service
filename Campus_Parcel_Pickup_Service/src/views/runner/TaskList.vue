<template>
  <div class="runner-task-list">
    <div class="header">
      <h2>任务大厅</h2>
      <div class="tabs">
        <el-radio-group v-model="activeTab" @change="handleTabChange">
          <el-radio-button label="available">待接单</el-radio-button>
          <el-radio-button label="mine">进行中</el-radio-button>
          <el-radio-button label="completed">已完成</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <div v-loading="loading" class="task-grid">
      <el-card
        v-for="task in taskList"
        :key="task.id"
        class="task-card"
        shadow="hover"
        @click="goDetail(task.id)"
      >
        <div class="card-top">
          <el-tag :type="statusType(task.status)" size="small">{{ statusText(task.status) }}</el-tag>
          <span class="reward">+{{ task.rewardPoints }} 积分</span>
        </div>
        <div class="code">取件码：<strong>{{ task.pickupCode }}</strong></div>
        <div class="point">送达：{{ task.deliveryPoint }}</div>
        <div class="meta">
          <span v-if="task.weight">重量：{{ task.weight }} kg</span>
          <span class="time">{{ formatDate(task.createTime) }}</span>
        </div>
        <div v-if="task.remark" class="remark">备注：{{ task.remark }}</div>
      </el-card>
    </div>

    <el-empty v-if="!loading && taskList.length === 0" description="暂无任务" class="empty" />

    <div class="pagination" v-if="pagination.total > 0">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20]"
        layout="total, sizes, prev, pager, next"
        :total="pagination.total"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)
const taskList = ref([])
const activeTab = ref('available')

const pagination = reactive({ page: 1, size: 10, total: 0 })

const fetchTasks = async () => {
  loading.value = true
  try {
    const { data: res } = await request.get('/api/runner/tasks', {
      params: { statusGroup: activeTab.value, page: pagination.page, size: pagination.size }
    })
    if (res?.code === 200 && res.data) {
      taskList.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error(res?.message || '获取任务列表失败')
    }
  } catch {
    ElMessage.error('获取任务列表失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  pagination.page = 1
  fetchTasks()
}

const handlePageChange = (page) => { pagination.page = page; fetchTasks() }
const handleSizeChange = (size) => { pagination.size = size; pagination.page = 1; fetchTasks() }

const goDetail = (id) => router.push({ name: 'RunnerTaskDetail', params: { id } })

const statusText = (s) => ({ PENDING: '待接单', ACCEPTED: '接单中', IN_TRANSIT: '进行中', COMPLETED: '已完成', CANCELLED: '已取消' }[s] || s)
const statusType = (s) => ({ PENDING: 'warning', ACCEPTED: 'primary', IN_TRANSIT: 'info', COMPLETED: 'success', CANCELLED: 'danger' }[s] || 'info')

const formatDate = (v) => {
  if (!v) return '-'
  return new Date(v).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

onMounted(fetchTasks)
</script>

<style scoped>
.runner-task-list { padding: 20px; background: #fff; min-height: calc(100vh - 120px); }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header h2 { margin: 0; }
.task-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px; }
.task-card { cursor: pointer; border-radius: 8px; }
.task-card:hover { transform: translateY(-2px); transition: transform .2s; }
.card-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.reward { font-weight: 700; color: #e6a23c; font-size: 15px; }
.code { font-size: 15px; margin-bottom: 6px; }
.point { color: #606266; margin-bottom: 6px; font-size: 14px; }
.meta { display: flex; justify-content: space-between; font-size: 12px; color: #909399; margin-top: 4px; }
.remark { font-size: 12px; color: #909399; margin-top: 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.empty { margin-top: 40px; }
.pagination { margin-top: 20px; text-align: right; }
</style>
