# 校园代取服务系统 - 认证授权系统设置指南

## 🎯 功能概述

已成功实现完整的用户登录认证和权限管理系统，包含以下功能：

### 后端功能
- ✅ JWT Token 认证机制
- ✅ Spring Security 安全配置
- ✅ 角色权限控制（ADMIN/USER/RUNNER）
- ✅ 密码 BCrypt 加密
- ✅ 接口权限注解保护

### 前端功能
- ✅ Vue 3 + Element Plus 登录页面
- ✅ Pinia 状态管理
- ✅ 路由权限守卫
- ✅ Axios 请求拦截器
- ✅ 动态侧边栏菜单
- ✅ 用户信息显示和退出登录

## 🚀 启动步骤

### 1. 数据库设置
```bash
# 执行 SQL 脚本插入测试用户
mysql -u root -p your_database < insert_admin.sql
```

### 2. 启动后端服务
```bash
cd C:\Users\YH\IdeaProjects\Campus_Parcel_Pickup_Service
mvn spring-boot:run
```
后端将在 `http://localhost:8080` 启动

### 3. 安装前端依赖
```bash
cd C:\Users\YH\IdeaProjects\Campus_Parcel_Pickup_Service
npm install
```

### 4. 启动前端服务
```bash
npm run dev
```
前端将在 `http://localhost:3000` 启动

## 🔐 测试账户

| 角色 | 学号 | 密码 | 权限说明 |
|------|------|------|----------|
| 管理员 | admin | admin123 | 可访问所有管理功能 |
| 普通用户 | user001 | user123 | 可查看个人信息、订单等 |
| 普通用户 | user002 | user123 | 可查看个人信息、订单等 |
| 跑腿员 | runner001 | runner123 | 可接单配送 |

## 🧪 测试流程

### 1. 登录测试
1. 访问 `http://localhost:3000`
2. 使用测试账户登录
3. 验证登录成功后跳转到对应页面

### 2. 权限测试
1. **管理员权限测试**
   - 登录 admin 账户
   - 访问 `/admin/users`、`/admin/orders`、`/admin/chat`
   - 验证所有管理页面可正常访问

2. **普通用户权限测试**
   - 登录 user001 账户
   - 尝试访问管理页面（应被拦截）
   - 访问用户页面（`/dashboard`、`/user/orders`）

3. **未授权访问测试**
   - 清除浏览器缓存和 localStorage
   - 直接访问受保护页面（应重定向到登录页）

### 3. API 接口测试
```bash
# 登录接口测试
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 获取用户信息测试
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 管理员接口测试（需要 ADMIN 角色的 token）
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

## 📁 文件结构

### 后端文件
```
src/main/java/com/team/
├── controller/
│   └── AuthController.java              # 认证控制器
├── dto/
│   └── LoginRequest.java                # 登录请求DTO
├── security/
│   └── JwtAuthenticationFilter.java    # JWT过滤器
├── utils/
│   └── JwtUtils.java                    # JWT工具类
├── config/
│   └── SecurityConfig.java             # 安全配置
└── admin/controller/
    ├── UserAdminController.java        # 用户管理（已加权限注解）
    ├── OrderAdminController.java       # 订单管理（已加权限注解）
    └── ChatAdminController.java        # 聊天管理（已加权限注解）
```

### 前端文件
```
src/
├── views/
│   ├── Login.vue                       # 登录页面
│   ├── Dashboard.vue                  # 用户首页
│   ├── Unauthorized.vue               # 权限不足页面
│   └── NotFound.vue                   # 404页面
├── stores/
│   └── user.js                        # 用户状态管理
├── router/
│   └── index.js                       # 路由配置
├── utils/
│   └── request.js                     # Axios 封装
├── layout/
│   ├── Layout.vue                     # 主布局
│   ├── components/
│   │   ├── Sidebar.vue                # 侧边栏
│   │   └── Header.vue                 # 顶部导航
├── App.vue                            # 根组件
└── main.js                            # 应用入口
```

## 🔧 配置说明

### JWT 配置
- Token 有效期：8小时
- 密钥：在 `JwtUtils.java` 中配置
- 算法：HS256

### 密码加密
- 算法：BCrypt
- 强度：10 rounds

### CORS 配置
- 允许所有来源（开发环境）
- 支持所有 HTTP 方法
- 允许携带凭证

## 🚨 注意事项

1. **生产环境安全**
   - 修改 JWT 密钥为复杂字符串
   - 配置 HTTPS
   - 限制 CORS 来源

2. **数据库安全**
   - 确保密码字段使用 BCrypt 加密
   - 定期更新密码

3. **Token 管理**
   - 当前未实现 Token 黑名单
   - 退出登录仅清除前端存储

4. **权限控制**
   - 后端接口使用 `@PreAuthorize` 注解
   - 前端路由使用守卫控制
   - 菜单根据角色动态显示

## 🐛 常见问题

### 1. 登录失败
- 检查数据库用户表是否有数据
- 确认密码是否正确加密
- 检查用户状态是否为启用

### 2. 权限错误
- 确认用户角色是否正确
- 检查 `@PreAuthorize` 注解配置
- 验证前端路由守卫逻辑

### 3. Token 过期
- Token 有效期为 8 小时
- 过期后需要重新登录
- 可在前端添加 Token 刷新机制

## 🔄 后续优化建议

1. **Token 刷新机制**
2. **权限管理界面**
3. **操作日志记录**
4. **多因素认证**
5. **单点登录（SSO）**

---

**测试完成后，您的系统将具备完整的认证授权功能！** 🎉
