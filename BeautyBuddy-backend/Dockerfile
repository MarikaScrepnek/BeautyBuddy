# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY BeautyBuddy-backend /app
RUN mvn -T 1C clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/BeautyBuddy-backend-*.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Environment variables (override at runtime with -e)
ENV JWT_SECRET_KEY=""

# Run the application
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]