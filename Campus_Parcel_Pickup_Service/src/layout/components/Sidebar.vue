<template>
  <div class="sidebar">
    <div class="logo">
      <h2>校园代取服务</h2>
    </div>
    
    <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      :collapse="isCollapse"
      :unique-opened="true"
      router
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
    >
      <!-- 管理员菜单 -->
      <template v-if="userStore.isAdmin">
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/orders">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/chat">
          <el-icon><ChatDotRound /></el-icon>
          <span>聊天中心</span>
        </el-menu-item>
        
        <el-sub-menu index="admin-data">
          <template #title>
            <el-icon><DataAnalysis /></el-icon>
            <span>数据看板</span>
          </template>
          <el-menu-item index="/admin/dashboard/statistics">
            <el-icon><TrendCharts /></el-icon>
            <span>统计分析</span>
          </el-menu-item>
          <el-menu-item index="/admin/dashboard/reports">
            <el-icon><Files /></el-icon>
            <span>报表管理</span>
          </el-menu-item>
        </el-sub-menu>
      </template>
      
      <!-- 普通用户菜单 -->
      <template v-if="userStore.isUser || userStore.isRunner">
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
        
        <el-menu-item index="/user/orders">
          <el-icon><Document /></el-icon>
          <span>我的订单</span>
        </el-menu-item>
        
        <el-menu-item index="/user/tasks">
          <el-icon><List /></el-icon>
          <span>发布任务</span>
        </el-menu-item>
        
        <el-menu-item index="/user/chat">
          <el-icon><ChatDotRound /></el-icon>
          <span>在线客服</span>
        </el-menu-item>
        
        <el-menu-item index="/user/profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
      </template>
      
      <!-- 跑腿员专属菜单 -->
      <template v-if="userStore.isRunner">
        <el-menu-item index="/runner/tasks">
          <el-icon><Van /></el-icon>
          <span>任务大厅</span>
        </el-menu-item>
        
        <el-menu-item index="/runner/deliveries">
          <el-icon><Position /></el-icon>
          <span>配送管理</span>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  User,
  Document,
  ChatDotRound,
  DataAnalysis,
  TrendCharts,
  Files,
  House,
  List,
  Van,
  Position
} from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()

// 侧边栏折叠状态
const isCollapse = ref(false)

// 当前激活的菜单项
const activeMenu = computed(() => {
  const { path } = route
  return path
})

// 监听路由变化
watch(route, (to) => {
  console.log('路由变化:', to.path)
})
</script>

<style scoped>
.sidebar {
  height: 100vh;
  background-color: #304156;
  transition: width 0.3s;
  width: 250px;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b2f3a;
  color: white;
  border-bottom: 1px solid #3a3f51;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 250px;
}

/* 自定义滚动条 */
.sidebar-menu::-webkit-scrollbar {
  width: 6px;
}

.sidebar-menu::-webkit-scrollbar-track {
  background: #304156;
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background: #4a5568;
  border-radius: 3px;
}

.sidebar-menu::-webkit-scrollbar-thumb:hover {
  background: #5a6578;
}

/* 菜单项样式 */
:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
}

:deep(.el-menu-item:hover) {
  background-color: #3a3f51 !important;
}

:deep(.el-menu-item.is-active) {
  background-color: #409EFF !important;
}

:deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
}

:deep(.el-sub-menu__title:hover) {
  background-color: #3a3f51 !important;
}

:deep(.el-menu--inline .el-menu-item) {
  background-color: #2b2f3a !important;
  padding-left: 60px !important;
}

:deep(.el-menu--inline .el-menu-item:hover) {
  background-color: #3a3f51 !important;
}

:deep(.el-menu--inline .el-menu-item.is-active) {
  background-color: #409EFF !important;
}

/* 图标样式 */
:deep(.el-icon) {
  margin-right: 8px;
  font-size: 16px;
}

/* 折叠状态样式 */
.sidebar:has(.el-menu--collapse) {
  width: 64px;
}

.logo:has(+ .el-menu--collapse) h2 {
  font-size: 14px;
}
</style>
