<template>
  <div class="order-list">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="全部" value="" />
            <el-option label="待接单" value="PENDING" />
            <el-option label="接单中" value="ACCEPTED" />
            <el-option label="途中" value="IN_TRANSIT" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input
            v-model="searchForm.name"
            placeholder="跑腿员或客户姓名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 订单表格 -->
    <el-table
      :data="orderList"
      v-loading="loading"
      stripe
      style="width: 100%"
    >
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="customerName" label="发布者" width="120" />
      <el-table-column prop="runnerName" label="跑腿员" width="120" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleViewDetail(row)">
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
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

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailDialog.visible"
      title="订单详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="detailDialog.data" class="order-detail">
        <!-- 基本信息 -->
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">
            {{ detailDialog.data.orderNo }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(detailDialog.data.status)">
              {{ getStatusText(detailDialog.data.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="取件码">
            <el-text type="primary" size="large" tag="b">
              {{ detailDialog.data.pickupCode }}
            </el-text>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDateTime(detailDialog.data.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="发布者">
            {{ detailDialog.data.customerName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="跑腿员">
            {{ detailDialog.data.runnerName || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 时间轴 -->
        <div class="timeline-section" v-if="detailDialog.data.timeline && detailDialog.data.timeline.length > 0">
          <h4>订单时间轴</h4>
          <el-steps direction="vertical" :active="detailDialog.data.timeline.length - 1">
            <el-step
              v-for="(event, index) in detailDialog.data.timeline"
              :key="index"
              :title="event.event"
              :description="event.description"
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
import axios from 'axios'

// 响应式数据
const loading = ref(false)
const orderList = ref([])

// 搜索表单
const searchForm = reactive({
  status: '',
  name: ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 详情弹窗
const detailDialog = reactive({
  visible: false,
  data: null
})

// 获取订单列表
const getOrderList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    
    if (searchForm.status) {
      params.status = searchForm.status
    }
    
    if (searchForm.name) {
      // 需要判断是跑腿员还是客户姓名，后端会同时搜索两个字段
      params.runnerName = searchForm.name
      params.customerName = searchForm.name
    }
    
    const response = await axios.get('/api/admin/orders', { params })
    
    if (response.data.code === 200) {
      orderList.value = response.data.data.records
      pagination.total = response.data.data.total
    } else {
      ElMessage.error(response.data.message || '获取订单列表失败')
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 获取订单详情
const getOrderDetail = async (id) => {
  try {
    const response = await axios.get(`/api/admin/orders/${id}`)
    
    if (response.data.code === 200) {
      detailDialog.data = response.data.data
      detailDialog.visible = true
    } else {
      ElMessage.error(response.data.message || '获取订单详情失败')
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  getOrderList()
}

// 重置
const handleReset = () => {
  searchForm.status = ''
  searchForm.name = ''
  pagination.page = 1
  getOrderList()
}

// 查看详情
const handleViewDetail = (row) => {
  getOrderDetail(row.id)
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  getOrderList()
}

// 当前页改变
const handleCurrentChange = (page) => {
  pagination.page = page
  getOrderList()
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    'PENDING': 'warning',
    'ACCEPTED': 'primary',
    'IN_TRANSIT': 'info',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待接单',
    'ACCEPTED': '接单中',
    'IN_TRANSIT': '途中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 组件挂载时获取数据
onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
.order-list {
  padding: 20px;
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
