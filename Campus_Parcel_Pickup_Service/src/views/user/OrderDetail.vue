<template>
  <div class="order-detail" v-loading="loading">
    <div class="header">
      <el-page-header content="订单详情" @back="goBack" />
      <div class="status">
        <el-tag :type="statusType(detail.status)">{{ statusText(detail.status) }}</el-tag>
      </div>
    </div>

    <el-card v-if="detail && detail.id" class="info-card">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单标题">{{ detail.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="金额">￥{{ formatAmount(detail.amount) }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="取件码">{{ detail.pickupCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ formatDate(detail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDate(detail.updateTime) }}</el-descriptions-item>
        <el-descriptions-item label="跑腿员">{{ detail.runnerName || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-empty v-else-if="!loading" description="未找到该订单或无权查看" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref({})

const fetchDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const response = await request.get(`/api/user/orders/${id}`)
    const { code, data, message } = response?.data || {}
    if (code === 200 && data) {
      detail.value = data
    } else {
      detail.value = {}
      ElMessage.error(message || '获取订单详情失败')
    }
  } catch (error) {
    detail.value = {}
    console.error('获取订单详情失败', error)
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
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

const formatAmount = (amount) => {
  if (amount === null || amount === undefined) return '0.00'
  const num = Number(amount)
  if (Number.isNaN(num)) return '0.00'
  return num.toFixed(2)
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.order-detail {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 120px);
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.status {
  margin-left: 16px;
}

.info-card {
  margin-top: 12px;
}
</style>
