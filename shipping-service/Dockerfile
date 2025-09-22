# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Copy the entire project
COPY . .

# First, build and install the API modules to local repository
RUN mvn clean install -pl user-api,product-api,cart-api,coupon-api -am -DskipTests

# Then build the order service
RUN cd order-service && \
    mvn clean package spring-boot:repackage -DskipTests && \
    ls -la target/

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /build/order-service/target/*.jar app.jar

# Set environment variables
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:MaxMetaspaceSize=256m"

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
