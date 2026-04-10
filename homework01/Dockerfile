# Многоступенчатая сборка для оптимизации размера образа

# Этап 1: Сборка приложения
FROM maven:3.8.4-openjdk-11-slim AS build
WORKDIR /app

# Копируем pom.xml и загружаем зависимости (кэширование слоя)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true

# Этап 2: Создание финального образа
FROM eclipse-temurin:11-jre-jammy
WORKDIR /app

# Создаем непривилегированного пользователя
RUN groupadd -r spring && useradd -r -g spring spring

# Копируем jar из этапа сборки
COPY --from=build /app/target/*.jar app.jar

# Меняем владельца файла
RUN chown spring:spring app.jar

# Переключаемся на непривилегированного пользователя
USER spring:spring

# Открываем порт приложения
EXPOSE 8080

# Настройки JVM для контейнера
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Запуск приложения
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
