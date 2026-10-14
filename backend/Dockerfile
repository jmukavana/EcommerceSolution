# ── Build stage ────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
# Copy wrapper first so dependency layer is cached
COPY gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew
# Cache dependencies
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon || true
# Build
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# ── Runtime stage ───────────────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
COPY --from=build /app/build/libs/*.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8050
ENTRYPOINT ["java","-XX:+UseContainerSupport","-jar","app.jar"]
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
CMD curl -f http://localhost:8050/actuator/health || exit 1