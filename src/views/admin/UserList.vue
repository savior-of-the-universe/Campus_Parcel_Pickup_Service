<template>
  <div class="user-list">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户名、手机号或邮箱"
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

    <!-- 用户表格 -->
    <el-table
      :data="userList"
      v-loading="loading"
      stripe
      style="width: 100%"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="getRoleType(row.role)">
            {{ getRoleText(row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleViewDetail(row)">
            查看详情
          </el-button>
          <el-button type="success" link @click="handleChatHistory(row)">
            聊天记录
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

    <!-- 用户详情弹窗 -->
    <el-dialog
      v-model="detailDialog.visible"
      title="用户详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="detailDialog.data" class="user-detail">
        <!-- 基本信息 -->
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">
            {{ detailDialog.data.id }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ detailDialog.data.username }}
          </el-descriptions-item>
          <el-descriptions-item label="昵称">
            {{ detailDialog.data.nickname || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ detailDialog.data.phone || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            {{ detailDialog.data.email || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag :type="getRoleType(detailDialog.data.role)">
              {{ getRoleText(detailDialog.data.role) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">
            {{ formatDateTime(detailDialog.data.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="最后登录">
            {{ formatDateTime(detailDialog.data.lastLoginTime) || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 操作按钮 -->
        <div class="detail-actions">
          <el-button type="success" @click="handleChatHistory(detailDialog.data)">
            <el-icon><ChatDotRound /></el-icon>
            查看聊天记录
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ChatDotRound } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const userList = ref([])

// 搜索表单
const searchForm = reactive({
  keyword: ''
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

// 获取用户列表
const getUserList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    
    if (searchForm.keyword) {
      params.keyword = searchForm.keyword
    }
    
    const response = await axios.get('/api/admin/users', { params })
    
    if (response.data.code === 200) {
      userList.value = response.data.data.records
      pagination.total = response.data.data.total
    } else {
      ElMessage.error(response.data.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 获取用户详情
const getUserDetail = async (userId) => {
  try {
    const response = await axios.get(`/api/admin/users/${userId}`)
    
    if (response.data.code === 200) {
      detailDialog.data = response.data.data
      detailDialog.visible = true
    } else {
      ElMessage.error(response.data.message || '获取用户详情失败')
    }
  } catch (error) {
    console.error('获取用户详情失败:', error)
    ElMessage.error('获取用户详情失败')
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  getUserList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  pagination.page = 1
  getUserList()
}

// 查看详情
const handleViewDetail = (row) => {
  getUserDetail(row.id)
}

// 查看聊天记录
const handleChatHistory = (user) => {
  // 导航到聊天中心，并传递用户信息
  router.push({
    name: 'ChatCenter',
    query: {
      userId: user.id,
      userName: user.nickname || user.username,
      autoLoadHistory: 'true'
    }
  })
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  getUserList()
}

// 当前页改变
const handleCurrentChange = (page) => {
  pagination.page = page
  getUserList()
}

// 获取角色类型
const getRoleType = (role) => {
  const roleMap = {
    'ADMIN': 'danger',
    'USER': 'primary',
    'RUNNER': 'success'
  }
  return roleMap[role] || 'info'
}

// 获取角色文本
const getRoleText = (role) => {
  const roleMap = {
    'ADMIN': '管理员',
    'USER': '用户',
    'RUNNER': '跑腿员'
  }
  return roleMap[role] || role
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
  getUserList()
})
</script>

<style scoped>
.user-list {
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

.user-detail {
  padding: 20px 0;
}

.detail-actions {
  margin-top: 20px;
  text-align: center;
}

.detail-actions .el-button {
  margin: 0 10px;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
}
</style>
