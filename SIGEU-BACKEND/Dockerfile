# ── Stage 1: Build the JAR with Maven ──
FROM maven:3.9.5-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build and skip tests for faster deployment
RUN mvn clean package -DskipTests

# ── Stage 2: Run the JAR ──
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from stage 1
COPY --from=builder /app/target/sigeu-backend-1.0.0.jar app.jar

# Expose port (Render uses the PORT env variable)
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
