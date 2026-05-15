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

    <!-- 待接单筛选栏 -->
    <div v-if="activeTab === 'available'" class="filter-bar">
      <el-input
        v-model="filterDeliveryPoint"
        placeholder="按快递点筛选"
        clearable
        style="width: 200px"
        @clear="doSearch"
        @keyup.enter="doSearch"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-checkbox v-model="sortByPoints" @change="doSearch" style="margin-left: 12px">
        按积分排序
      </el-checkbox>
      <el-button type="primary" plain size="small" style="margin-left: 8px" @click="doSearch">搜索</el-button>
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
        <!-- 待接单显示后四位，进行中/已完成显示完整 -->
        <div class="code">
          取件码：
          <strong v-if="activeTab === 'available'">{{ task.pickupCodeMasked || '****' }}</strong>
          <strong v-else>{{ task.pickupCode }}</strong>
        </div>
        <div class="point">送达：{{ task.deliveryPoint }}</div>
        <div class="meta">
          <span v-if="task.weight">{{ task.weight }} kg</span>
          <span class="time">{{ formatDate(task.createTime) }}</span>
        </div>
        <div v-if="task.remark" class="remark">{{ task.remark }}</div>
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
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const taskList = ref([])
const activeTab = ref('available')
const filterDeliveryPoint = ref('')
const sortByPoints = ref(false)

const pagination = reactive({ page: 1, size: 10, total: 0 })

const fetchTasks = async () => {
  loading.value = true
  try {
    let res
    if (activeTab.value === 'available') {
      // 使用专用待接单列表接口
      const resp = await request.get('/api/runner/tasks/available', {
        params: {
          deliveryPoint: filterDeliveryPoint.value || undefined,
          sortByPoints: sortByPoints.value || undefined,
          page: pagination.page,
          size: pagination.size
        }
      })
      res = resp.data
    } else {
      const resp = await request.get('/api/runner/tasks', {
        params: { statusGroup: activeTab.value, page: pagination.page, size: pagination.size }
      })
      res = resp.data
    }

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

const doSearch = () => {
  pagination.page = 1
  fetchTasks()
}

const handleTabChange = () => {
  pagination.page = 1
  filterDeliveryPoint.value = ''
  sortByPoints.value = false
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

onMounted(() => {
  // 支持接单成功后跳转过来时自动切到"进行中"
  if (route.query.tab) {
    activeTab.value = route.query.tab
  }
  fetchTasks()
})
</script>

<style scoped>
.runner-task-list { padding: 20px; background: #fff; min-height: calc(100vh - 120px); }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.header h2 { margin: 0; }
.filter-bar { display: flex; align-items: center; margin-bottom: 16px; flex-wrap: wrap; gap: 8px; }
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
