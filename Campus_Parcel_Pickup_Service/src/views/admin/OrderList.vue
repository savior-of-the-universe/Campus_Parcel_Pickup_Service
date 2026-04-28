<template>
  <div class="order-list">
    <!-- Tab 切换 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="main-tabs">
      <el-tab-pane label="代取任务管理" name="tasks" />
      <el-tab-pane label="历史订单" name="orders" />
    </el-tabs>

    <!-- ===== 代取任务列表 ===== -->
    <template v-if="activeTab === 'tasks'">
      <div class="search-bar">
        <el-form :inline="true" :model="taskSearchForm" class="search-form">
          <el-form-item label="状态">
            <el-select v-model="taskSearchForm.status" placeholder="请选择状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="待接单" value="PENDING" />
              <el-option label="接单中" value="ACCEPTED" />
              <el-option label="途中" value="IN_TRANSIT" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
          </el-form-item>
          <el-form-item label="快递点">
            <el-input
              v-model="taskSearchForm.keyword"
              placeholder="快递点关键字"
              clearable
              @keyup.enter="handleTaskSearch"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleTaskSearch">搜索</el-button>
            <el-button @click="handleTaskReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="taskList" v-loading="taskLoading" stripe style="width: 100%">
        <el-table-column prop="deliveryPoint" label="快递点" min-width="160" />
        <el-table-column prop="rewardPoints" label="悬赏积分" width="100">
          <template #default="{ row }">
            <span class="points">{{ row.rewardPoints }} 分</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="pickupCode" label="取件码" width="140" />
        <el-table-column prop="runnerNickname" label="跑腿员" width="100" />
        <el-table-column prop="runnerPhoneMasked" label="跑腿员手机" width="130" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewTaskDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="taskPagination.page"
          v-model:page-size="taskPagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="taskPagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleTaskSizeChange"
          @current-change="handleTaskPageChange"
        />
      </div>
    </template>

    <!-- ===== 历史订单列表 ===== -->
    <template v-if="activeTab === 'orders'">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="待接单" value="PENDING" />
              <el-option label="接单中" value="ACCEPTED" />
              <el-option label="途中" value="IN_TRANSIT" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
          </el-form-item>
          <el-form-item label="订单号">
            <el-input v-model="searchForm.orderNo" placeholder="订单号模糊匹配" clearable @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item label="学号">
            <el-input v-model="searchForm.studentId" placeholder="客户学号模糊匹配" clearable @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="searchForm.name" placeholder="跑腿员或客户姓名" clearable @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="orderList" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="customerName" label="发布者" width="120" />
        <el-table-column prop="customerStudentId" label="发布者学号" width="140" />
        <el-table-column prop="customerPhone" label="发布者手机号" width="140" />
        <el-table-column prop="runnerName" label="跑腿员" width="120" />
        <el-table-column prop="runnerStudentId" label="跑腿员学号" width="140" />
        <el-table-column prop="runnerPhone" label="跑腿员手机号" width="140" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </template>

    <!-- 任务详情弹窗 -->
    <el-dialog v-model="taskDetailDialog.visible" title="任务详情" width="580px" :close-on-click-modal="false">
      <div v-if="taskDetailDialog.data" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="快递点">{{ taskDetailDialog.data.deliveryPoint }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(taskDetailDialog.data.status)">{{ getStatusText(taskDetailDialog.data.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="完整取件码">
            <el-text type="primary" tag="b">{{ taskDetailDialog.data.pickupCode || '-' }}</el-text>
          </el-descriptions-item>
          <el-descriptions-item label="悬赏积分">{{ taskDetailDialog.data.rewardPoints }} 分</el-descriptions-item>
          <el-descriptions-item label="重量">{{ taskDetailDialog.data.weight ? taskDetailDialog.data.weight + ' kg' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ formatDateTime(taskDetailDialog.data.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="跑腿员">{{ taskDetailDialog.data.runnerNickname || '-' }}</el-descriptions-item>
          <el-descriptions-item label="跑腿员手机">{{ taskDetailDialog.data.runnerPhoneMasked || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ taskDetailDialog.data.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
        <div class="timeline-section" v-if="taskDetailDialog.data.timeline && taskDetailDialog.data.timeline.length > 0">
          <h4>任务时间线</h4>
          <el-steps direction="vertical" :active="taskDetailDialog.data.timeline.filter(t => t.done).length - 1">
            <el-step
              v-for="(item, index) in taskDetailDialog.data.timeline"
              :key="index"
              :title="item.label"
              :description="item.time ? formatDateTime(item.time) : '未完成'"
            />
          </el-steps>
        </div>
      </div>
    </el-dialog>

    <!-- 旧订单详情弹窗 -->
    <el-dialog v-model="detailDialog.visible" title="订单详情" width="600px" :close-on-click-modal="false">
      <div v-if="detailDialog.data" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ detailDialog.data.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(detailDialog.data.status)">{{ getStatusText(detailDialog.data.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="取件码">
            <el-text type="primary" size="large" tag="b">{{ detailDialog.data.pickupCode }}</el-text>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(detailDialog.data.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="发布者">{{ detailDialog.data.customerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="发布者学号">{{ detailDialog.data.customerStudentId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="发布者手机号">{{ detailDialog.data.customerPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="跑腿员">{{ detailDialog.data.runnerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="跑腿员学号(脱敏)">{{ detailDialog.data.runnerStudentId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="跑腿员手机号(脱敏)">{{ detailDialog.data.runnerPhone || '-' }}</el-descriptions-item>
        </el-descriptions>
        <div class="timeline-section" v-if="detailDialog.data.timeline && detailDialog.data.timeline.length > 0">
          <h4>订单时间轴</h4>
          <el-steps direction="vertical" :active="detailDialog.data.timeline.length - 1">
            <el-step
              v-for="(event, index) in detailDialog.data.timeline"
              :key="index"
              :title="event.event"
              :description="formatTimelineDesc(event)"
              :timestamp="formatDateTime(event.timestamp)"
            />
          </el-steps>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// ===== Tab 状态 =====
const activeTab = ref('tasks')

// ===== 任务相关 =====
const taskLoading = ref(false)
const taskList = ref([])
const taskSearchForm = reactive({ status: '', keyword: '' })
const taskPagination = reactive({ page: 1, size: 20, total: 0 })
const taskDetailDialog = reactive({ visible: false, data: null })

const getTaskList = async () => {
  taskLoading.value = true
  try {
    const params = { page: taskPagination.page, size: taskPagination.size }
    if (taskSearchForm.status) params.status = taskSearchForm.status
    if (taskSearchForm.keyword) params.keyword = taskSearchForm.keyword
    const response = await request.get('/api/cs/orders/tasks', { params })
    if (response.data.code === 200) {
      const data = response.data.data
      taskList.value = data.records || data.list || []
      taskPagination.total = data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取任务列表失败')
    }
  } catch (error) {
    console.error('获取任务列表失败:', error)
    ElMessage.error('获取任务列表失败')
  } finally {
    taskLoading.value = false
  }
}

const getTaskDetail = async (id) => {
  try {
    const response = await request.get(`/api/cs/orders/tasks/${id}`)
    if (response.data.code === 200) {
      taskDetailDialog.data = response.data.data
      taskDetailDialog.visible = true
    } else {
      ElMessage.error(response.data.message || '获取任务详情失败')
    }
  } catch (error) {
    ElMessage.error('获取任务详情失败')
  }
}

const handleTaskSearch = () => { taskPagination.page = 1; getTaskList() }
const handleTaskReset = () => { taskSearchForm.status = ''; taskSearchForm.keyword = ''; taskPagination.page = 1; getTaskList() }
const handleViewTaskDetail = (row) => getTaskDetail(row.id)
const handleTaskSizeChange = (size) => { taskPagination.size = size; taskPagination.page = 1; getTaskList() }
const handleTaskPageChange = (page) => { taskPagination.page = page; getTaskList() }

// ===== 旧订单相关 =====
const loading = ref(false)
const orderList = ref([])
const searchForm = reactive({ status: '', name: '', orderNo: '', studentId: '' })
const pagination = reactive({ page: 1, size: 20, total: 0 })
const detailDialog = reactive({ visible: false, data: null })

const getOrderList = async () => {
  loading.value = true
  try {
    const params = { page: pagination.page, size: pagination.size, sort: 'DESC' }
    if (searchForm.status) params.status = searchForm.status
    if (searchForm.orderNo) params.orderNo = searchForm.orderNo
    if (searchForm.studentId) params.studentId = searchForm.studentId
    if (searchForm.name) { params.runnerName = searchForm.name; params.customerName = searchForm.name }
    const response = await request.get('/api/cs/orders', { params })
    if (response.data.code === 200) {
      orderList.value = response.data.data.records
      pagination.total = response.data.data.total
    } else {
      ElMessage.error(response.data.message || '获取订单列表失败')
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const getOrderDetail = async (id) => {
  try {
    const response = await request.get(`/api/cs/orders/${id}`)
    if (response.data.code === 200) {
      detailDialog.data = response.data.data
      detailDialog.visible = true
    } else {
      ElMessage.error(response.data.message || '获取订单详情失败')
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

const handleSearch = () => { pagination.page = 1; getOrderList() }
const handleReset = () => { searchForm.status = ''; searchForm.name = ''; searchForm.orderNo = ''; searchForm.studentId = ''; pagination.page = 1; getOrderList() }
const handleViewDetail = (row) => getOrderDetail(row.id)
const handleSizeChange = (size) => { pagination.size = size; pagination.page = 1; getOrderList() }
const handleCurrentChange = (page) => { pagination.page = page; getOrderList() }

// ===== Tab 切换 =====
const handleTabChange = (tab) => {
  if (tab === 'tasks') getTaskList()
  else getOrderList()
}

// ===== 公共工具函数 =====
const getStatusType = (status) => {
  const map = { PENDING: 'warning', ACCEPTED: 'primary', IN_TRANSIT: 'info', COMPLETED: 'success', CANCELLED: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { PENDING: '待接单', ACCEPTED: '接单中', IN_TRANSIT: '途中', COMPLETED: '已完成', CANCELLED: '已取消' }
  return map[status] || status
}

const roleText = (role) => {
  const map = { CUSTOMER: '用户', RUNNER: '跑腿员', CS: '客服', ADMIN: '管理员', SYSTEM: '系统' }
  return map[role?.toUpperCase()] || role || '系统'
}

const formatTimelineDesc = (event) => {
  const parts = []
  if (event?.role) parts.push(`操作人：${roleText(event.role)}`)
  if (event?.description) parts.push(event.description)
  return parts.join('｜') || '状态更新'
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

onMounted(() => {
  getTaskList()
})
</script>

<style scoped>
.order-list {
  padding: 20px;
}

.main-tabs {
  margin-bottom: 8px;
}

.search-bar {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 4px;
}

.search-form .el-form-item {
  margin-bottom: 0;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.points {
  font-weight: 600;
  color: #e6a23c;
}

.order-detail {
  padding: 20px 0;
}

.timeline-section {
  margin-top: 30px;
}

.timeline-section h4 {
  margin-bottom: 20px;
  color: #303133;
  font-weight: 600;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
}

:deep(.el-step__title) {
  font-size: 14px;
  font-weight: 600;
}

:deep(.el-step__description) {
  font-size: 12px;
  color: #606266;
}
</style>
