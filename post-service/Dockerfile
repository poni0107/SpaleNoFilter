# ================= STAGE 1: Build =================
FROM maven:3.9.2-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

# Build JAR sa testovima (CI/CD workflow može da kontroliše)
RUN mvn clean package -DskipTests

# ================= STAGE 2: Runtime =================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Kopiranje JAR iz build stage-a
COPY --from=build /app/target/post_service-*.jar app.jar

# Environment variables za konfiguraciju
ENV MINIO_BUCKET=instagram-media
ENV MINIO_URL=http://minio:9000
ENV SERVER_PORT=8080

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=prod"]
