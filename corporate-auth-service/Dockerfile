# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Copy the entire project
COPY . .

# Build the corporate auth service
RUN mvn clean package -pl corporate-auth-service -am -DskipTests

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /build/corporate-auth-service/target/*.jar app.jar

# Set environment variables
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Expose the application port
EXPOSE 8086

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
