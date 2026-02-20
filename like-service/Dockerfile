# =========================
# BUILD STAGE
# =========================
FROM maven:3.9.2-eclipse-temurin-17 AS build

# Postavljanje radnog direktorijuma
WORKDIR /app

# Kopiranje pom.xml i preuzimanje dependencija
COPY pom.xml .
RUN mvn dependency:go-offline

# Kopiranje izvornog koda i build
COPY src ./src
RUN mvn clean package -DskipTests

# =========================
# RUNTIME STAGE
# =========================
FROM openjdk:17-jdk-alpine AS runtime

# Radni direktorijum
WORKDIR /app

# Kopiranje jar fajla iz build faze
COPY --from=build /app/target/like-service-0.0.1-SNAPSHOT.jar app.jar

# Postavljanje portova i env promenljivih
EXPOSE 7000
ENV JAVA_OPTS="-Xms256m -Xmx512m -Dspring.profiles.active=prod"
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS
ENV JAVA_TOOL_OPTIONS="$JAVA_OPTS"

# Healthcheck za docker
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# Pokretanje aplikacije
ENTRYPOINT ["sh", "-c", "java $JAVA_TOOL_OPTIONS -jar app.jar"]

