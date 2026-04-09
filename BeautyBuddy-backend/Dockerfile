# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Environment variables (override at runtime with -e)
ENV JAVA_OPTS="-Xmx512m"
ENV JWT_SECRET_KEY=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]