---
title: '当前脚手架架构分析与文档更新'
slug: 'architecture-analysis-and-docs-update'
created: '2026-03-12T07:00:00'
status: 'ready-for-dev'
stepsCompleted: [1, 2, 3, 4]
---

# Tech-Spec: 当前脚手架架构分析与文档更新

**Created:** 2026-03-12T07:00:00

## Overview

### Problem Statement

当前项目架构已完善但缺乏系统性文档，新开发者难以快速理解整体架构、组件职责和开发规范。

### Solution

深入分析现有代码架构，创建完整的架构设计文档和开发者指南，包括组件职责、依赖关系、开发规范等内容。

### Scope

**In Scope:**
- 架构分析
- 组件职责文档
- 依赖关系图
- 开发规范
- API 文档
- 测试指南

**Out of Scope:**
- 代码重构
- 新功能开发
- 性能优化

## Context for Development

### Codebase Patterns

**当前架构模式:**
- 分层架构：domain → application → interfaces → infrastructure
- Spring Modulith 模块化单体架构（依赖已引入）
- DTO 模式用于数据传输
- Repository 模式用于数据访问
- 全局异常处理
- 领域驱动设计（部分实施）

**代码规范:**
- 使用 Lombok 简化样板代码 (@Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor, @Slf4j, @RequiredArgsConstructor)
- 使用 Builder 模式构建实体和 DTO
- 依赖注入使用 @RequiredArgsConstructor + final 字段
- 事务管理使用 @Transactional(readOnly = true) 类级别 + @Transactional 方法级别覆盖
- 日志使用 Slf4j
- 使用 Record 类型定义错误响应
- 使用枚举管理状态 (UserStatus)

**文件结构规范:**
- Entity: `domain/entity/` - JPA Entity，包含领域行为方法
- Repository: `domain/repository/` - 继承 JpaRepository，支持自定义查询
- Service: `application/service/` - 业务逻辑层，事务管理
- DTO: `application/dto/` - 静态内部类形式 (Request, Response, UpdateRequest)
- Controller: `interfaces/rest/` - 使用 @RestController + @RequestMapping + OpenAPI 注解
- Config: `infrastructure/config/` - Spring 配置类
- Exception: `infrastructure/exception/` - 使用 @RestControllerAdvice 统一处理

**依赖关系:**
```
domain (实体、仓储接口、领域异常)
  ↑
application (DTO、Service)
  ↑
interfaces (REST Controller)
  ↑
infrastructure (配置、异常处理、持久化实现)
```

### Files to Reference

| 文件 | 职责 | 分析状态 |
| ---- | ---- | -------- |
| `DemoApplication.java` | 应用入口，@SpringBootApplication | ✅ 已分析 |
| `User.java` | 用户实体，包含 5 个领域行为方法 | ✅ 已分析 |
| `UserRepository.java` | 用户仓储，继承 JpaRepository，支持自定义查询 | ✅ 已分析 |
| `UserService.java` | 用户业务服务，11 个业务方法，事务管理 | ✅ 已分析 |
| `UserDTO.java` | 用户数据传输对象 (Request/Response/UpdateRequest) | ✅ 已分析 |
| `UserController.java` | 用户 REST 控制器，9 个端点，OpenAPI 文档 | ✅ 已分析 |
| `SecurityConfig.java` | Spring Security 配置，BCrypt 密码加密 | ✅ 已分析 |
| `GlobalExceptionHandler.java` | 全局异常处理，8 个异常处理器 | ✅ 已分析 |
| `build.gradle.kts` | Gradle 构建配置，依赖管理 | ✅ 已分析 |
| `UserControllerIntegrationTest.java` | 集成测试，8 个测试用例全部通过 | ✅ 已分析 |

### Technical Decisions

**已采用的技术决策:**
- JDK 21
- Spring Boot 4.0.3
- Spring Modulith 2.0.3 (模块化单体)
- Flyway 数据库迁移
- H2 (开发) / MySQL (生产)
- Lombok 简化代码
- Jakarta Validation 参数校验
- Spring Security (密码加密)
- SpringDoc OpenAPI 3.0 (API 文档)

## Implementation Plan

### Tasks

**文档创建任务列表:**

- [ ] Task 1: 创建架构设计文档 (ARCHITECTURE.md)
  - File: `docs/ARCHITECTURE.md`
  - Action: 创建完整的架构设计文档，包含分层架构说明、组件职责、依赖关系图
  - Notes: 使用 Mermaid 绘制依赖关系图和序列图

- [ ] Task 2: 创建开发者指南 (DEVELOPER_GUIDE.md)
  - File: `docs/DEVELOPER_GUIDE.md`
  - Action: 创建开发者指南，包含环境搭建、项目结构、开发流程、代码规范
  - Notes: 包含快速上手指南和常见任务示例

- [ ] Task 3: 创建 API 文档说明 (API_GUIDE.md)
  - File: `docs/API_GUIDE.md`
  - Action: 创建 API 使用指南，包含端点说明、认证方式、错误码、示例请求
  - Notes: 补充 Swagger 之外的详细使用说明

- [ ] Task 4: 创建测试指南 (TESTING_GUIDE.md)
  - File: `docs/TESTING_GUIDE.md`
  - Action: 创建测试指南，包含测试策略、测试框架、测试用例编写规范
  - Notes: 包含单元测试、集成测试示例

- [ ] Task 5: 创建数据库设计文档 (DATABASE_DESIGN.md)
  - File: `docs/DATABASE_DESIGN.md`
  - Action: 创建数据库设计文档，包含 ER 图、表结构说明、索引设计
  - Notes: 使用 Mermaid 绘制 ER 图

- [ ] Task 6: 更新 README.md
  - File: `README.md`
  - Action: 更新 README，添加文档导航、快速开始指南、技术栈详细说明
  - Notes: 使 README 成为文档入口

### Acceptance Criteria

**验收标准 (Given/When/Then 格式):**

**架构设计文档:**
- [ ] AC 1: Given 新开发者阅读 ARCHITECTURE.md，When 查看分层架构图，Then 能清晰理解各层职责和依赖关系
- [ ] AC 2: Given 开发者需要修改代码，When 查看组件职责说明，Then 能准确定位需要修改的文件
- [ ] AC 3: Given 开发者查看依赖关系图，When 分析模块间依赖，Then 能理解依赖方向约束

**开发者指南:**
- [ ] AC 4: Given 新开发者首次使用项目，When 按照 DEVELOPER_GUIDE.md 操作，Then 能在 30 分钟内完成环境搭建并运行项目
- [ ] AC 5: Given 开发者需要添加新功能，When 查看开发流程说明，Then 知道从哪层开始编写代码
- [ ] AC 6: Given 开发者查看代码规范，When 编写新代码，Then 能遵循项目的命名和结构规范

**API 文档:**
- [ ] AC 7: Given API 使用者查看 API_GUIDE.md，When 查看端点说明，Then 能理解每个端点的用途和参数
- [ ] AC 8: Given API 调用失败，When 查看错误码说明，Then 能找到对应的错误原因和解决方案
- [ ] AC 9: Given 开发者查看示例请求，When 复制示例代码，Then 能直接运行成功

**测试指南:**
- [ ] AC 10: Given 开发者需要编写测试，When 查看 TESTING_GUIDE.md，Then 知道使用什么测试框架和注解
- [ ] AC 11: Given 开发者编写单元测试，When 参考测试示例，Then 能写出符合项目规范的测试代码
- [ ] AC 12: Given 开发者运行测试，When 执行测试命令，Then 所有测试通过且无警告

**数据库设计文档:**
- [ ] AC 13: Given 开发者查看 DATABASE_DESIGN.md，When 查看 ER 图，Then 能理解表之间的关系
- [ ] AC 14: Given 开发者需要查询数据，When 查看表结构说明，Then 知道字段含义和约束

**README 更新:**
- [ ] AC 15: Given 用户查看 README.md，When 浏览项目介绍，Then 能快速了解项目用途和技术栈
- [ ] AC 16: Given 用户需要深入文档，When 查看文档导航，Then 能找到对应的详细文档

## Additional Context

### Dependencies

**外部依赖:**
- Mermaid - 用于绘制架构图和 ER 图（Markdown 原生支持）
- SpringDoc OpenAPI - 已集成，用于自动生成 API 文档

**内部依赖:**
- Task 6 (README 更新) 依赖 Task 1-5 完成
- 所有文档依赖现有代码分析结果

**技术依赖:**
- JDK 21+
- Gradle 8.5+
- Spring Boot 4.0.3

### Testing Strategy

**文档测试策略:**

| 文档类型 | 验证方式 | 验证内容 |
| -------- | -------- | -------- |
| ARCHITECTURE.md | 人工审查 | 架构图准确性、组件职责清晰度 |
| DEVELOPER_GUIDE.md | 新人验证 | 按步骤操作能否成功搭建环境 |
| API_GUIDE.md | 示例验证 | 示例请求能否成功执行 |
| TESTING_GUIDE.md | 代码审查 | 测试示例能否运行通过 |
| DATABASE_DESIGN.md | 对比验证 | ER 图与实际数据库结构一致性 |
| README.md | 人工审查 | 导航链接有效性、信息准确性 |

**文档更新流程:**
1. 代码变更时同步更新相关文档
2. 每次 Release 前审查文档完整性
3. 新开发者反馈文档问题及时修正

### Notes

**高风险项 (Pre-mortem 分析):**

| 风险 | 可能性 | 影响 | 缓解措施 |
| ---- | ------ | ---- | -------- |
| 文档与代码不同步 | 高 | 高 | 在 CI/CD 中添加文档审查步骤，代码变更时强制检查文档 |
| 文档过于复杂吓退新人 | 中 | 中 | 采用分层文档结构，从简单到复杂 |
| 示例代码过期 | 高 | 中 | 将示例代码纳入测试范围，定期验证 |
| 架构图难以维护 | 中 | 低 | 使用 Mermaid 文本绘图，便于版本管理 |

**已知限制:**
- 文档为 Markdown 格式，不支持交互式示例
- 架构图为静态图，不实时反映代码变化
- API 示例需要手动维护，可能与实际端点不同步

**未来考虑 (Out of Scope but worth noting):**
- 考虑引入 AsciiDoc 增强文档表现力
- 考虑搭建独立的文档站点（如 GitBook、Docsify）
- 考虑添加视频教程补充文字文档
- 考虑引入 OpenAPI Generator 自动生成客户端 SDK 示例
