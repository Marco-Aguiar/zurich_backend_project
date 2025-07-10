# --- STAGE 1: Build the Spring Boot Application ---
FROM maven:3.9.6-eclipse-temurin-17-focal AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file first to download dependencies
# This leverages Docker's layer caching - if pom.xml doesn't change,
# dependencies won't be re-downloaded
COPY pom.xml .

# Download dependencies (empty src/ for this step)
RUN mvn dependency:go-offline

# Copy the rest of the application source code
COPY src ./src

# Build the Spring Boot application, skipping tests
RUN mvn clean install -DskipTests

# --- STAGE 2: Create the final lightweight runtime image ---
# Use a smaller JRE-only base image
FROM eclipse-temurin:17-jre-focal

# Set the working directory
WORKDIR /app

# Copy the built JAR from the 'build' stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot application runs on (default 8080)
EXPOSE 8080

# Set the entrypoint to run your Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]