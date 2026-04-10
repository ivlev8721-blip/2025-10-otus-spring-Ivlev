# VinylLib - Руководство по развертыванию

## Содержание

1. [Запуск на локальном ПК](#запуск-на-локальном-пк)
2. [Запуск через Docker](#запуск-через-docker)

---

## Запуск на локальном ПК

### Предварительные требования

- JDK 11
- Maven 3.6.3 (рекомендуется)
- Docker Desktop 

### Шаг 1: Запуск PostgreSQL

```powershell
# Перейти в каталог проекта
cd C:\WORK\DSHTRANIN\2025-10-otus-spring-Ivlev2\homework01

# Запустить только PostgreSQL
docker-compose up -d postgres

# Проверить статус
docker-compose ps
```

### Шаг 2: Запуск приложения

```powershell
# Вариант 1: Через Maven
C:\mvn\apache-maven-3.6.3\bin\mvn.cmd spring-boot:run

# Вариант 2: Сборка JAR и запуск
C:\mvn\apache-maven-3.6.3\bin\mvn.cmd clean package -DskipTests
java -jar target\vinyl-library-0.0.1-SNAPSHOT.jar
```

### Доступ к приложению

- Приложение: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Actuator Health: http://localhost:8080/actuator/health

### Учетные записи по умолчанию

- Admin: admin / password
- User: user / password

### Остановка

```powershell
# Остановить приложение: Ctrl+C в терминале

# Остановить PostgreSQL
docker-compose down

# Остановить с удалением данных БД
docker-compose down -v
```

---

## Запуск через Docker

### Вариант 1: Запуск всех сервисов

```powershell
# Перейти в каталог проекта
cd C:\WORK\DSHTRANIN\2025-10-otus-spring-Ivlev2\homework01

# Запустить все сервисы (PostgreSQL + приложение + PgAdmin)
docker-compose up -d

# Просмотр логов
docker-compose logs -f vinyl-app

# Проверка статуса
docker-compose ps
```

### Вариант 2: Запуск только PostgreSQL

```powershell
# Запустить только базу данных
docker-compose up -d postgres

# Приложение запускать локально (см. раздел выше)
```

### Доступ к сервисам

- VinylLib: http://localhost:8080
- PgAdmin: http://localhost:5050 (admin@vinyl.com / admin)
- PostgreSQL: localhost:5432 (postgres / postgres)

### Управление контейнерами

```powershell
# Перезапуск приложения
docker-compose restart vinyl-app

# Пересборка образа и запуск
docker-compose up -d --build

# Просмотр логов
docker-compose logs -f

# Остановка всех сервисов
docker-compose down

# Остановка с удалением volumes
docker-compose down -v
```

### Сборка Docker образа вручную

```powershell
# Сборка образа
docker build -t vinyl-library:latest .

# Проверка образа
docker images | findstr vinyl-library

# Запуск контейнера вручную
docker run -d `
  --name vinyl-app `
  -p 8080:8080 `
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/vinyldb `
  -e DB_USER=postgres `
  -e DB_PASSWORD=postgres `
  vinyl-library:latest
```

### Troubleshooting

#### Порт 8080 занят

```powershell
# Найти процесс на порту 8080
netstat -ano | findstr :8080

# Остановить Docker контейнеры
docker-compose down
```

#### База данных недоступна

```powershell
# Проверить статус PostgreSQL
docker-compose ps postgres

# Просмотреть логи PostgreSQL
docker-compose logs postgres

# Перезапустить PostgreSQL
docker-compose restart postgres
```

#### Проблемы с подключением к БД

```powershell
# Проверить сетевое подключение
docker exec -it vinyl-postgres psql -U postgres -c "\l"

# Проверить переменные окружения
docker-compose config
```
