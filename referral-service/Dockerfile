# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Copy the entire project
COPY . .

# Build required API modules first
RUN mvn clean install -pl user-api -am -DskipTests

# Build referral service
RUN cd referral-service && \
    mvn clean package spring-boot:repackage -DskipTests && \
    ls -la target/

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /build/referral-service/target/*.jar app.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:MaxMetaspaceSize=256m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
