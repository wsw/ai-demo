# Spring Boot 4.0 Demo 示例项目

基于 Spring Boot 4.0 + Spring Modulith 构建的现代化 Java 示例项目。

## 技术栈

- **Spring Boot 4.0.3** - 核心框架
- **Spring Modulith 2.0.3** - 模块化单体架构
- **Spring Data JPA** - 数据持久层
- **Spring MVC** - REST API
- **SpringDoc OpenAPI 3.0** - API 文档
- **Flyway** - 数据库迁移
- **H2 Database** - 开发测试数据库
- **MySQL** - 生产数据库
- **Lombok** - 代码简化
- **JUnit 5 + Mockito** - 单元测试

## 项目结构

```
src/main/java/com/example/demo/
├── DemoApplication.java          # 应用入口
├── domain/                       # 领域层
│   ├── entity/                   # 实体类
│   │   └── User.java
│   └── repository/               # 仓储接口
│       └── UserRepository.java
├── application/                  # 应用层
│   ├── dto/                      # 数据传输对象
│   │   └── UserDTO.java
│   └── service/                  # 服务层
│       └── UserService.java
├── interfaces/                   # 接口层
│   └── rest/                     # REST 控制器
│       ├── UserController.java
│       └── HealthController.java
└── infrastructure/               # 基础设施层
    ├── config/                   # 配置类
    │   └── OpenApiConfig.java
    └── exception/                # 异常处理
        └── GlobalExceptionHandler.java

src/main/resources/
├── application.yml               # 主配置
├── application-dev.yml         # 开发环境
├── application-demo.yml        # Demo 环境 (H2)
├── application-test.yml        # 测试环境
├── application-prod.yml        # 生产环境
└── db/migration/               # Flyway 迁移脚本
    └── V1__Create_users_table.sql

src/test/
├── java/com/example/demo/
│   └── UserControllerIntegrationTest.java  # 集成测试
└── resources/
```

## 快速开始

### 1. 环境要求

- JDK 21+
- Gradle 8.5+
- (可选) Docker - 用于运行 MySQL

### 2. 运行方式

#### 方式一：使用 H2 内存数据库（最简单，无需 MySQL）

```bash
# 使用 demo profile 启动（使用 H2 内存数据库）
./gradlew bootRun --args='--spring.profiles.active=demo'
```

#### 方式二：使用本地 MySQL

1. 确保 MySQL 正在运行（版本要求 5.7+ 或 8.0+）

2. 修改 `application-dev.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
```

3. 运行应用：

```bash
./gradlew bootRun
# 或
./gradlew bootRun --args='--spring.profiles.active=dev'
```

#### 方式三：使用 Docker Compose 运行 MySQL

```bash
# 启动 MySQL 容器
docker-compose up -d mysql

# 运行应用
./gradlew bootRun
```

### 3. 访问应用

- **REST API**: http://localhost:8080/api/v1/users
- **H2 Console** (demo profile): http://localhost:8080/api/h2-console
  - JDBC URL: `jdbc:h2:mem:demo_db`
  - User: `sa`
  - Password: (空)
- **API 文档 (Swagger UI)**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI 文档**: http://localhost:8080/api/v3/api-docs

## API 端点

### 用户管理 API

| 方法 | 端点 | 描述 |
|------|------|------|
| POST | /api/v1/users | 创建用户 |
| GET | /api/v1/users | 获取所有用户 |
| GET | /api/v1/users/{id} | 根据ID获取用户 |
| GET | /api/v1/users/username/{username} | 根据用户名获取用户 |
| GET | /api/v1/users/by-status/{status} | 根据状态分页获取用户 |
| GET | /api/v1/users/search | 搜索用户 |
| PUT | /api/v1/users/{id} | 更新用户 |
| DELETE | /api/v1/users/{id} | 删除用户 |

### 健康检查 API

| 方法 | 端点 | 描述 |
|------|------|------|
| GET | /api/health | 健康检查 |
| GET | /api/health/info | 应用信息 |
| GET | /api/health/ready | 就绪检查 |

## 测试

### 运行单元测试

```bash
# 运行所有测试
./gradlew test

# 运行特定测试类
./gradlew test --tests UserControllerIntegrationTest

# 生成测试报告
./gradlew test jacocoTestReport
```

### 测试报告位置

- HTML 报告: `build/reports/tests/test/index.html`
- JaCoCo 覆盖率报告: `build/reports/jacoco/test/html/index.html`

## 构建与部署

### 构建项目

```bash
# 清理并构建
./gradlew clean build

# 跳过测试构建
./gradlew clean build -x test
```

### 运行 JAR

```bash
# 使用 demo profile 运行
java -jar -Dspring.profiles.active=demo build/libs/demo-0.0.1-SNAPSHOT.jar

# 使用 dev profile 运行
java -jar -Dspring.profiles.active=dev build/libs/demo-0.0.1-SNAPSHOT.jar
```

### Docker 构建

```bash
# 构建 Docker 镜像
./gradlew bootBuildImage

# 或使用 Dockerfile
docker build -t demo-app .

# 运行容器
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=demo demo-app
```

## 常见问题

### 1. Flyway 迁移失败

**问题**: `Unsupported Database: MySQL 5.5`

**解决方案**:
- 升级 MySQL 到 5.7+ 或 8.0+
- 或使用 demo profile（H2 内存数据库）

### 2. 端口被占用

**问题**: `Port 8080 was already in use`

**解决方案**:
```bash
# 查找占用端口的进程
lsof -i :8080
# 或
netstat -ano | findstr :8080

# 修改 application.yml 中的端口
server:
  port: 8081
```

### 3. 数据库连接失败

**问题**: `Communications link failure`

**解决方案**:
- 检查 MySQL 是否正在运行
- 检查数据库配置（URL、用户名、密码）
- 检查网络连接和防火墙设置

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目主页: [https://github.com/example/demo](https://github.com/example/demo)
- 问题反馈: [https://github.com/example/demo/issues](https://github.com/example/demo/issues)
- 邮箱: demo@example.com

---

**Happy Coding!** 🚀
