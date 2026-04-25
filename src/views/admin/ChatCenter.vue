<template>
  <div class="chat-center">
    <!-- 左侧会话列表 -->
    <div class="session-list">
      <div class="session-header">
        <h3>会话列表</h3>
        <el-button @click="refreshSessions" :loading="sessionsLoading" circle>
          <el-icon><Refresh /></el-icon>
        </el-button>
      </div>
      
      <div class="sessions-container">
        <div
          v-for="session in sessionList"
          :key="session.sessionId"
          class="session-item"
          :class="{ active: currentSessionId === session.sessionId }"
          @click="selectSession(session)"
        >
          <div class="session-avatar">
            <el-avatar :size="40">{{ session.userName?.charAt(0) || 'U' }}</el-avatar>
            <div v-if="session.unreadCount > 0" class="unread-badge">
              {{ session.unreadCount > 99 ? '99+' : session.unreadCount }}
            </div>
          </div>
          
          <div class="session-info">
            <div class="session-name">{{ session.userName || '未知用户' }}</div>
            <div class="session-last-message">
              {{ session.lastMessage || '暂无消息' }}
            </div>
          </div>
          
          <div class="session-time">
            {{ formatTime(session.lastMessageTime) }}
          </div>
        </div>
        
        <div v-if="sessionList.length === 0" class="empty-sessions">
          <el-empty description="暂无会话" />
        </div>
      </div>
    </div>

    <!-- 右侧聊天区域 -->
    <div class="chat-area">
      <div v-if="!currentSessionId" class="no-session-selected">
        <el-empty description="请选择一个会话开始聊天" />
      </div>
      
      <div v-else class="chat-container">
        <!-- 聊天头部 -->
        <div class="chat-header">
          <div class="chat-user-info">
            <el-avatar :size="36">{{ currentSession?.userName?.charAt(0) || 'U' }}</el-avatar>
            <span class="chat-user-name">{{ currentSession?.userName || '未知用户' }}</span>
          </div>
          <div class="chat-actions">
            <!-- 搜索输入框 -->
            <div class="search-container">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索消息..."
                size="small"
                style="width: 200px"
                @keydown.enter="handleSearch"
                clearable
                @clear="clearSearch"
              >
                <template #append>
                  <el-button @click="handleSearch" :loading="searchLoading">
                    <el-icon><Search /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </div>
            <el-button @click="markAsRead" size="small">标记已读</el-button>
          </div>
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
            :data-message-id="message.id"
          >
            <div class="message-avatar">
              <el-avatar :size="32">{{ isSelfMessage(message) ? '我' : (currentSession?.userName?.charAt(0) || 'U') }}</el-avatar>
            </div>
            
            <div class="message-content">
              <div class="message-info">
                <span class="message-sender">{{ isSelfMessage(message) ? '我' : currentSession?.userName }}</span>
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
    
    <!-- 搜索结果弹窗 -->
    <el-dialog
      v-model="searchResultsVisible"
      title="搜索结果"
      width="600px"
      :before-close="closeSearchResults"
    >
      <div class="search-results">
        <div v-if="searchLoading" class="search-loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>搜索中...</span>
        </div>
        
        <div v-else-if="searchResults.length === 0" class="no-results">
          <el-empty description="未找到相关消息" />
        </div>
        
        <div v-else class="results-list">
          <div
            v-for="result in searchResults"
            :key="result.id"
            class="search-result-item"
            @click="jumpToMessage(result)"
          >
            <div class="result-header">
              <div class="result-session">
                <el-avatar :size="24">{{ getSessionUser(result.sessionId)?.charAt(0) || 'U' }}</el-avatar>
                <span class="session-name">{{ getSessionUser(result.sessionId) || '未知用户' }}</span>
              </div>
              <span class="result-time">{{ formatTime(result.sendTime) }}</span>
            </div>
            
            <div class="result-content">
              <div class="result-message" v-html="highlightKeyword(result.content)"></div>
            </div>
          </div>
          
          <!-- 分页 -->
          <div v-if="searchTotal > searchPageSize" class="search-pagination">
            <el-pagination
              v-model:current-page="searchPage"
              :page-size="searchPageSize"
              :total="searchTotal"
              @current-change="handleSearchPageChange"
              layout="prev, pager, next"
              small
            />
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh, Loading, Picture, Search } from '@element-plus/icons-vue'
import axios from 'axios'
import { Client } from '@stomp/stompjs'

const route = useRoute()
const router = useRouter()

// 响应式数据
const sessionList = ref([])
const currentSessionId = ref('')
const currentSession = ref(null)
const messageList = ref([])
const messageInput = ref('')
const sessionsLoading = ref(false)
const sending = ref(false)
const loadingMore = ref(false)
const messageListRef = ref(null)

// 搜索相关
const searchKeyword = ref('')
const searchLoading = ref(false)
const searchResults = ref([])
const searchResultsVisible = ref(false)
const searchPage = ref(1)
const searchPageSize = ref(10)
const searchTotal = ref(0)

// 自动加载历史消息相关
const autoLoadTargetUserId = ref(null)
const autoLoadTargetUserName = ref('')
const autoLoadHistory = ref(false)

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
    ElMessage.error('请先登录')
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
    console.log('WebSocket 连接成功:', frame)
    reconnectAttempts.value = 0
    
    // 订阅个人消息队列
    stompClient.subscribe('/user/queue/messages', (message) => {
      const chatMessage = JSON.parse(message.body)
      handleNewMessage(chatMessage)
    })
    
    // 获取会话列表
    getSessions()
  }

  stompClient.onStompError = (frame) => {
    console.error('WebSocket 错误:', frame)
    ElMessage.error('聊天连接失败')
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
  } else {
    ElMessage.error('聊天服务连接失败，请刷新页面重试')
  }
}

// 处理新消息
const handleNewMessage = (message) => {
  // 如果是当前会话的消息，添加到消息列表
  if (message.sessionId === currentSessionId.value) {
    messageList.value.push(message)
    scrollToBottom()
    // 自动标记为已读
    markAsRead()
  } else {
    // 否则更新会话列表的未读数
    updateSessionUnreadCount(message.sessionId)
  }
}

// 获取会话列表
const getSessions = async () => {
  try {
    sessionsLoading.value = true
    const response = await axios.get('/api/admin/chats/sessions')
    if (response.data.code === 200) {
      sessionList.value = response.data.data
      
      // 检查是否需要自动加载特定用户的聊天记录
      if (autoLoadHistory.value && autoLoadTargetUserId.value) {
        await handleAutoLoadHistory()
      }
    }
  } catch (error) {
    console.error('获取会话列表失败:', error)
    ElMessage.error('获取会话列表失败')
  } finally {
    sessionsLoading.value = false
  }
}

// 处理自动加载历史消息
const handleAutoLoadHistory = async () => {
  try {
    // 查找目标用户的会话
    let targetSession = sessionList.value.find(session => 
      session.userId === autoLoadTargetUserId.value
    )
    
    if (targetSession) {
      // 如果会话存在，直接选择并加载全部历史消息
      await selectSessionAndLoadAll(targetSession)
    } else {
      // 如果会话不存在，创建新会话
      await createNewSessionAndLoad()
    }
  } catch (error) {
    console.error('自动加载历史消息失败:', error)
    ElMessage.error('加载聊天记录失败')
  }
}

// 选择会话并加载全部历史消息
const selectSessionAndLoadAll = async (session) => {
  currentSessionId.value = session.sessionId
  currentSession.value = session
  messagePagination.page = 1
  messagePagination.hasMore = true
  
  // 先加载第一页消息
  await getMessages()
  
  // 然后继续加载所有历史消息
  await loadAllHistoryMessages()
  
  ElMessage.success(`已加载与 ${autoLoadTargetUserName.value} 的全部聊天记录`)
}

// 创建新会话并加载历史消息
const createNewSessionAndLoad = async () => {
  try {
    // 生成新的会话ID
    const newSessionId = `admin_${getCurrentUserId()}_${autoLoadTargetUserId.value}_${Date.now()}`
    
    // 创建虚拟会话对象
    const newSession = {
      sessionId: newSessionId,
      userId: autoLoadTargetUserId.value,
      userName: autoLoadTargetUserName.value,
      unreadCount: 0,
      lastMessage: null,
      lastMessageTime: null
    }
    
    // 添加到会话列表
    sessionList.value.unshift(newSession)
    
    // 选择新会话并加载历史消息
    await selectSessionAndLoadAll(newSession)
    
  } catch (error) {
    console.error('创建新会话失败:', error)
    ElMessage.error('创建会话失败')
  }
}

// 加载所有历史消息
const loadAllHistoryMessages = async () => {
  let loadAttempts = 0
  const maxLoadAttempts = 50 // 最多加载50页，防止无限循环
  
  while (messagePagination.hasMore && loadAttempts < maxLoadAttempts) {
    loadingMore.value = true
    messagePagination.page++
    
    try {
      const response = await axios.get('/api/admin/chats/messages', {
        params: {
          sessionId: currentSessionId.value,
          page: messagePagination.page,
          size: messagePagination.size
        }
      })
      
      if (response.data.code === 200) {
        const newMessages = response.data.data
        
        if (newMessages.length > 0) {
          // 将新消息插入到列表开头
          messageList.value = [...newMessages.reverse(), ...messageList.value]
        }
        
        // 判断是否还有更多数据
        if (newMessages.length < messagePagination.size) {
          messagePagination.hasMore = false
        }
      }
    } catch (error) {
      console.error('加载历史消息失败:', error)
      break
    } finally {
      loadingMore.value = false
    }
    
    loadAttempts++
    
    // 防止请求过快
    await new Promise(resolve => setTimeout(resolve, 100))
  }
  
  // 重置分页状态
  messagePagination.page = 1
  messagePagination.hasMore = true
}

// 刷新会话列表
const refreshSessions = () => {
  getSessions()
}

// 选择会话
const selectSession = async (session) => {
  if (currentSessionId.value === session.sessionId) return
  
  currentSessionId.value = session.sessionId
  currentSession.value = session
  messagePagination.page = 1
  messagePagination.hasMore = true
  
  await getMessages()
  // 标记为已读
  markAsRead()
}

// 获取消息历史
const getMessages = async (loadMore = false) => {
  try {
    if (!loadMore) {
      messageList.value = []
    }
    
    const response = await axios.get('/api/admin/chats/messages', {
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
    ElMessage.error('获取消息失败')
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
      receiverId: currentSession.value.userId,
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
      receiverId: currentSession.value.userId,
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
    await axios.post(`/api/admin/chats/${currentSessionId.value}/read`)
    
    // 更新会话列表中的未读数
    const session = sessionList.value.find(s => s.sessionId === currentSessionId.value)
    if (session) {
      session.unreadCount = 0
    }
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

// 更新会话未读数
const updateSessionUnreadCount = (sessionId) => {
  const session = sessionList.value.find(s => s.sessionId === sessionId)
  if (session) {
    session.unreadCount = (session.unreadCount || 0) + 1
  }
}

// 判断是否是自己发送的消息
const isSelfMessage = (message) => {
  return message.senderId === getCurrentUserId()
}

// 获取当前用户ID
const getCurrentUserId = () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return userInfo.id || 1 // 临时使用固定值，实际应该从用户信息获取
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

// 搜索相关方法
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  searchLoading.value = true
  searchPage.value = 1
  
  try {
    const response = await axios.get('/api/admin/chats/search', {
      params: {
        keyword: searchKeyword.value.trim(),
        page: searchPage.value,
        size: searchPageSize.value
      }
    })
    
    if (response.data.code === 200) {
      searchResults.value = response.data.data.records || []
      searchTotal.value = response.data.data.total || 0
      searchResultsVisible.value = true
    } else {
      ElMessage.error(response.data.message || '搜索失败')
    }
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    searchLoading.value = false
  }
}

const clearSearch = () => {
  searchKeyword.value = ''
  searchResults.value = []
  searchResultsVisible.value = false
}

const closeSearchResults = () => {
  searchResultsVisible.value = false
}

const handleSearchPageChange = (page) => {
  searchPage.value = page
  handleSearch()
}

const getSessionUser = (sessionId) => {
  const session = sessionList.value.find(s => s.sessionId === sessionId)
  return session?.userName
}

const highlightKeyword = (content) => {
  if (!searchKeyword.value || !content) return content
  
  const keyword = searchKeyword.value.trim()
  const regex = new RegExp(`(${keyword})`, 'gi')
  return content.replace(regex, '<mark>$1</mark>')
}

const jumpToMessage = async (result) => {
  try {
    // 如果不在当前会话，切换到该会话
    if (result.sessionId !== currentSessionId.value) {
      const targetSession = sessionList.value.find(s => s.sessionId === result.sessionId)
      if (targetSession) {
        await selectSession(targetSession)
      } else {
        ElMessage.error('会话不存在')
        return
      }
    }
    
    // 关闭搜索结果弹窗
    searchResultsVisible.value = false
    
    // 等待消息加载完成后，滚动到目标消息
    await nextTick()
    setTimeout(() => {
      const targetElement = document.querySelector(`[data-message-id="${result.id}"]`)
      if (targetElement) {
        targetElement.scrollIntoView({ behavior: 'smooth', block: 'center' })
        
        // 高亮目标消息
        targetElement.classList.add('highlight-message')
        setTimeout(() => {
          targetElement.classList.remove('highlight-message')
        }, 2000)
      } else {
        // 如果消息不在当前列表中，可能需要加载更多历史消息
        loadMoreMessagesUntilFound(result.id)
      }
    }, 100)
    
  } catch (error) {
    console.error('跳转到消息失败:', error)
    ElMessage.error('跳转失败')
  }
}

const loadMoreMessagesUntilFound = async (targetMessageId) => {
  let attempts = 0
  const maxAttempts = 5
  
  while (attempts < maxAttempts && messagePagination.hasMore) {
    await loadMoreMessages()
    await nextTick()
    
    const targetElement = document.querySelector(`[data-message-id="${targetMessageId}"]`)
    if (targetElement) {
      targetElement.scrollIntoView({ behavior: 'smooth', block: 'center' })
      targetElement.classList.add('highlight-message')
      setTimeout(() => {
        targetElement.classList.remove('highlight-message')
      }, 2000)
      return
    }
    
    attempts++
  }
  
  ElMessage.warning('未找到目标消息，可能已被删除')
}

// 生命周期
onMounted(() => {
  // 检查路由参数，设置自动加载历史消息
  const { userId, userName, autoLoadHistory: shouldAutoLoad } = route.query
  
  if (userId && shouldAutoLoad === 'true') {
    autoLoadTargetUserId.value = parseInt(userId)
    autoLoadTargetUserName.value = userName || '未知用户'
    autoLoadHistory.value = true
  }
  
  initWebSocket()
})

onUnmounted(() => {
  if (stompClient) {
    stompClient.deactivate()
  }
})
</script>

<style scoped>
.chat-center {
  display: flex;
  height: 100vh;
  background: #f5f5f5;
}

/* 左侧会话列表 */
.session-list {
  width: 320px;
  background: white;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.session-header {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.session-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.sessions-container {
  flex: 1;
  overflow-y: auto;
}

.session-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  border-bottom: 1px solid #f0f0f0;
}

.session-item:hover {
  background: #f8f9fa;
}

.session-item.active {
  background: #e6f7ff;
  border-left: 3px solid #1890ff;
}

.session-avatar {
  position: relative;
  margin-right: 12px;
}

.unread-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #ff4d4f;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
  line-height: 1;
}

.session-info {
  flex: 1;
  min-width: 0;
}

.session-name {
  font-weight: 500;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.session-last-message {
  font-size: 13px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.session-time {
  font-size: 12px;
  color: #ccc;
  margin-left: 8px;
}

.empty-sessions {
  padding: 40px 20px;
  text-align: center;
}

/* 右侧聊天区域 */
.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.no-session-selected {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
}

.chat-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-user-info {
  display: flex;
  align-items: center;
}

.chat-user-name {
  margin-left: 12px;
  font-weight: 500;
  font-size: 16px;
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
  max-width: 60%;
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
  background: #1890ff;
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

/* 搜索相关样式 */
.search-container {
  margin-right: 12px;
}

.chat-actions {
  display: flex;
  align-items: center;
}

.search-results {
  max-height: 500px;
  overflow-y: auto;
}

.search-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #999;
  gap: 8px;
}

.no-results {
  padding: 40px;
  text-align: center;
}

.results-list {
  max-height: 400px;
  overflow-y: auto;
}

.search-result-item {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.search-result-item:hover {
  background: #f8f9fa;
}

.search-result-item:last-child {
  border-bottom: none;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.result-session {
  display: flex;
  align-items: center;
  gap: 8px;
}

.session-name {
  font-weight: 500;
  font-size: 14px;
}

.result-time {
  font-size: 12px;
  color: #999;
}

.result-content {
  font-size: 14px;
  line-height: 1.4;
}

.result-message {
  word-wrap: break-word;
}

.result-message :deep(mark) {
  background: #ffeb3b;
  padding: 1px 2px;
  border-radius: 2px;
}

.search-pagination {
  display: flex;
  justify-content: center;
  padding: 16px 0 0;
  border-top: 1px solid #f0f0f0;
  margin-top: 16px;
}

/* 高亮消息样式 */
.highlight-message {
  background: #fff3cd !important;
  border: 2px solid #ffc107 !important;
  border-radius: 8px;
  animation: highlight-pulse 2s ease-in-out;
}

@keyframes highlight-pulse {
  0% {
    background: #fff3cd;
    transform: scale(1);
  }
  50% {
    background: #ffeaa7;
    transform: scale(1.02);
  }
  100% {
    background: #fff3cd;
    transform: scale(1);
  }
}
</style>
