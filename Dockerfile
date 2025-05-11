# Используем базовый образ с Java
FROM openjdk:latest

# Установка рабочей директории внутри контейнера
WORKDIR /app

# Копирование JAR файла сборки проекта внутрь контейнера
COPY target/scala-2.13/rec-travel-designer.jar /app

# Команда для запуска приложения при старте контейнера
CMD ["java", "-jar", "rec-travel-designer.jar"]