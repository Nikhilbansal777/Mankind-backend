# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY . .

RUN mvn clean package -pl mankind_services -DskipTests

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /build/mankind_services/target/mankind_services-*.jar app.jar

ENV JAVA_OPTS="-Xms256m -Xmx1024m"
ENV SERVER_PORT=8085

EXPOSE 8085

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
