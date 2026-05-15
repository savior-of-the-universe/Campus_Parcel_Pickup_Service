# 聊天消息全文搜索功能

## 功能概述

为聊天消息系统添加了全文搜索功能，支持按关键词搜索消息内容，并可按会话ID过滤。

## 实现的功能

### 1. 数据库索引
- 在 `chat_message` 表的 `content` 字段上添加了 MySQL FULLTEXT 索引
- SQL脚本：`sql/add_fulltext_index.sql`

### 2. 搜索接口
- **端点**: `GET /api/admin/chats/search`
- **参数**:
  - `sessionId` (可选): 会话ID，用于限制搜索范围
  - `keyword` (必需): 搜索关键词
  - `page` (可选): 页码，默认为1
  - `size` (可选): 每页大小，默认为20

**示例请求**:
```
GET /api/admin/chats/search?sessionId=abc123&keyword=包裹&page=1&size=10
GET /api/admin/chats/search?keyword=快递&page=1&size=20
```

### 3. 消息历史查询优化
- **端点**: `GET /api/admin/chats/messages`
- **参数**:
  - `sessionId` (必需): 会话ID
  - `page` (可选): 页码，默认为1
  - `size` (可选): 每页大小，默认为20
- **返回**: 分页的消息列表，按发送时间降序排列

## 技术实现

### 1. 数据库层
```sql
-- 添加全文索引
ALTER TABLE `chat_message` ADD FULLTEXT INDEX `ft_content` (`content`);
```

### 2. 服务层
- `ChatSearchService`: 搜索服务接口
- `ChatSearchServiceImpl`: 使用 MyBatis-Plus 的 `apply` 方法执行原生 SQL
- 搜索语法: `MATCH(content) AGAINST(keyword IN NATURAL LANGUAGE MODE)`

### 3. 控制器层
- `ChatSearchController`: 专门处理搜索请求
- `ChatAdminController`: 更新了消息历史查询接口

## 使用说明

### 1. 部署步骤
1. 执行数据库索引脚本：
   ```sql
   -- 在数据库中执行
   SOURCE sql/add_fulltext_index.sql;
   ```

2. 重启应用服务

### 2. API 使用示例

#### 搜索所有会话中的消息
```bash
curl -X GET "http://localhost:8080/api/admin/chats/search?keyword=包裹&page=1&size=10"
```

#### 搜索特定会话中的消息
```bash
curl -X GET "http://localhost:8080/api/admin/chats/search?sessionId=session-123&keyword=快递&page=1&size=10"
```

#### 获取会话消息历史
```bash
curl -X GET "http://localhost:8080/api/admin/chats/messages?sessionId=session-123&page=1&size=20"
```

### 3. 权限控制
- 当前实现仅支持客服端搜索
- 用户端搜索功能可在后续版本中添加

## 注意事项

1. **MySQL 版本**: 确保使用支持 FULLTEXT 索引的 MySQL 版本 (5.6+)
2. **字符集**: 数据库表使用 `utf8mb4` 字符集，支持中文搜索
3. **索引维护**: FULLTEXT 索引需要定期维护，建议设置定时任务
4. **性能**: 大量数据时，考虑添加缓存机制

## 扩展功能建议

1. **用户端搜索**: 添加用户只能搜索自己会话的权限控制
2. **搜索高亮**: 在返回结果中高亮显示匹配的关键词
3. **搜索历史**: 记录用户搜索历史
4. **高级搜索**: 支持日期范围、发送者等更多过滤条件
5. **搜索建议**: 提供搜索关键词自动补全功能
