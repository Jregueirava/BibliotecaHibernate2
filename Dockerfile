# Etapa de compilación con Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa de ejecución ligera
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]