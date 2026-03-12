# Docker 镜像配置指南

## 镜像源选项

根据网络环境选择合适的镜像源：

### 1. Docker Hub（官方）- 推荐
```
eclipse-temurin:21-jdk-alpine
eclipse-temurin:21-jre-alpine
```
**优点:** 官方维护，更新及时
**缺点:** 国内访问可能较慢或不稳定

### 2. 腾讯云镜像（免费）
```
ccr.ccs.tencentyun.com/library/eclipse-temurin:21-jdk-alpine
ccr.ccs.tencentyun.com/library/eclipse-temurin:21-jre-alpine
```
**优点:** 国内访问快，免费
**缺点:** 同步可能有延迟

### 3. 华为云镜像（免费）
```
swr.cn-east-2.myhuaweicloud.com/library/eclipse-temurin:21-jdk-alpine
swr.cn-east-2.myhuaweicloud.com/library/eclipse-temurin:21-jre-alpine
```
**优点:** 国内访问快，免费
**缺点:** 同步可能有延迟

### 4. 阿里云镜像（需认证）
```
registry.cn-hangzhou.aliyuncs.com/library/eclipse-temurin:21-jdk-alpine
registry.cn-hangzhou.aliyuncs.com/library/eclipse-temurin:21-jre-alpine
```
**优点:** 国内访问快
**缺点:** 需要阿里云账号认证

---

## 配置方法

### 方法一：修改 Dockerfile

编辑 `Dockerfile`，替换 `FROM` 行：

```dockerfile
# 使用 Docker Hub（官方）
FROM eclipse-temurin:21-jdk-alpine AS builder
FROM eclipse-temurin:21-jre-alpine

# 或使用腾讯云
FROM ccr.ccs.tencentyun.com/library/eclipse-temurin:21-jdk-alpine AS builder
FROM ccr.ccs.tencentyun.com/library/eclipse-temurin:21-jre-alpine

# 或使用华为云
FROM swr.cn-east-2.myhuaweicloud.com/library/eclipse-temurin:21-jdk-alpine AS builder
FROM swr.cn-east-2.myhuaweicloud.com/library/eclipse-temurin:21-jre-alpine
```

### 方法二：修改 build.gradle.kts

编辑 `build.gradle.kts`，修改 Jib 配置：

```kotlin
jib {
    from {
        // Docker Hub（官方）
        image = "eclipse-temurin:21-jre-alpine"
        
        // 或腾讯云
        // image = "ccr.ccs.tencentyun.com/library/eclipse-temurin:21-jre-alpine"
        
        // 或华为云
        // image = "swr.cn-east-2.myhuaweicloud.com/library/eclipse-temurin:21-jre-alpine"
    }
}
```

### 方法三：配置 Docker 镜像加速器

#### Linux/Mac

编辑 `/etc/docker/daemon.json`（需要 sudo 权限）：

```json
{
  "registry-mirrors": [
    "https://docker.m.daocloud.io",
    "https://docker.1panel.live",
    "https://hub.rat.dev",
    "https://dhub.kubesre.xyz"
  ]
}
```

重启 Docker：
```bash
sudo systemctl restart docker
```

#### Windows (Docker Desktop)

1. 打开 Docker Desktop
2. Settings → Docker Engine
3. 添加镜像加速配置：
```json
{
  "registry-mirrors": [
    "https://docker.m.daocloud.io",
    "https://docker.1panel.live"
  ]
}
```
4. 点击 Apply & Restart

---

## 推荐方案

### 方案优先级：

1. **首选**: 配置 Docker 镜像加速器（一劳永逸）
2. **备选**: 使用腾讯云/华为云镜像（免费，无需认证）
3. **最后**: 使用 Docker Hub 官方镜像

### 快速修复命令：

```bash
# 1. 尝试直接拉取（使用 Docker Hub）
docker pull eclipse-temurin:21-jre-alpine

# 2. 如果失败，使用腾讯云镜像
docker pull ccr.ccs.tencentyun.com/library/eclipse-temurin:21-jre-alpine
docker tag ccr.ccs.tencentyun.com/library/eclipse-temurin:21-jre-alpine eclipse-temurin:21-jre-alpine

# 3. 或使用华为云镜像
docker pull swr.cn-east-2.myhuaweicloud.com/library/eclipse-temurin:21-jre-alpine
docker tag swr.cn-east-2.myhuaweicloud.com/library/eclipse-temurin:21-jre-alpine eclipse-temurin:21-jre-alpine
```

---

## 验证配置

```bash
# 构建镜像
docker build -t demo:latest .

# 查看镜像
docker images demo

# 运行容器
docker run -d -p 8080:8080 --name demo-app demo:latest

# 健康检查
curl http://localhost:8080/actuator/health
```

---

## 常见问题

### Q1: 所有镜像源都失败？
**A:** 检查网络连接，或配置 Docker 镜像加速器。

### Q2: 镜像拉取成功但构建失败？
**A:** 检查 Dockerfile 语法和路径配置。

### Q3: 如何查看当前使用的镜像源？
**A:** 运行 `docker history <镜像名>` 查看。

### Q4: 镜像加速器配置不生效？
**A:** 确保重启了 Docker 服务，并检查配置文件语法。

---

**最后更新:** 2026-03-12
