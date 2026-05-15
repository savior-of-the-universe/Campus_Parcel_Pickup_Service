<template>
  <div class="unauthorized-container">
    <div class="unauthorized-content">
      <el-result
        icon="warning"
        title="权限不足"
        sub-title="抱歉，您没有权限访问该页面"
      >
        <template #extra>
          <el-button type="primary" @click="goBack">返回上一页</el-button>
          <el-button @click="goHome">返回首页</el-button>
        </template>
      </el-result>
      
      <div class="help-info">
        <h3>需要帮助？</h3>
        <p>如果您认为应该有访问权限，请联系系统管理员。</p>
        <p>当前角色：<el-tag :type="getRoleTagType(userStore.userRole)">{{ getRoleText(userStore.userRole) }}</el-tag></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 返回首页
const goHome = () => {
  if (userStore.isAdmin) {
    router.push('/admin/users')
  } else {
    router.push('/dashboard')
  }
}

// 获取角色文本
const getRoleText = (role) => {
  const roleMap = {
    'ADMIN': '管理员',
    'USER': '普通用户',
    'RUNNER': '跑腿员'
  }
  return roleMap[role] || '未知角色'
}

// 获取角色标签类型
const getRoleTagType = (role) => {
  const typeMap = {
    'ADMIN': 'danger',
    'USER': 'primary',
    'RUNNER': 'success'
  }
  return typeMap[role] || 'info'
}
</script>

<style scoped>
.unauthorized-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.unauthorized-content {
  text-align: center;
  max-width: 500px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.help-info {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.help-info h3 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
}

.help-info p {
  margin: 8px 0;
  color: #606266;
  font-size: 14px;
}

:deep(.el-result__icon) {
  font-size: 64px;
}

:deep(.el-result__title) {
  margin: 20px 0 10px 0;
}

:deep(.el-result__subtitle) {
  margin: 0 0 30px 0;
}
</style>
