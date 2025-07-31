# -------- Stage 1: Build the application --------
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# -------- Stage 2: Run the built JAR --------
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy only the final JAR from the builder stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
