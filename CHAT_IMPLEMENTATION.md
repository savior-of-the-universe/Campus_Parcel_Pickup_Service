# 聊天功能实现文档

## 后端实现

### 1. 依赖配置

需要在 `pom.xml` 中手动添加 WebSocket 依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

### 2. 核心组件

#### WebSocket 配置 (`WebSocketConfig.java`)
- 启用 STOMP 端点 `/ws/chat`
- 支持 SockJS 降级
- 允许跨域访问
- 配置消息代理

#### 消息控制器 (`ChatController.java`)
- 处理 `@MessageMapping("/chat.send")` 消息
- 使用 `SimpMessagingTemplate` 点对点发送
- 自动生成会话ID
- 保存消息到数据库

#### 会话管理服务 (`ChatSessionService.java`)
- 基于 Redis 管理会话状态
- 支持获取活跃会话列表
- 分页获取历史消息
- 未读消息计数管理

#### 消息实体 (`ChatMessage.java`)
- 支持文本和图片消息
- 包含发送时间、已读状态等字段
- 对应数据库表 `chat_message`

#### 管理端 REST 接口 (`ChatAdminController.java`)
- `GET /api/admin/chats/sessions` - 获取会话列表
- `GET /api/admin/chats/messages` - 分页获取历史消息
- `POST /api/admin/chats/{sessionId}/read` - 标记已读

### 3. 数据库表

执行 `sql/chat_message.sql` 创建聊天消息表：

```sql
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` varchar(64) NOT NULL,
  `sender_id` bigint NOT NULL,
  `receiver_id` bigint NOT NULL,
  `content` text NOT NULL,
  `content_type` varchar(10) NOT NULL DEFAULT 'TEXT',
  `send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_read` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_send_time` (`send_time`)
);
```

### 4. 需要完善的部分

- `UserService` 需要添加 `getUserNameById` 方法
- JWT 拦截器需要从 WebSocket 连接中获取用户信息
- 实际的用户ID获取逻辑需要根据认证系统调整

## 前端实现

### 1. 组件结构

#### ChatCenter.vue (`src/views/admin/ChatCenter.vue`)
- 左右两栏布局
- 左侧：会话列表（头像、昵称、最后消息、未读数）
- 右侧：聊天区域（用户信息、消息列表、输入框）

### 2. 核心功能

#### WebSocket 连接
- 使用 `@stomp/stompjs` 连接 `/ws/chat`
- 订阅 `/user/queue/messages` 接收个人消息
- 自动重连机制
- JWT 认证支持

#### 消息处理
- 实时接收和显示消息
- 支持文本和图片消息
- 消息发送状态管理
- 自动滚动到最新消息

#### 会话管理
- 获取和刷新会话列表
- 未读消息计数显示
- 会话切换和消息加载
- 标记消息已读

#### 历史消息
- 分页加载历史消息
- 上滑加载更多
- 消息时间格式化

#### 图片处理
- 本地图片转 base64
- 图片预览功能
- 图片消息显示

### 3. 依赖安装

```bash
npm install @stomp/stompjs
```

或在 `package.json` 中添加依赖。

### 4. 路由配置

已添加路由：
```javascript
{
  path: '/admin/chat',
  name: 'ChatCenter',
  component: ChatCenter,
  meta: {
    title: '聊天中心',
    requiresAuth: true,
    roles: ['ADMIN']
  }
}
```

## 使用说明

### 1. 后端启动
1. 添加 WebSocket 依赖到 pom.xml
2. 执行数据库脚本创建表
3. 启动 Spring Boot 应用

### 2. 前端访问
1. 安装前端依赖
2. 访问 `/admin/chat` 路由
3. 确保用户已登录且具有 ADMIN 角色

### 3. 功能测试
1. 打开聊天中心页面
2. WebSocket 自动连接
3. 可以发送文本和图片消息
4. 支持查看历史消息
5. 未读消息提示

## 注意事项

1. **WebSocket 连接**：确保后端 WebSocket 端点正确配置
2. **认证**：JWT token 需要在 WebSocket 连接时传递
3. **CORS**：前端和后端跨域配置正确
4. **Redis**：会话管理依赖 Redis，确保 Redis 服务运行
5. **用户信息**：当前用户ID获取逻辑需要根据实际认证系统调整

## 扩展功能

可以进一步实现的功能：
- 消息撤回
- 消息转发
- 文件上传
- 消息搜索
- 在线状态显示
- 消息通知推送
