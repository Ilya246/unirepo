# Используем официальный образ OpenJDK
FROM openjdk:26-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR файл в контейнер
COPY target/_AMEBA_-_PESEZ_-1.0-SNAPCHOT.jar app.jar

# Открываем порт, на котором работает приложение
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]