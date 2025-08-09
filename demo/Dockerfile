# Stage 1: Build the application using the 'prod' profile
FROM maven:3.9.4-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
# Activate the 'prod' profile to build a JAR with only the PostgreSQL driver
RUN mvn -B -Pprod dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

# Stage 2: Create the final runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Create and switch to a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
EXPOSE 8081
# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]