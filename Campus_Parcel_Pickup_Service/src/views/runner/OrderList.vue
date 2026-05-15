<template>
  <div class="runner-order-list">
    <div class="header">
      <h2>我的接单</h2>
      <div class="actions">
        <el-select v-model="filters.statusGroup" placeholder="分组" @change="handleFilterChange">
          <el-option label="进行中" value="mine" />
          <el-option label="已完成" value="completed" />
        </el-select>
        <el-select v-model="filters.sort" placeholder="排序" @change="handleFilterChange">
          <el-option label="按发布时间降序" value="DESC" />
          <el-option label="按发布时间升序" value="ASC" />
        </el-select>
      </div>
    </div>

    <el-table
      v-if="orderList.length > 0"
      :data="orderList"
      v-loading="loading"
      stripe
      class="order-table"
      @row-click="handleRowClick"
    >
      <el-table-column prop="deliveryPoint" label="快递点" min-width="160" />
      <el-table-column prop="rewardPoints" label="悬赏积分" width="100">
        <template #default="{ row }">
          <span class="points">{{ row.rewardPoints }} 分</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="pickupCodeMasked" label="取件码(后四位)" width="140" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click.stop="handleRowClick(row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-else-if="!loading" description="暂无接单记录" class="empty-block" />

    <div class="pagination" v-if="pagination.total > 0">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)
const orderList = ref([])

const filters = reactive({
  statusGroup: 'mine',
  sort: 'DESC'
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      statusGroup: filters.statusGroup
    }

    const response = await request.get('/api/runner/tasks', { params })
    const { code, data, message } = response?.data || {}
    if (code === 200 && data) {
      let records = data.records || data.list || []
      if (filters.sort === 'ASC') {
        records = records.slice().sort((a, b) => new Date(a.createTime) - new Date(b.createTime))
      } else {
        records = records.slice().sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      }
      orderList.value = records
      pagination.total = data.total || records.length
    } else {
      ElMessage.error(message || '获取接单列表失败')
    }
  } catch (error) {
    console.error('获取接单列表失败', error)
    ElMessage.error('获取接单列表失败')
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  pagination.page = 1
  fetchOrders()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchOrders()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchOrders()
}

const handleRowClick = (row) => {
  router.push({ name: 'RunnerTaskDetail', params: { id: row.id } })
}

const statusText = (status) => {
  const map = {
    PENDING: '待接单',
    ACCEPTED: '接单中',
    IN_TRANSIT: '进行中',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return map[status] || status || '-'
}

const statusType = (status) => {
  const map = {
    PENDING: 'warning',
    ACCEPTED: 'primary',
    IN_TRANSIT: 'info',
    COMPLETED: 'success',
    CANCELLED: 'danger'
  }
  return map[status] || 'info'
}

const formatDate = (value) => {
  if (!value) return '-'
  const date = new Date(value)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.runner-order-list {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 120px);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.actions {
  display: flex;
  gap: 12px;
}

.order-table {
  width: 100%;
}

.points {
  font-weight: 600;
  color: #e6a23c;
}

.pagination {
  margin-top: 16px;
  text-align: right;
}

.empty-block {
  margin-top: 40px;
}
</style>
