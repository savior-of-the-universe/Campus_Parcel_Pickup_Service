# Maven 安装和项目构建指南

## 当前状态
- ✅ Java 21 已安装并可用
- ❌ Maven 未安装或不在系统 PATH 中

## Maven 安装方案

### 方案 1: 下载并安装 Maven (推荐)

#### Windows 安装步骤:
1. **下载 Maven**
   - 访问 [Maven 官网](https://maven.apache.org/download.cgi)
   - 下载最新的 Binary zip archive (例如: `apache-maven-3.9.x-bin.zip`)

2. **解压文件**
   - 将下载的文件解压到合适的位置，例如: `C:\Program Files\Apache\maven`

3. **配置环境变量**
   - 打开系统属性 → 环境变量
   - 添加新的系统变量 `MAVEN_HOME`:
     ```
     变量名: MAVEN_HOME
     变量值: C:\Program Files\Apache\maven\apache-maven-3.9.x
     ```
   - 编辑 `PATH` 变量，添加: `%MAVEN_HOME%\bin`

4. **验证安装**
   ```cmd
   mvn -version
   ```

### 方案 2: 使用包管理器 (Chocolatey)

#### 安装 Chocolatey (如果未安装):
```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

#### 使用 Chocolatey 安装 Maven:
```powershell
choco install maven
```

### 方案 3: 使用包管理器 (Scoop)

#### 安装 Scoop (如果未安装):
```powershell
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
irm get.scoop.sh | iex
```

#### 使用 Scoop 安装 Maven:
```powershell
scoop install maven
```

## 项目构建

### 安装完成后，运行构建命令:
```bash
mvn clean package
```

### 构建选项:
```bash
# 跳过测试
mvn clean package -DskipTests

# 包含测试
mvn clean package

# 清理并编译
mvn clean compile

# 只打包不运行测试
mvn package -DskipTests

# 安装到本地仓库
mvn clean install
```

## 项目依赖检查

确保以下依赖在 `pom.xml` 中正确配置:

### 必需的依赖:
- Spring Boot Starter Web
- Spring Boot Starter Test
- MyBatis-Plus Spring Boot3 Starter
- MySQL Connector
- H2 Database (用于测试)

### 测试相关依赖 (需要手动添加):
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

## 常见问题解决

### 1. Maven 命令无法识别
**问题**: `mvn : 无法将"mvn"识别为 cmdlet、函数或脚本文件`

**解决方案**:
- 检查环境变量 PATH 是否包含 Maven bin 目录
- 重新启动命令行窗口
- 验证 MAVEN_HOME 环境变量设置

### 2. Java 版本兼容性
**当前状态**: Java 21 ✅
**Maven 要求**: Java 8 或更高版本 ✅

### 3. 网络连接问题
如果下载依赖缓慢，可以配置国内镜像:

在 `~/.m2/settings.xml` 中添加:
```xml
<mirrors>
  <mirror>
    <id>aliyun-maven</id>
    <mirrorOf>*</mirrorOf>
    <name>Aliyun Maven Mirror</name>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

## 项目特定说明

### 当前项目结构:
```
Campus_Parcel_Pickup_Service/
├── pom.xml
├── src/
│   ├── main/java/
│   ├── test/java/
│   └── test/resources/
├── sql/
└── README.md
```

### 构建输出:
- 构建成功后，jar 文件将位于: `target/campus-parcel-pickup-service-1.0-SNAPSHOT.jar`

### 运行应用:
```bash
java -jar target/campus-parcel-pickup-service-1.0-SNAPSHOT.jar
```

## 下一步操作

1. **安装 Maven** (选择上述任一方案)
2. **验证安装**: `mvn -version`
3. **运行构建**: `mvn clean package`
4. **运行测试**: `mvn test`
5. **启动应用**: `java -jar target/*.jar`

## 联系支持

如果遇到安装问题，可以:
- 查看 Maven 官方文档
- 检查系统环境变量配置
- 确认 Java 版本兼容性
