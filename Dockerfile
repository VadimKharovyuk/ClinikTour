FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Копируем собранный JAR-файл
COPY target/*.jar app.jar

# Создаем директории для постоянных данных и логов
VOLUME /app/data
VOLUME /app/logs

# Экспонируем порт, на котором работает приложение
EXPOSE 2817

# Запускаем приложение
CMD ["java", "-jar", "app.jar"]