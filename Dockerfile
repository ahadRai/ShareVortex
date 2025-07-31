# Use a small JDK 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy everything into the container
COPY . .

# Build the project (skip tests for speed)
RUN ./mvnw clean package -DskipTests

# Expose Spring Boot port
EXPOSE 8080

# Run the jar file
CMD ["java", "-jar", "target/sharevortex-0.0.1-SNAPSHOT.jar"]
