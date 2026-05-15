<template>
  <div class="dashboard">
    <div class="welcome-card">
      <h1>欢迎回来，{{ userStore.userInfo.name }}！</h1>
      <p>您的角色：{{ getRoleText(userStore.userInfo.role) }}</p>
      <p>学号：{{ userStore.userInfo.studentId }}</p>
      <p>宿舍区域：{{ userStore.userInfo.dormitoryArea || '未设置' }}</p>
    </div>
    
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon">
              <el-icon size="32" color="#409EFF"><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">5</div>
              <div class="stat-label">我的订单</div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon">
              <el-icon size="32" color="#67C23A"><List /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">3</div>
              <div class="stat-label">发布任务</div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon">
              <el-icon size="32" color="#E6A23C"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">2</div>
              <div class="stat-label">未读消息</div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon">
              <el-icon size="32" color="#F56C6C"><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">1</div>
              <div class="stat-label">待处理</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <div class="recent-activities">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>最近活动</span>
            <el-button type="text">查看全部</el-button>
          </div>
        </template>
        
        <el-timeline>
          <el-timeline-item timestamp="2024-01-15 14:30" placement="top">
            <el-card>
              <h4>订单创建</h4>
              <p>您成功创建了一个代取订单</p>
            </el-card>
          </el-timeline-item>
          
          <el-timeline-item timestamp="2024-01-15 10:20" placement="top">
            <el-card>
              <h4>任务完成</h4>
              <p>您的代取任务已完成</p>
            </el-card>
          </el-timeline-item>
          
          <el-timeline-item timestamp="2024-01-14 16:45" placement="top">
            <el-card>
              <h4>客服回复</h4>
              <p>客服回复了您的咨询</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { Document, List, ChatDotRound, Clock } from '@element-plus/icons-vue'

const userStore = useUserStore()

// 获取角色文本
const getRoleText = (role) => {
  const roleMap = {
    'ADMIN': '管理员',
    'USER': '普通用户',
    'RUNNER': '跑腿员'
  }
  return roleMap[role] || '未知角色'
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 10px;
  margin-bottom: 20px;
}

.welcome-card h1 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
}

.welcome-card p {
  margin: 5px 0;
  opacity: 0.9;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  margin-right: 15px;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.recent-activities {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-timeline-item__content) {
  padding-left: 10px;
}

:deep(.el-timeline-item__content h4) {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #303133;
}

:deep(.el-timeline-item__content p) {
  margin: 0;
  font-size: 14px;
  color: #606266;
}
</style>
