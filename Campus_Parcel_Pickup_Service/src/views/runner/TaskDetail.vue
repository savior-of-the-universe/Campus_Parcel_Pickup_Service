<template>
  <div class="runner-task-detail" v-loading="loading">

    <!-- 顶部 -->
    <div class="header">
      <el-page-header content="任务详情" @back="router.back()" />
      <el-tag v-if="task.status" :type="statusType(task.status)" size="large">
        {{ statusText(task.status) }}
      </el-tag>
    </div>

    <template v-if="task.id">
      <!-- 任务基本信息 -->
      <el-card class="info-card">
        <el-descriptions :column="2" border title="任务信息">
          <el-descriptions-item label="取件码">
            <strong>{{ task.pickupCode }}</strong>
          </el-descriptions-item>
          <el-descriptions-item label="悬赏积分">
            <span class="reward">+{{ task.rewardPoints }} 积分</span>
          </el-descriptions-item>
          <el-descriptions-item label="送达地点">{{ task.deliveryPoint }}</el-descriptions-item>
          <el-descriptions-item label="重量">{{ task.weight ? task.weight + ' kg' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ formatDate(task.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ task.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 跑腿员信息（接单后显示） -->
        <el-descriptions v-if="task.runnerNickname" :column="2" border title="接单信息" class="runner-info">
          <el-descriptions-item label="跑腿员">{{ task.runnerNickname }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ task.runnerPhoneMasked || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 状态时间线 -->
      <el-card class="timeline-card">
        <template #header><span>任务进度</span></template>
        <el-steps direction="vertical" :active="activeStep" finish-status="success">
          <el-step
            v-for="item in task.timeline"
            :key="item.status"
            :title="item.label"
            :description="item.time ? formatDate(item.time) : (item.done ? '' : '等待中...')"
            :status="getStepStatus(item)"
          />
        </el-steps>
      </el-card>

      <!-- 操作按钮区 -->
      <el-card v-if="nextAction" class="action-card">
        <el-button
          :type="nextAction.btnType"
          size="large"
          :loading="updating"
          @click="handleAction"
          style="width: 100%"
        >
          {{ nextAction.label }}
        </el-button>
      </el-card>
    </template>

    <el-empty v-else-if="!loading" description="任务不存在或无权查看" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const updating = ref(false)
const task = ref({})

let pollTimer = null

// 下一步可执行操作映射
const ACTION_MAP = {
  PENDING:    { type: 'accept',  label: '立即接单',    btnType: 'primary',  confirm: '确认接单？接单后请尽快取件。' },
  ACCEPTED:   { type: 'status', toStatus: 'IN_TRANSIT', label: '确认已取件',  btnType: 'warning',  confirm: '确认已取到快递？请确保快递已在手中。' },
  IN_TRANSIT: { type: 'status', toStatus: 'COMPLETED',  label: '确认送达完成', btnType: 'success', confirm: '确认已完成送达？完成后积分将自动到账。' }
}

const nextAction = computed(() => ACTION_MAP[task.value.status] || null)

const activeStep = computed(() => {
  const timeline = task.value.timeline
  if (!Array.isArray(timeline)) return 0
  let last = 0
  timeline.forEach((item, idx) => { if (item.done) last = idx + 1 })
  return last
})

const getStepStatus = (item) => {
  if (task.value.status === 'CANCELLED' && item.status === 'CANCELLED') return 'error'
  if (item.done) return 'finish'
  return 'wait'
}

const fetchDetail = async () => {
  loading.value = true
  try {
    const { data: res } = await request.get(`/api/runner/tasks/${route.params.id}`)
    if (res?.code === 200 && res.data) {
      task.value = res.data
    } else {
      task.value = {}
      ElMessage.error(res?.message || '获取任务详情失败')
    }
  } catch {
    task.value = {}
    ElMessage.error('获取任务详情失败')
  } finally {
    loading.value = false
  }
}

const handleAction = async () => {
  const action = nextAction.value
  if (!action) return
  try {
    await ElMessageBox.confirm(action.confirm, '操作确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  updating.value = true
  try {
    let res
    if (action.type === 'accept') {
      // 专用接单接口（乐观锁）
      const resp = await request.post(`/api/runner/tasks/${task.value.id}/accept`)
      res = resp.data
    } else {
      // 状态流转接口
      const resp = await request.put(`/api/runner/tasks/${task.value.id}/status`, {
        toStatus: action.toStatus
      })
      res = resp.data
    }

    if (res?.code === 200) {
      ElMessage.success(action.type === 'accept' ? '接单成功！' : '状态更新成功')
      task.value = res.data
      // 接单成功后跳转到"进行中"列表
      if (action.type === 'accept') {
        setTimeout(() => router.push({ name: 'RunnerTasks', query: { tab: 'mine' } }), 1200)
        return
      }
      if (!ACTION_MAP[res.data.status]) stopPoll()
    } else {
      ElMessage.error(res?.message || '操作失败')
    }
  } catch {
    ElMessage.error('操作失败，请重试')
  } finally {
    updating.value = false
  }
}

// 进行中状态开启30s轮询
const startPoll = () => {
  if (pollTimer) return
  pollTimer = setInterval(() => {
    if (task.value.status && !ACTION_MAP[task.value.status]) {
      stopPoll()
      return
    }
    fetchDetail()
  }, 30000)
}
const stopPoll = () => { clearInterval(pollTimer); pollTimer = null }

const statusText = (s) => ({ PENDING: '待接单', ACCEPTED: '接单中', IN_TRANSIT: '进行中', COMPLETED: '已完成', CANCELLED: '已取消' }[s] || s)
const statusType = (s) => ({ PENDING: 'warning', ACCEPTED: 'primary', IN_TRANSIT: 'info', COMPLETED: 'success', CANCELLED: 'danger' }[s] || 'info')

const formatDate = (v) => {
  if (!v) return ''
  return new Date(v).toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'
  })
}

onMounted(async () => {
  await fetchDetail()
  startPoll()
})
onUnmounted(stopPoll)
</script>

<style scoped>
.runner-task-detail { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 120px); }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.info-card, .timeline-card, .action-card { margin-bottom: 16px; border-radius: 8px; }
.reward { font-weight: 700; color: #e6a23c; }
.runner-info { margin-top: 16px; }
</style>
