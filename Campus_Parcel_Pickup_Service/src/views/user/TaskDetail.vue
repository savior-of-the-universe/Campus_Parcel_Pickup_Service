<template>
  <div class="task-detail-page" v-loading="loading">
    <div class="page-header">
      <el-button :icon="ArrowLeft" link @click="router.back()">返回</el-button>
      <span class="page-title">任务详情</span>
    </div>

    <template v-if="task">
      <!-- 基本信息 -->
      <el-card shadow="never" class="detail-card">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
            <el-tag :type="statusType(task.status)">{{ statusText(task.status) }}</el-tag>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="取件码">
            <span class="mono">{{ task.pickupCode }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="快递点">{{ task.deliveryPoint }}</el-descriptions-item>
          <el-descriptions-item label="悬赏积分">
            <span class="reward">{{ task.rewardPoints }} 分</span>
          </el-descriptions-item>
          <el-descriptions-item label="重量">
            {{ task.weight ? formatWeight(task.weight) + ' kg' : '未填写' }}
          </el-descriptions-item>
          <el-descriptions-item label="发布时间" :span="2">
            {{ formatDate(task.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="task.remark" label="备注" :span="2">
            {{ task.remark }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 跑腿员信息 -->
      <el-card v-if="task.runnerNickname" shadow="never" class="detail-card">
        <template #header><span>跑腿员信息</span></template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="昵称">{{ task.runnerNickname }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">
            {{ task.runnerPhoneMasked || '暂无' }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
      <el-card v-else shadow="never" class="detail-card">
        <template #header><span>跑腿员信息</span></template>
        <el-empty description="暂无跑腿员接单" :image-size="60" />
      </el-card>

      <!-- 状态时间线 -->
      <el-card shadow="never" class="detail-card">
        <template #header><span>任务进度</span></template>
        <el-steps direction="vertical" :active="activeStep" finish-status="success" class="task-steps">
          <el-step
            v-for="(step, index) in statusSteps"
            :key="step.status"
            :title="step.label"
            :description="index < activeStep ? step.doneDesc : (index === activeStep ? step.activeDesc : '')"
          />
        </el-steps>
      </el-card>
    </template>

    <el-empty v-else-if="!loading" description="任务不存在或无权查看">
      <el-button type="primary" @click="router.back()">返回</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const task = ref(null)

const statusSteps = [
  { status: 'PENDING',   label: '待接单',  doneDesc: '任务已发布',   activeDesc: '等待跑腿员接单...' },
  { status: 'ACCEPTED',  label: '已接单',  doneDesc: '已有跑腿员接单', activeDesc: '跑腿员正在前往取件...' },
  { status: 'IN_TRANSIT',label: '配送中',  doneDesc: '取件成功，配送中', activeDesc: '快递正在送来的路上...' },
  { status: 'COMPLETED', label: '已完成',  doneDesc: '任务完成',      activeDesc: '任务已完成' }
]

const statusOrder = statusSteps.map(s => s.status)

const activeStep = computed(() => {
  if (!task.value) return 0
  if (task.value.status === 'CANCELLED') return 0
  const idx = statusOrder.indexOf(task.value.status)
  return idx >= 0 ? idx : 0
})

const fetchDetail = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const resp = await request.get(`/api/task/${id}`)
    const { code, data, message } = resp?.data || {}
    if (code === 200 && data) {
      task.value = data
    } else {
      ElMessage.error(message || '获取任务详情失败')
    }
  } catch (error) {
    console.error('获取任务详情失败', error)
    ElMessage.error('获取任务详情失败')
  } finally {
    loading.value = false
  }
}

const statusText = (status) => {
  const map = { PENDING: '待接单', ACCEPTED: '已接单', IN_TRANSIT: '配送中', COMPLETED: '已完成', CANCELLED: '已取消' }
  return map[status] || status || '-'
}

const statusType = (status) => {
  const map = { PENDING: 'warning', ACCEPTED: 'primary', IN_TRANSIT: 'primary', COMPLETED: 'success', CANCELLED: 'danger' }
  return map[status] || 'info'
}

const formatDate = (value) => {
  if (!value) return '-'
  return new Date(value).toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit', second: '2-digit'
  })
}

const formatWeight = (value) => {
  if (value === null || value === undefined) return '-'
  const num = Number(value)
  return Number.isNaN(num) ? value : num.toFixed(2)
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.task-detail-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.detail-card {
  border: 1px solid #ebeef5;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.mono {
  font-family: monospace;
  font-size: 15px;
  letter-spacing: 1px;
}

.reward {
  color: #e6a23c;
  font-weight: 600;
}

.task-steps {
  padding: 8px 0;
}
</style>
