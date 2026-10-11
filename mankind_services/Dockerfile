# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

ARG MAVEN_OPTS="-Xmx1024m -XX:MaxMetaspaceSize=512m"
ENV MAVEN_OPTS=$MAVEN_OPTS

# Resolve dependencies in a separate layer (reused when only source changes)
COPY pom.xml .
COPY mankind_services/pom.xml mankind_services/pom.xml
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl mankind_services -am dependency:go-offline -DskipTests -q || true

COPY mankind_services/src mankind_services/src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl mankind_services -am package -DskipTests -q

FROM build AS extract
RUN java -Djarmode=tools -jar mankind_services/target/mankind_services-*.jar extract --layers --destination /build/extracted

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

COPY --from=extract /build/extracted/dependencies/ ./
COPY --from=extract /build/extracted/spring-boot-loader/ ./
COPY --from=extract /build/extracted/snapshot-dependencies/ ./
COPY --from=extract /build/extracted/application/ ./

ENV JAVA_OPTS="-Xms256m -Xmx1024m"
ENV SERVER_PORT=8085

EXPOSE 8085

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
