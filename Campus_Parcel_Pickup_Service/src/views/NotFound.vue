<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <h1>页面不存在</h1>
      <p>抱歉，您访问的页面不存在或已被移除</p>
      
      <div class="actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </div>
      
      <div class="suggestions">
        <h3>您可能在寻找：</h3>
        <div class="suggestion-links">
          <template v-if="userStore.isAdmin">
            <el-link href="/admin/users" type="primary">用户管理</el-link>
            <el-link href="/admin/orders" type="primary">订单管理</el-link>
            <el-link href="/admin/chat" type="primary">聊天中心</el-link>
          </template>
          <template v-else>
            <el-link href="/dashboard" type="primary">个人中心</el-link>
            <el-link href="/user/orders" type="primary">我的订单</el-link>
            <el-link href="/user/tasks" type="primary">发布任务</el-link>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 返回首页
const goHome = () => {
  if (userStore.isAdmin) {
    router.push('/admin/users')
  } else {
    router.push('/dashboard')
  }
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.not-found-content {
  text-align: center;
  max-width: 600px;
  padding: 40px;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  opacity: 0.8;
}

.not-found-content h1 {
  font-size: 32px;
  margin-bottom: 15px;
  font-weight: 600;
}

.not-found-content p {
  font-size: 16px;
  margin-bottom: 30px;
  opacity: 0.9;
}

.actions {
  margin-bottom: 40px;
}

.actions .el-button {
  margin: 0 10px;
}

.suggestions {
  background: rgba(255, 255, 255, 0.1);
  padding: 20px;
  border-radius: 10px;
  backdrop-filter: blur(10px);
}

.suggestions h3 {
  margin: 0 0 15px 0;
  font-size: 18px;
  font-weight: 500;
}

.suggestion-links {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.suggestion-links .el-link {
  color: white;
  font-size: 14px;
  padding: 8px 16px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  transition: all 0.3s;
}

.suggestion-links .el-link:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .error-code {
    font-size: 80px;
  }
  
  .not-found-content h1 {
    font-size: 24px;
  }
  
  .suggestion-links {
    flex-direction: column;
    align-items: center;
  }
  
  .suggestion-links .el-link {
    width: 200px;
    text-align: center;
  }
}
</style>
