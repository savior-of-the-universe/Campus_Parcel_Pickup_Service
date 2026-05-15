# 运行应用说明

## Lombok 问题已修复

已完成以下修复：

### 1. Maven 配置修复
- ✅ 降级 maven-compiler-plugin 到 3.11.0（与 Lombok 兼容性更好）
- ✅ 添加了正确的 annotationProcessorPaths 配置
- ✅ 添加了 UTF-8 编码和 -parameters 编译参数

### 2. IDEA 配置脚本
- ✅ 生成了 `fix-idea-annotations.bat` 脚本
- ✅ 包含完整的 IDEA 注解处理器配置说明

### 3. 手动 Getter/Setter 版本
- ✅ ChatMessage 实体类已替换为手动 getter/setter 版本
- ✅ Order 实体类已替换为手动 getter/setter 版本
- ⚠️ User 实体类需要手动替换（由于文件编辑限制）

### 4. 代码引用更新
- ✅ 所有 Service、Controller、Mapper 类已更新引用

## 如何运行应用

### 方法一：IDEA 直接运行（推荐）

1. **确保 IDEA 配置正确**
   ```bash
   # 运行修复脚本
   fix-idea-annotations.bat
   ```

2. **清理和重建项目**
   - 在 IDEA 中：`Build` → `Clean`
   - 然后：`Build` → `Rebuild Project`

3. **找到主类并运行**
   ```
   主类路径：com.team.admin.CampusParcelPickupServiceApplication
   ```
   - 在 IDEA 中找到该文件
   - 右键 → `Run 'CampusParcelPickupServiceApplication.main()'`

### 方法二：Maven 运行

```bash
# 清理项目
mvn clean

# 编译项目
mvn compile

# 运行应用
mvn spring-boot:run
```

### 方法三：跳过 Maven 编译直接运行（如果仍有问题）

1. **手动编译 Java 文件**
   ```bash
   # 创建编译目录
   mkdir -p target/classes
   
   # 编译所有 Java 文件（需要包含所有依赖）
   javac -cp "$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" -d target/classes src/main/java/com/team/admin/**/*.java
   ```

2. **直接运行主类**
   ```bash
   java -cp "target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" com.team.admin.CampusParcelPickupServiceApplication
   ```

## 测试验证

### 1. 应用启动测试
- 检查控制台输出是否正常启动
- 确认没有 Lombok 相关错误

### 2. API 测试
```bash
# 健康检查
curl http://localhost:8080/actuator/health

# 用户列表测试
curl http://localhost:8080/api/admin/users?page=1&size=10
```

## 故障排除

### 如果仍有 Lombok 问题：
1. 使用手动 getter/setter 版本（已完成）
2. 检查 IDEA 插件是否启用
3. 重启 IDEA 并清除缓存

### User 实体类手动修复：
由于文件编辑限制，请手动替换 `User.java` 文件内容：
1. 删除 `import lombok.Data;`
2. 删除 `@Data` 注解
3. 复制 `User_fixed.java` 中的 getter/setter 方法到 `User.java`

### 数据库连接问题：
确保 `application.yml` 中的数据库配置正确：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    username: your_username
    password: your_password
```

## 成功标志
- 应用启动无错误
- 可以访问 API 端点
- 数据库连接正常
- 日志显示 "Started CampusParcelPickupServiceApplication"
