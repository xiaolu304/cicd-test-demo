# ============================================
# Dockerfile for Spring Boot Store Application
# ============================================
# 作者: 陆梦琳
# 日期: 2026-06-26
# 描述: 多阶段构建优化镜像大小
# ============================================

# ========== 阶段1：构建阶段 ==========
FROM maven:3.9-eclipse-temurin-17 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 并下载依赖（利用 Docker 缓存层）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 编译打包（跳过测试，因为测试已在 CI 中执行）
RUN mvn package -DskipTests -B

# ========== 阶段2：运行阶段 ==========
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 创建非 root 用户（安全最佳实践）
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 从构建阶段复制 JAR 包
COPY --from=builder /app/target/*.jar app.jar

# 修改文件所有权
RUN chown appuser:appgroup app.jar

# 切换到非 root 用户
USER appuser

# 暴露应用端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]