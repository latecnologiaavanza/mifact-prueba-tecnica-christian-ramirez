FROM maven:3.9-eclipse-temurin-8 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:8-jre-alpine
WORKDIR /app
ENV TZ=America/Lima
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=builder /app/target/inventory-api-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-Duser.timezone=America/Lima", "-jar", "app.jar"]
