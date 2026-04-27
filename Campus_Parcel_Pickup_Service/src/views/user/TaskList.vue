<template>
  <div class="task-page" v-loading="loading">
    <el-card class="form-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>发布代取任务</span>
        </div>
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
              <el-select v-model="form.deliveryPoint" placeholder="请选择快递点">
                <el-option
                  v-for="item in deliveryOptions"
                  :key="item"
                  :label="item"
                  :value="item"
                />
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
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            maxlength="100"
            show-word-limit
            placeholder="可选，最多100字"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">发布任务</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="list-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>我的发布</span>
        </div>
      </template>
      <el-table :data="tasks" stripe>
        <el-table-column prop="pickupCode" label="取件码" width="160" />
        <el-table-column prop="deliveryPoint" label="快递点" width="160" />
        <el-table-column prop="weight" label="重量(kg)" width="120">
          <template #default="{ row }">
            {{ formatWeight(row.weight) }}
          </template>
        </el-table-column>
        <el-table-column prop="rewardPoints" label="悬赏积分" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" min-width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
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
  pickupCode: [
    { required: true, message: '请输入取件码', trigger: 'blur' }
  ],
  deliveryPoint: [
    { required: true, message: '请选择快递点', trigger: 'change' }
  ],
  rewardPoints: [
    { required: true, message: '请输入悬赏积分', trigger: 'change' },
    { type: 'number', min: 1, message: '积分需大于等于1', trigger: 'change' }
  ],
  remark: [
    { min: 0, max: 100, message: '备注最多100字', trigger: 'blur' }
  ]
}

const tasks = ref([])

const fetchTasks = async () => {
  loading.value = true
  try {
    const resp = await request.get('/api/task/my')
    const { code, data, message } = resp?.data || {}
    if (code === 200 && Array.isArray(data)) {
      tasks.value = data
    } else {
      ElMessage.error(message || '获取任务列表失败')
    }
  } catch (error) {
    console.error('获取任务列表失败', error)
    ElMessage.error('获取任务列表失败')
  } finally {
    loading.value = false
  }
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const payload = { ...form }
      const resp = await request.post('/api/task/publish', payload)
      const { code, message } = resp?.data || {}
      if (code === 200) {
        ElMessage.success(message || '发布成功')
        handleReset()
        fetchTasks()
      } else {
        ElMessage.error(message || '发布失败')
      }
    } catch (error) {
      console.error('发布任务失败', error)
      ElMessage.error(error?.response?.data?.message || '发布失败')
    } finally {
      loading.value = false
    }
  })
}

const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  form.rewardPoints = 1
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

const statusText = (status) => {
  const map = {
    PENDING: '待接单',
    ACCEPTED: '已接单',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return map[status] || status || '-'
}

const statusType = (status) => {
  const map = {
    PENDING: 'warning',
    ACCEPTED: 'primary',
    COMPLETED: 'success',
    CANCELLED: 'danger'
  }
  return map[status] || 'info'
}

const formatWeight = (value) => {
  if (value === null || value === undefined || value === '') return '-'
  const num = Number(value)
  if (Number.isNaN(num)) return value
  return num.toFixed(2)
}

onMounted(() => {
  fetchTasks()
})
</script>

<style scoped>
.task-page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.form-card {
  border: 1px solid #ebeef5;
}

.list-card {
  border: 1px solid #ebeef5;
}
</style>
