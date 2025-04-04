# Сборка приложения
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Запуск приложения
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
VOLUME /app/data
VOLUME /app/logs
EXPOSE 2817
CMD ["java", "-jar", "app.jar"]