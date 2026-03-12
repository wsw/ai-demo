# ==================== 多阶段构建 Dockerfile ====================
# 作为 Jib 插件的备用方案，适用于需要自定义构建逻辑的场景
# 使用阿里云镜像加速：registry.cn-hangzhou.aliyuncs.com

# ==================== Stage 1: 构建阶段 ====================
FROM registry.cn-hangzhou.aliyuncs.com/library/eclipse-temurin:21-jdk-alpine AS builder

# 设置工作目录
WORKDIR /app

# 复制 Gradle 配置文件
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY gradlew ./

# 下载依赖（利用 Docker 缓存层）
RUN chmod +x gradlew && \
    ./gradlew dependencies --no-daemon || true

# 复制源代码
COPY src ./src

# 构建应用
RUN ./gradlew bootJar --no-daemon -x test

# ==================== Stage 2: 运行阶段 ====================
FROM registry.cn-hangzhou.aliyuncs.com/library/eclipse-temurin:21-jre-alpine

# 设置维护者信息
LABEL maintainer="Weishuwen"
LABEL version="0.0.1-SNAPSHOT"

# 设置时区
ENV TZ=Asia/Shanghai
ENV SPRING_PROFILES_ACTIVE=prod

# 安装时区数据（Alpine 默认不包含）
RUN apk add --no-cache tzdata

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/build/libs/*.jar app.jar

# 暴露端口
EXPOSE 8080 8081

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", \
    "-Xms256m", \
    "-Xmx512m", \
    "-XX:+UseG1GC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Duser.timezone=Asia/Shanghai", \
    "-jar", "app.jar"]
