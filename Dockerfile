# ---- Stage 1: Build the app ----
FROM eclipse-temurin:17-jdk-alpine AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for caching dependencies)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy the rest of the source code
COPY src src

# Package the Spring Boot application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# ---- Stage 2: Run the app ----
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot's default port
EXPOSE 9192

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
