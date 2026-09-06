# syntax=docker/dockerfile:1.7
# ============================================================================
# 构建阶段 —— 构建上下文必须是仓库根（compose 中 context: .）
# api 按硬编码版本依赖兄弟模块（core/security, model, mapper, service），
# Maven reactor 需要完整目录树。
# ============================================================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# -- 第 1 层：仅 pom ------------------------------------------------------
# 任何 pom 变更只会失效依赖层，不影响下面的源码层
COPY pom.xml ./
COPY core/pom.xml core/pom.xml
COPY core/common/pom.xml core/common/pom.xml
COPY core/security/pom.xml core/security/pom.xml
COPY model/pom.xml model/pom.xml
COPY mapper/pom.xml mapper/pom.xml
COPY service/pom.xml service/pom.xml
COPY api/pom.xml api/pom.xml

# 预热依赖缓存。go-offline 对多模块 reactor 不完全（兄弟构件+system scope），
# 故 || true；真正的跨构建缓存靠下面的 BuildKit cache mount。
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q dependency:go-offline -pl api -am || true

# -- 第 2 层：源码 --------------------------------------------------------
COPY core/ core/
COPY model/ model/
COPY mapper/ mapper/
COPY service/ service/
# 含 api/libs/open-sdk-2.1.11.jar（system scope）
COPY api/ api/

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl api -am package -DskipTests

# ============================================================================
# 运行阶段
# ============================================================================
FROM eclipse-temurin:17-jre-jammy
ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-Xms512m -Xmx1024m -Duser.timezone=Asia/Shanghai"
WORKDIR /app
# api/pom.xml 无 finalName → api-2.2.0.jar（.original 不匹配通配符）
COPY --from=build /build/api/target/api-*.jar app.jar
EXPOSE 8086
# sh -c + exec：JAVA_OPTS 可展开，java 保持 PID 1（能收到 SIGTERM 优雅停机）
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
