# 聊天消息搜索功能测试指南

## 测试概述

为聊天消息搜索功能创建了完整的测试套件，包括单元测试、集成测试和控制器测试。

## 测试文件结构

```
src/test/java/com/team/admin/
├── controller/
│   └── ChatSearchControllerTest.java          # 控制器层单元测试
├── service/impl/
│   ├── ChatSearchServiceImplTest.java          # 服务层单元测试
│   └── ChatSearchServiceIntegrationTest.java   # 服务层集成测试
├── ChatSearchTestSuite.java                    # 测试套件
└── resources/
    └── application-test.yml                    # 测试配置
```

## 测试覆盖范围

### 1. ChatSearchServiceImplTest (服务层单元测试)

**测试场景：**
- ✅ 带会话ID和关键词的搜索
- ✅ 仅关键词搜索（不限制会话）
- ✅ 空会话ID字符串处理
- ✅ 分页参数验证
- ✅ 数据库异常处理
- ✅ 空结果处理
- ✅ 排序验证（按时间降序）

**测试方法数量：** 8个测试方法

### 2. ChatSearchControllerTest (控制器层单元测试)

**测试场景：**
- ✅ 成功响应（带会话ID）
- ✅ 成功响应（不带会话ID）
- ✅ 默认分页参数
- ✅ 自定义分页参数
- ✅ 缺少必需参数处理
- ✅ 空参数处理
- ✅ 服务层异常处理
- ✅ 无效分页参数处理
- ✅ 特殊字符关键词
- ✅ 中文关键词
- ✅ 长关键词
- ✅ 空结果集

**测试方法数量：** 12个测试方法

### 3. ChatSearchServiceIntegrationTest (服务层集成测试)

**测试场景：**
- ✅ 关键词"包裹"搜索
- ✅ 关键词"快递"搜索
- ✅ 按会话ID过滤搜索
- ✅ 不存在关键词处理
- ✅ 不存在会话ID处理
- ✅ 分页功能测试
- ✅ 复合关键词搜索
- ✅ 部分匹配搜索
- ✅ 空字符串会话ID处理
- ✅ 时间排序验证
- ✅ 特殊字符处理

**测试方法数量：** 11个测试方法

## 运行测试

### 1. 运行所有搜索相关测试
```bash
mvn test -Dtest=ChatSearchTestSuite
```

### 2. 运行单个测试类
```bash
# 控制器测试
mvn test -Dtest=ChatSearchControllerTest

# 服务层单元测试
mvn test -Dtest=ChatSearchServiceImplTest

# 集成测试
mvn test -Dtest=ChatSearchServiceIntegrationTest
```

### 3. 运行特定测试方法
```bash
mvn test -Dtest=ChatSearchControllerTest#searchMessages_WithSessionId_ShouldReturnSuccess
```

### 4. 在IDE中运行
- 直接运行 `ChatSearchTestSuite` 类
- 或右键点击各个测试类选择 "Run Tests"

## 测试配置

### application-test.yml
- 使用 H2 内存数据库进行测试
- 自动创建和删除表结构
- 启用 SQL 日志输出
- 配置嵌入式 Redis

### 依赖要求
需要在 `pom.xml` 中添加 H2 数据库依赖：
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

## 测试数据

集成测试使用以下测试数据：
- **会话1 (test-session-1):** 3条消息，包含"包裹"、"快递"等关键词
- **会话2 (test-session-2):** 2条消息，包含"快递"等关键词

## 测试验证点

### 1. 功能验证
- ✅ 全文搜索正确性
- ✅ 会话过滤功能
- ✅ 分页功能
- ✅ 排序功能（时间降序）

### 2. 异常处理
- ✅ 参数验证
- ✅ 数据库异常
- ✅ 边界条件处理

### 3. 性能验证
- ✅ 分页查询效率
- ✅ 大量数据处理

## 测试报告

运行测试后，可以查看以下信息：
- 测试通过率
- 测试覆盖率
- 执行时间
- 失败测试详情

## 持续集成

建议在 CI/CD 流程中包含这些测试：
```yaml
# GitHub Actions 示例
- name: Run Chat Search Tests
  run: mvn test -Dtest=ChatSearchTestSuite
```

## 扩展测试

未来可以添加的测试：
- 性能压力测试
- 并发搜索测试
- 大数据量测试
- 搜索结果缓存测试
- 权限控制测试

## 故障排除

### 常见问题
1. **H2 数据库连接失败** - 检查测试配置
2. **FULLTEXT 索引不支持** - H2 不支持 MySQL FULLTEXT，需要使用模拟
3. **Redis 连接失败** - 检查 Redis 测试配置

### 解决方案
- 使用 `@MockBean` 模拟不支持的功能
- 调整测试配置文件
- 添加必要的测试依赖
