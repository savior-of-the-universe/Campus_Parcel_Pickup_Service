<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <Sidebar ref="sidebarRef" :class="{ 'sidebar-collapsed': isCollapse }" />
    
    <!-- 主内容区 -->
    <div class="main-container" :class="{ 'main-expanded': isCollapse }">
      <!-- 顶部导航 -->
      <Header @toggle-sidebar="handleToggleSidebar" />
      
      <!-- 页面内容 -->
      <div class="page-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Sidebar from './components/Sidebar.vue'
import Header from './components/Header.vue'

// 侧边栏折叠状态
const isCollapse = ref(false)

// 处理侧边栏折叠
const handleToggleSidebar = (collapsed) => {
  isCollapse.value = collapsed
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 250px;
  transition: margin-left 0.3s;
  background-color: #f0f2f5;
}

.main-expanded {
  margin-left: 64px;
}

.page-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-container {
    margin-left: 64px;
  }
  
  .main-expanded {
    margin-left: 0;
  }
}

/* 自定义滚动条 */
.page-content::-webkit-scrollbar {
  width: 8px;
}

.page-content::-webkit-scrollbar-track {
  background: #f0f2f5;
}

.page-content::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
}

.page-content::-webkit-scrollbar-thumb:hover {
  background: #a0a4ac;
}
</style>
