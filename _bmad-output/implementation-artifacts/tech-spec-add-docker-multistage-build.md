---
title: '增加 Docker 多阶段构建支持'
slug: 'add-docker-multistage-build'
created: '2026-03-12T08:00:00'
status: 'ready-for-dev'
stepsCompleted: [1, 2, 3, 4]
---

# Tech-Spec: 增加 Docker 多阶段构建支持

**Created:** 2026-03-12T08:00:00

## Overview

### Problem Statement

项目缺少 Docker 打包支持，无法快速部署到容器化环境。

### Solution

添加 Docker 多阶段构建配置，使用轻量级 Alpine 镜像，集成 Gradle Jib 插件实现一键构建和推送 Docker 镜像。

### Scope

**In Scope:**
- Dockerfile 多阶段构建（构建阶段 + 运行阶段）
- docker-compose.yml 配置文件
- Gradle Jib 插件集成
- 构建脚本和文档

**Out of Scope:**
- 其他服务配置（MySQL、Kafka 等）
- CI/CD 流水线配置

## Context for Development

### Codebase Patterns

**当前构建配置:**
- Gradle 8.5+ 构建工具
- Spring Boot 4.0.3
- JDK 21
- Spring Boot Gradle 插件（内置打包支持）
- 无 Docker 相关配置

**目标架构:**
- 多阶段 Docker 构建（构建阶段 + 运行阶段）
- 轻量级 Alpine 基础镜像（eclipse-temurin:21-jre-alpine）
- Jib 插件简化镜像构建（无需 Dockerfile）
- 分层 JAR 优化（dependencies、spring-boot-loader、application）

**Files to Reference:**

| 文件 | 用途 | 状态 |
| ---- | ---- | ---- |
| `build.gradle.kts` | Gradle 构建配置，需添加 Jib 插件 | ✅ 已分析 |
| `settings.gradle.kts` | 项目配置 | ✅ 已分析 |
| `src/main/resources/application.yml` | 主配置文件 | ✅ 已分析 |
| `src/main/resources/application-prod.yml` | 生产环境配置 | ⚠️ 需更新 |

## Implementation Plan

### Tasks

**Docker 打包任务列表:**

- [ ] Task 1: 添加 Jib Gradle 插件
  - File: `build.gradle.kts`
  - Action: 在 plugins 块添加 `com.google.cloud.tools.jib` 插件，版本 3.4.0
  - Notes: Jib 无需 Dockerfile 即可构建优化的 Docker 镜像

- [ ] Task 2: 配置 Jib 插件
  - File: `build.gradle.kts`
  - Action: 添加 jib 配置块，设置基础镜像为 eclipse-temurin:21-jre-alpine，配置容器化参数
  - Notes: 包含 JVM 参数、端口暴露、镜像标签等

- [ ] Task 3: 创建 Dockerfile（备用方案）
  - File: `Dockerfile`
  - Action: 创建多阶段构建 Dockerfile（构建阶段 + 运行阶段）
  - Notes: 作为 Jib 插件的备用方案，适用于复杂场景

- [ ] Task 4: 创建 .dockerignore 文件
  - File: `.dockerignore`
  - Action: 排除不必要的文件（build/, .git/, *.md 等）
  - Notes: 减小构建上下文大小

- [ ] Task 5: 创建 docker-compose.yml
  - File: `docker-compose.yml`
  - Action: 定义应用服务和网络配置
  - Notes: 简化本地运行和测试

- [ ] Task 6: 更新生产环境配置
  - File: `src/main/resources/application-prod.yml`
  - Action: 添加 Docker 环境特定的配置（端口、日志、健康检查等）
  - Notes: 确保应用适合容器化运行

- [ ] Task 7: 更新 README 文档
  - File: `README.md`
  - Action: 添加 Docker 构建和运行说明
  - Notes: 包含快速开始指南

### Acceptance Criteria

**验收标准 (Given/When/Then 格式):**

**Jib 插件集成:**
- [ ] AC 1: Given 项目需要构建 Docker 镜像，When 执行 `./gradlew jibDockerBuild`，Then 成功构建 Docker 镜像
- [ ] AC 2: Given Jib 插件配置正确，When 查看构建日志，Then 显示使用 eclipse-temurin:21-jre-alpine 基础镜像
- [ ] AC 3: Given 镜像构建完成，When 运行 `docker images`，Then 镜像名称为 `demo:0.0.1-SNAPSHOT`

**多阶段构建:**
- [ ] AC 4: Given 使用 Dockerfile 构建，When 查看镜像层，Then 显示多阶段构建（构建阶段 + 运行阶段）
- [ ] AC 5: Given 镜像构建完成，When 检查镜像大小，Then 小于 200MB（Alpine 优化）

**容器运行:**
- [ ] AC 6: Given Docker 镜像已构建，When 运行 `docker-compose up`，Then 应用成功启动并监听 8080 端口
- [ ] AC 7: Given 应用容器运行中，When 访问 `http://localhost:8080/actuator/health`，Then 返回健康状态 UP
- [ ] AC 8: Given 应用容器运行中，When 访问 `http://localhost:8080/v3/api-docs`，Then 返回 OpenAPI 文档

**配置正确性:**
- [ ] AC 9: Given 容器运行在生产模式，When 查看日志，Then 显示使用 application-prod.yml 配置
- [ ] AC 10: Given 容器停止，When 重启容器，Then 应用正常重启无错误

### Dependencies

**外部依赖:**
- Docker Desktop 或 Docker Engine
- JDK 21（本地构建需要）
- Gradle 8.5+

**技术依赖:**
- Jib Gradle Plugin 3.4.0
- eclipse-temurin:21-jre-alpine（基础镜像）
- Spring Boot Actuator（健康检查）

**任务依赖关系:**
```
Task 1 (Jib 插件) → Task 2 (Jib 配置)
Task 3 (Dockerfile) → Task 4 (.dockerignore)
Task 5 (docker-compose) → Task 6 (application-prod.yml)
Task 7 (README) ← 依赖所有上述任务完成
```

### Testing Strategy

**Docker 镜像测试策略:**

| 测试类型 | 测试命令 | 验证内容 |
| -------- | -------- | -------- |
| 镜像构建 | `./gradlew jibDockerBuild` | 构建成功，无错误 |
| 镜像大小 | `docker images demo` | 镜像大小 < 200MB |
| 容器启动 | `docker-compose up -d` | 容器正常启动 |
| 健康检查 | `curl http://localhost:8080/actuator/health` | 返回 {"status":"UP"} |
| API 测试 | `curl http://localhost:8080/v1/users` | 返回预期 JSON 响应 |
| 日志检查 | `docker-compose logs` | 无 ERROR 级别错误 |

**手动测试步骤:**
1. 执行 `./gradlew clean jibDockerBuild -Djib.to.image=demo:latest`
2. 执行 `docker-compose up -d`
3. 访问 Swagger UI: `http://localhost:8080/swagger-ui.html`
4. 测试创建用户 API
5. 执行 `docker-compose down` 清理

### Notes

**高风险项 (Pre-mortem 分析):**

| 风险 | 可能性 | 影响 | 缓解措施 |
| ---- | ------ | ---- | -------- |
| 镜像过大 | 中 | 中 | 使用 Alpine 基础镜像，排除不必要文件 |
| 容器启动失败 | 中 | 高 | 添加健康检查，配置正确的 JVM 参数 |
| 数据库连接问题 | 高 | 高 | 在 docker-compose 中添加 MySQL 服务（可选） |
| 时区问题 | 中 | 低 | 在 Dockerfile 中设置 TZ=Asia/Shanghai |
| 内存限制 | 中 | 中 | 配置 JVM 堆大小参数 -Xmx512m |

**已知限制:**
- Jib 插件不支持运行时的动态配置更改
- Alpine 镜像使用 musl libc，某些 native 库可能不兼容
- 多阶段构建的 Dockerfile 需要手动维护（如果使用 Jib 则为备用方案）

**未来考虑 (Out of Scope but worth noting):**
- 添加 CI/CD 自动构建和推送流程
- 使用 Distroless 镜像进一步减小体积
- 添加多架构支持（ARM64 for Apple Silicon）
- 集成 Docker Scout 进行镜像安全扫描
