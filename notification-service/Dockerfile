FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
COPY common/pom.xml common/
COPY task-service/pom.xml task-service/
COPY event-service/pom.xml event-service/
COPY notification-service/pom.xml notification-service/

RUN mvn -B -pl notification-service -am dependency:go-offline -DskipTests || true

COPY common/src common/src
COPY notification-service/src notification-service/src

RUN mvn -B -pl notification-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /build/notification-service/target/*.jar app.jar

EXPOSE 8003

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]