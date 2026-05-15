<template>
  <div class="chat-button-container">
    <!-- 悬浮按钮 -->
    <div
      class="chat-float-button"
      :class="{ 'has-unread': unreadCount > 0 }"
      @click="toggleChat"
    >
      <el-icon :size="24">
        <ChatDotRound />
      </el-icon>
      <div v-if="unreadCount > 0" class="unread-badge">
        {{ unreadCount > 99 ? '99+' : unreadCount }}
      </div>
    </div>

    <!-- 聊天窗口 -->
    <div v-if="showChat" class="chat-window">
      <!-- 聊天头部 -->
      <div class="chat-header">
        <div class="chat-info">
          <el-avatar :size="32">客服</el-avatar>
          <div class="chat-title">
            <div class="service-name">在线客服</div>
            <div class="service-status">
              <span class="status-dot online"></span>
              在线
            </div>
          </div>
        </div>
        <el-button @click="toggleChat" circle size="small">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>

      <!-- 消息列表 -->
      <div class="message-list" ref="messageListRef" @scroll="handleScroll">
        <div v-if="loadingMore" class="loading-more">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>加载更多...</span>
        </div>
        
        <div
          v-for="message in messageList"
          :key="message.id"
          class="message-item"
          :class="{ 'is-self': isSelfMessage(message) }"
        >
          <div class="message-avatar">
            <el-avatar :size="32">{{ isSelfMessage(message) ? '我' : '客服' }}</el-avatar>
          </div>
          
          <div class="message-content">
            <div class="message-info">
              <span class="message-sender">{{ isSelfMessage(message) ? '我' : '客服' }}</span>
              <span class="message-time">{{ formatTime(message.sendTime) }}</span>
            </div>
            
            <div class="message-body">
              <div v-if="message.contentType === 'TEXT'" class="text-message">
                {{ message.content }}
              </div>
              <div v-else-if="message.contentType === 'IMAGE'" class="image-message">
                <el-image
                  :src="message.content"
                  :preview-src-list="[message.content]"
                  fit="cover"
                  class="message-image"
                />
              </div>
            </div>
          </div>
        </div>

        <div v-if="messageList.length === 0" class="empty-messages">
          <div class="welcome-message">
            👋 您好！我是在线客服，有什么可以帮助您的吗？
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <div class="input-toolbar">
          <el-upload
            :auto-upload="false"
            :show-file-list="false"
            accept="image/*"
            :on-change="handleImageSelect"
          >
            <el-button size="small" circle>
              <el-icon><Picture /></el-icon>
            </el-button>
          </el-upload>
        </div>
        
        <div class="input-container">
          <el-input
            v-model="messageInput"
            type="textarea"
            :rows="3"
            placeholder="输入消息..."
            @keydown.enter.prevent="sendMessage"
            resize="none"
          />
          <el-button
            type="primary"
            @click="sendMessage"
            :loading="sending"
            :disabled="!messageInput.trim()"
          >
            发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Close, Loading, Picture } from '@element-plus/icons-vue'
import axios from 'axios'
import { Client } from '@stomp/stompjs'

// 响应式数据
const showChat = ref(false)
const messageList = ref([])
const messageInput = ref('')
const sending = ref(false)
const loadingMore = ref(false)
const messageListRef = ref(null)
const unreadCount = ref(0)
const currentSessionId = ref('')

// 分页相关
const messagePagination = reactive({
  page: 1,
  size: 20,
  hasMore: true
})

// WebSocket 客户端
let stompClient = null
const reconnectAttempts = ref(0)
const maxReconnectAttempts = 5

// 初始化 WebSocket 连接
const initWebSocket = () => {
  const token = localStorage.getItem('token')
  if (!token) {
    // 用户未登录，不初始化聊天功能
    return
  }

  stompClient = new Client({
    brokerURL: 'ws://localhost:8080/ws/chat',
    connectHeaders: {
      'Authorization': `Bearer ${token}`
    },
    debug: (str) => {
      console.log('STOMP Debug:', str)
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  })

  stompClient.onConnect = (frame) => {
    console.log('用户端 WebSocket 连接成功:', frame)
    reconnectAttempts.value = 0
    
    // 订阅个人消息队列
    stompClient.subscribe('/user/queue/messages', (message) => {
      const chatMessage = JSON.parse(message.body)
      handleNewMessage(chatMessage)
    })
    
    // 获取或创建会话
    getOrCreateSession()
  }

  stompClient.onStompError = (frame) => {
    console.error('WebSocket 错误:', frame)
  }

  stompClient.onDisconnect = () => {
    console.log('WebSocket 连接断开')
    handleReconnect()
  }

  stompClient.activate()
}

// 处理重连
const handleReconnect = () => {
  if (reconnectAttempts.value < maxReconnectAttempts) {
    reconnectAttempts.value++
    console.log(`尝试重连第 ${reconnectAttempts.value} 次`)
    setTimeout(() => {
      initWebSocket()
    }, 5000)
  }
}

// 处理新消息
const handleNewMessage = (message) => {
  // 如果聊天窗口打开，添加到消息列表
  if (showChat.value) {
    messageList.value.push(message)
    scrollToBottom()
    // 自动标记为已读
    markAsRead()
  } else {
    // 否则增加未读数
    unreadCount.value++
  }
}

// 获取或创建会话
const getOrCreateSession = async () => {
  try {
    const response = await axios.post('/api/user/chat/session')
    if (response.data.code === 200) {
      currentSessionId.value = response.data.data.sessionId
      // 获取历史消息
      getMessages()
    }
  } catch (error) {
    console.error('获取会话失败:', error)
  }
}

// 获取消息历史
const getMessages = async (loadMore = false) => {
  if (!currentSessionId.value) return
  
  try {
    if (!loadMore) {
      messageList.value = []
    }
    
    const response = await axios.get('/api/user/chat/messages', {
      params: {
        sessionId: currentSessionId.value,
        page: messagePagination.page,
        size: messagePagination.size
      }
    })
    
    if (response.data.code === 200) {
      const newMessages = response.data.data
      
      if (loadMore) {
        // 加载更多时，将新消息插入到列表开头
        messageList.value = [...newMessages.reverse(), ...messageList.value]
      } else {
        // 首次加载时，倒序显示（最新消息在底部）
        messageList.value = newMessages.reverse()
        scrollToBottom()
      }
      
      // 判断是否还有更多数据
      if (newMessages.length < messagePagination.size) {
        messagePagination.hasMore = false
      }
    }
  } catch (error) {
    console.error('获取消息失败:', error)
  }
}

// 切换聊天窗口
const toggleChat = () => {
  showChat.value = !showChat.value
  
  if (showChat.value) {
    // 打开聊天窗口时，清空未读数并获取消息
    unreadCount.value = 0
    if (currentSessionId.value) {
      getMessages()
      markAsRead()
    }
  }
}

// 处理滚动加载
const handleScroll = (e) => {
  const { scrollTop } = e.target
  
  // 当滚动到顶部附近时，加载更多历史消息
  if (scrollTop < 50 && messagePagination.hasMore && !loadingMore.value) {
    loadMoreMessages()
  }
}

// 加载更多消息
const loadMoreMessages = async () => {
  if (!messagePagination.hasMore || loadingMore.value) return
  
  loadingMore.value = true
  messagePagination.page++
  
  await getMessages(true)
  
  loadingMore.value = false
}

// 发送消息
const sendMessage = async () => {
  if (!messageInput.value.trim() || !currentSessionId.value || sending.value) return
  
  try {
    sending.value = true
    
    const message = {
      sessionId: currentSessionId.value,
      senderId: getCurrentUserId(),
      receiverId: 1, // 客服ID，实际应该从系统获取
      content: messageInput.value.trim(),
      contentType: 'TEXT',
      timestamp: new Date().toISOString()
    }
    
    // 通过 WebSocket 发送消息
    if (stompClient && stompClient.connected) {
      stompClient.publish({
        destination: '/app/chat.send',
        body: JSON.stringify(message)
      })
    } else {
      ElMessage.error('聊天连接已断开')
      return
    }
    
    messageInput.value = ''
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败')
  } finally {
    sending.value = false
  }
}

// 处理图片选择
const handleImageSelect = (file) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    sendImageMessage(e.target.result)
  }
  reader.readAsDataURL(file.raw)
}

// 发送图片消息
const sendImageMessage = async (imageData) => {
  if (!currentSessionId.value || sending.value) return
  
  try {
    sending.value = true
    
    const message = {
      sessionId: currentSessionId.value,
      senderId: getCurrentUserId(),
      receiverId: 1, // 客服ID
      content: imageData,
      contentType: 'IMAGE',
      timestamp: new Date().toISOString()
    }
    
    // 通过 WebSocket 发送消息
    if (stompClient && stompClient.connected) {
      stompClient.publish({
        destination: '/app/chat.send',
        body: JSON.stringify(message)
      })
    } else {
      ElMessage.error('聊天连接已断开')
      return
    }
  } catch (error) {
    console.error('发送图片失败:', error)
    ElMessage.error('发送图片失败')
  } finally {
    sending.value = false
  }
}

// 标记消息已读
const markAsRead = async () => {
  if (!currentSessionId.value) return
  
  try {
    await axios.post(`/api/user/chat/${currentSessionId.value}/read`)
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

// 判断是否是自己发送的消息
const isSelfMessage = (message) => {
  return message.senderId === getCurrentUserId()
}

// 获取当前用户ID
const getCurrentUserId = () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return userInfo.id || 2 // 临时使用固定值，实际应该从用户信息获取
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  // 今天
  if (diff < 24 * 60 * 60 * 1000 && date.getDate() === now.getDate()) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  
  // 昨天
  const yesterday = new Date(now)
  yesterday.setDate(yesterday.getDate() - 1)
  if (date.getDate() === yesterday.getDate() && 
      date.getMonth() === yesterday.getMonth() && 
      date.getFullYear() === yesterday.getFullYear()) {
    return '昨天'
  }
  
  // 更早
  return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
}

// 生命周期
onMounted(() => {
  initWebSocket()
})

onUnmounted(() => {
  if (stompClient) {
    stompClient.deactivate()
  }
})
</script>

<style scoped>
.chat-button-container {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 9999;
}

/* 悬浮按钮 */
.chat-float-button {
  position: relative;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  animation: pulse 2s infinite;
}

.chat-float-button:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 30px rgba(102, 126, 234, 0.6);
}

.chat-float-button.has-unread {
  animation: bounce 1s infinite;
}

.unread-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #ff4757;
  color: white;
  font-size: 12px;
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 12px;
  min-width: 20px;
  text-align: center;
  line-height: 1;
  border: 2px solid white;
}

/* 聊天窗口 */
.chat-window {
  position: absolute;
  bottom: 80px;
  right: 0;
  width: 380px;
  height: 600px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

.chat-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.chat-info {
  display: flex;
  align-items: center;
}

.chat-title {
  margin-left: 12px;
}

.service-name {
  font-weight: 600;
  font-size: 16px;
}

.service-status {
  display: flex;
  align-items: center;
  font-size: 12px;
  margin-top: 2px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 6px;
}

.status-dot.online {
  background: #2ecc71;
}

/* 消息列表 */
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
  background: #f8f9fa;
}

.loading-more {
  text-align: center;
  padding: 10px;
  color: #999;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.message-item {
  display: flex;
  margin-bottom: 16px;
}

.message-item.is-self {
  flex-direction: row-reverse;
}

.message-item.is-self .message-content {
  align-items: flex-end;
}

.message-avatar {
  margin: 0 8px;
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
}

.message-info {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  font-size: 12px;
  color: #999;
}

.message-item.is-self .message-info {
  flex-direction: row-reverse;
}

.message-sender {
  margin-right: 8px;
}

.message-time {
  font-size: 11px;
}

.message-body {
  background: white;
  border-radius: 8px;
  padding: 8px 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.message-item.is-self .message-body {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.text-message {
  word-wrap: break-word;
  line-height: 1.4;
}

.image-message {
  max-width: 200px;
}

.message-image {
  max-width: 100%;
  border-radius: 4px;
}

.empty-messages {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.welcome-message {
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  font-size: 14px;
  color: #666;
}

/* 输入区域 */
.input-area {
  border-top: 1px solid #e4e7ed;
  background: white;
}

.input-toolbar {
  padding: 8px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.input-container {
  display: flex;
  align-items: flex-end;
  padding: 12px 20px;
  gap: 12px;
}

.input-container :deep(.el-textarea) {
  flex: 1;
}

.input-container :deep(.el-textarea__inner) {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  resize: none;
}

.input-container :deep(.el-button) {
  flex-shrink: 0;
}

/* 动画 */
@keyframes pulse {
  0% {
    box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  }
  50% {
    box-shadow: 0 4px 30px rgba(102, 126, 234, 0.6);
  }
  100% {
    box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  }
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-window {
    width: calc(100vw - 40px);
    right: -10px;
    height: 500px;
  }
  
  .chat-button-container {
    bottom: 20px;
    right: 20px;
  }
  
  .chat-float-button {
    width: 50px;
    height: 50px;
  }
}
</style>
