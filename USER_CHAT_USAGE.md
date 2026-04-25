# 用户端聊天组件使用说明

## 组件概述

`ChatButton.vue` 是一个用户端聊天组件，提供悬浮按钮和聊天窗口功能，专门用于用户与客服进行单聊。

## 组件特性

### 🎯 核心功能
- **悬浮按钮**: 页面右下角的圆形按钮，带有脉冲动画效果
- **未读提示**: 红色徽章显示未读消息数量
- **聊天窗口**: 点击按钮弹出完整的聊天界面
- **自动会话**: 首次打开自动创建与客服的会话
- **实时通信**: 基于 WebSocket 的实时消息收发

### 🎨 界面设计
- **渐变主题**: 紫色渐变的现代化设计
- **响应式布局**: 支持移动端适配
- **流畅动画**: 按钮脉冲、窗口滑入等动画效果
- **状态显示**: 客服在线状态指示器

## 使用方法

### 1. 基本引入

```vue
<template>
  <div>
    <!-- 你的页面内容 -->
    
    <!-- 引入聊天组件 -->
    <ChatButton />
  </div>
</template>

<script setup>
import ChatButton from '@/views/user/ChatButton.vue'
</script>
```

### 2. 全局引入（推荐）

在 `App.vue` 或主布局组件中引入：

```vue
<template>
  <router-view />
  <ChatButton />
</template>

<script setup>
import ChatButton from '@/views/user/ChatButton.vue'
</script>
```

### 3. 条件显示

可以根据用户登录状态控制显示：

```vue
<template>
  <div>
    <router-view />
    <ChatButton v-if="isLoggedIn" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ChatButton from '@/views/user/ChatButton.vue'

const isLoggedIn = computed(() => {
  return localStorage.getItem('token') !== null
})
</script>
```

## 功能说明

### 📱 悬浮按钮
- **位置**: 固定在页面右下角
- **动画**: 脉冲效果吸引注意力
- **未读徽章**: 显示未读消息数量（99+显示）
- **悬停效果**: 鼠标悬停时放大并增强阴影

### 💬 聊天窗口
- **头部信息**: 显示客服头像、名称和在线状态
- **消息列表**: 支持文本和图片消息
- **输入区域**: 文本输入框和图片上传按钮
- **历史加载**: 上滑加载更多历史消息

### 🔄 自动会话管理
- **自动创建**: 首次打开时自动创建与客服的会话
- **会话保持**: 关闭窗口后重新打开保持同一会话
- **消息同步**: 实时同步客服发送的消息

### 📸 消息类型
- **文本消息**: 支持长文本和表情符号
- **图片消息**: 本地图片转 base64 发送
- **图片预览**: 点击图片可放大查看

## 技术实现

### 🌐 WebSocket 连接
```javascript
// 自动连接到 WebSocket
stompClient = new Client({
  brokerURL: 'ws://localhost:8080/ws/chat',
  connectHeaders: {
    'Authorization': `Bearer ${token}`
  }
})

// 订阅个人消息队列
stompClient.subscribe('/user/queue/messages', handleNewMessage)
```

### 📡 REST 接口调用
- **创建会话**: `POST /api/user/chat/session`
- **获取消息**: `GET /api/user/chat/messages`
- **标记已读**: `POST /api/user/chat/{sessionId}/read`

### 💾 状态管理
- **消息列表**: 本地存储聊天记录
- **未读计数**: 实时更新未读消息数
- **分页加载**: 支持历史消息分页获取

## 样式定制

### 🎨 主要 CSS 变量
```css
:root {
  --chat-primary-color: #667eea;
  --chat-secondary-color: #764ba2;
  --chat-window-width: 380px;
  --chat-window-height: 600px;
}
```

### 📱 响应式断点
```css
@media (max-width: 768px) {
  .chat-window {
    width: calc(100vw - 40px);
    height: 500px;
  }
}
```

## 配置选项

### 🔧 可配置参数
```javascript
// 在组件中可以修改的配置
const config = {
  // WebSocket 地址
  brokerURL: 'ws://localhost:8080/ws/chat',
  
  // 默认客服ID
  defaultAdminId: 1,
  
  // 分页大小
  pageSize: 20,
  
  // 重连次数
  maxReconnectAttempts: 5
}
```

## 注意事项

### ⚠️ 重要提醒
1. **用户认证**: 组件会检查 `localStorage.getItem('token')`
2. **权限控制**: 需要用户登录后才能使用
3. **网络依赖**: 依赖 WebSocket 连接，断网时会自动重连
4. **浏览器兼容**: 需要支持 WebSocket 的现代浏览器

### 🔒 安全考虑
- **JWT 认证**: WebSocket 连接使用 JWT token
- **消息验证**: 后端验证消息发送权限
- **图片限制**: 仅支持图片文件上传

### 🚀 性能优化
- **消息分页**: 避免一次性加载过多历史消息
- **图片压缩**: 建议对上传图片进行压缩
- **连接管理**: 组件销毁时自动断开 WebSocket

## 故障排除

### 🔧 常见问题

**Q: 悬浮按钮不显示？**
A: 检查用户是否已登录，确认 token 存在

**Q: 消息发送失败？**
A: 检查 WebSocket 连接状态，查看网络连接

**Q: 图片无法发送？**
A: 确认图片格式支持，检查文件大小限制

**Q: 未读消息不更新？**
A: 检查 WebSocket 订阅是否成功

### 🐛 调试方法
```javascript
// 开启调试模式
stompClient.debug = (str) => {
  console.log('STOMP Debug:', str)
}

// 查看连接状态
console.log('Connected:', stompClient.connected)
```

## 扩展功能

### 🚀 可选增强
- **消息撤回**: 支持撤回已发送消息
- **文件上传**: 支持文档、视频等文件类型
- **快捷回复**: 预设常用回复语句
- **评价系统**: 聊天结束后服务评价
- **转接功能**: 客服之间的会话转接

### 📊 数据统计
- **聊天记录**: 存储到数据库用于分析
- **用户行为**: 统计用户使用习惯
- **服务质量**: 客服响应时间统计

## 更新日志

### v1.0.0 (当前版本)
- ✅ 基础聊天功能
- ✅ 悬浮按钮设计
- ✅ WebSocket 实时通信
- ✅ 图片消息支持
- ✅ 自动会话创建
- ✅ 响应式设计

### 计划功能
- 🔄 消息撤回
- 📁 文件上传
- ⭐ 服务评价
- 🎨 主题定制
