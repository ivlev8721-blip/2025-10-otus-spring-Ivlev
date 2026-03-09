# VinylLib - Виниловая библиотека

Онлайн-каталог коллекционера виниловых пластинок с возможностью прослушивания (асинхронная выдача треков), ведения статистики прослушиваний и пакетного импорта/экспорта данных.

## Технологический стек

### Backend
- **Spring Boot 2.4.4** - основной фреймворк
- **Spring Data JPA** - работа с БД
- **Spring Security** - аутентификация и авторизация (JWT + Form-based)
- **Spring Integration** - асинхронная обработка запросов на прослушивание
- **Spring Batch** - пакетная обработка данных (импорт/экспорт)
- **Spring Shell** - CLI для управления батч-заданиями
- **Spring WebSocket** - уведомления в реальном времени
- **PostgreSQL** - основная БД (с поддержкой H2 для тестов)
- **Liquibase** - управление миграциями БД

### Frontend
- **Thymeleaf** - серверный рендеринг HTML
- **Bootstrap 5** - UI фреймворк
- **AJAX/Fetch API** - динамическое обновление контента
- **SockJS + STOMP** - WebSocket клиент

### DevOps
- **Docker** - контейнеризация
- **Docker Compose** - оркестрация сервисов
- **Spring Boot Actuator** - мониторинг и health checks

## Архитектура

Проект следует принципам **Cloud-Ready** и **12-Factor App**:
- Конфигурация через переменные окружения
- Stateless приложение
- Готовность к горизонтальному масштабированию
- Health checks для Kubernetes/Cloud платформ
- Логирование в файлы и stdout

## Функциональные возможности

### Для гостей (неавторизованных пользователей)
- ✅ Просмотр каталога пластинок
- ✅ Поиск по названию и исполнителю (AJAX)
- ✅ Фильтрация по жанрам
- ✅ Просмотр деталей альбома и отзывов

### Для пользователей (ROLE_USER)
- ✅ Все возможности гостя
- ✅ Добавление альбомов в коллекцию
- ✅ Создание вишлиста (хочу купить)
- ✅ Написание отзывов и оценок
- ✅ Асинхронное прослушивание треков (имитация через Spring Integration)
- ✅ WebSocket уведомления о готовности трека

### Для администраторов (ROLE_ADMIN)
- ✅ Все возможности пользователя
- ✅ Добавление/редактирование/удаление альбомов
- ✅ Запуск пакетных операций через Spring Shell
- ✅ Импорт альбомов из CSV
- ✅ Просмотр статуса батч-заданий

## Быстрый старт

### Требования
- Java 11+
- Maven 3.6+
- Docker и Docker Compose (опционально)

### Запуск с Docker Compose

```bash
# Клонировать репозиторий
cd homework01

# Запустить все сервисы
docker-compose up -d

# Приложение будет доступно по адресу:
# http://localhost:8080

# PostgreSQL: localhost:5432
# pgAdmin: http://localhost:5050 (admin@vinyl.com / admin)
```

### Запуск локально

```bash
# 1. Запустить PostgreSQL
docker run -d \
  --name vinyl-postgres \
  -e POSTGRES_DB=vinyldb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:14-alpine

# 2. Собрать проект
mvn clean package -DskipTests

# 3. Запустить приложение
java -jar target/vinyl-library-0.0.1-SNAPSHOT.jar

# Или через Maven
mvn spring-boot:run
```

### Переменные окружения

```bash
DB_URL=jdbc:postgresql://localhost:5432/vinyldb
DB_USER=postgres
DB_PASSWORD=postgres
JWT_SECRET=your-secret-key-here
SERVER_PORT=8080
LOG_FILE=/logs/vinyl-library.log
```

## Использование

### Web интерфейс

1. **Главная страница**: http://localhost:8080
2. **Каталог**: http://localhost:8080/catalog
3. **Вход**: http://localhost:8080/login

#### Тестовые учетные записи:
- **Администратор**: `admin` / `password`
- **Пользователь**: `user` / `password`

### REST API

#### Публичные эндпоинты

```bash
# Получить все альбомы (с пагинацией)
GET /api/albums?page=0&size=20

# Поиск альбомов
GET /api/albums/search?query=Pink+Floyd

# Получить альбом по ID
GET /api/albums/{id}

# Получить отзывы по альбому
GET /api/reviews/album/{albumId}
```

#### Защищенные эндпоинты (требуется авторизация)

```bash
# Добавить альбом в коллекцию
POST /api/collection/add/{albumId}?wishlist=false
Authorization: Bearer {jwt-token}

# Запросить прослушивание трека
POST /api/stream/listen?albumId={albumId}&trackId={trackId}
Authorization: Bearer {jwt-token}

# Создать отзыв
POST /api/reviews
Authorization: Bearer {jwt-token}
Content-Type: application/json
{
  "albumId": 1,
  "rating": 5,
  "comment": "Отличный альбом!"
}
```

#### Административные эндпоинты (ROLE_ADMIN)

```bash
# Создать альбом
POST /api/albums
Authorization: Bearer {jwt-token}
Content-Type: application/json
{
  "title": "Abbey Road",
  "artist": "The Beatles",
  "genreId": 1,
  "releaseYear": 1969,
  "coverImageUrl": "/images/abbey-road.jpg"
}

# Обновить альбом
PUT /api/albums/{id}

# Удалить альбом
DELETE /api/albums/{id}
```

### Spring Shell (CLI)

```bash
# Запустить приложение в режиме Shell
java -jar target/vinyl-library-0.0.1-SNAPSHOT.jar --spring.shell.interactive.enabled=true

# Доступные команды:

# Импорт альбомов из CSV
shell:>batch:import --file=data/albums.csv

# Проверить статус батч-задания
shell:>batch:status --id=1

# Список последних батч-заданий
shell:>batch:list --count=10

# Выход
shell:>exit
```

### Формат CSV для импорта

Создайте файл `data/albums.csv`:

```csv
title,artist,genreName,releaseYear,coverImageUrl
"The Dark Side of the Moon","Pink Floyd","Rock",1973,"/images/dark-side.jpg"
"Kind of Blue","Miles Davis","Jazz",1959,"/images/kind-of-blue.jpg"
"Abbey Road","The Beatles","Rock",1969,"/images/abbey-road.jpg"
```

## Асинхронное прослушивание треков

### Как это работает

1. Пользователь нажимает "Слушать" на альбоме
2. Запрос отправляется в Spring Integration канал `listenRequestChannel`
3. **Transformer** преобразует запрос в `ListenRequest`
4. **Service Activator** имитирует подготовку трека (3 секунды)
5. **Publish-Subscribe Channel** отправляет уведомление:
   - В WebSocket (пользователь получает уведомление)
   - В лог (сохранение события)

### WebSocket подключение

```javascript
// Подключение к WebSocket
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    // Подписка на уведомления
    stompClient.subscribe('/user/topic/stream-ready', function(message) {
        const notification = JSON.parse(message.body);
        console.log('Track ready:', notification.streamUrl);
        // Показать уведомление пользователю
    });
});
```

## Мониторинг и Health Checks

### Actuator эндпоинты

```bash
# Health check
GET http://localhost:8080/actuator/health

# Метрики
GET http://localhost:8080/actuator/metrics

# Информация о приложении
GET http://localhost:8080/actuator/info
```

### Для Kubernetes

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 10

readinessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 5
```

## Структура проекта

```
homework01/
├── src/main/java/ru/otus/vivlev/library/
│   ├── batch/              # Spring Batch конфигурация и процессоры
│   ├── config/             # Конфигурация (Security, WebSocket, Integration)
│   ├── controller/         # REST и Web контроллеры
│   ├── domain/             # JPA сущности (Album, Track, Review, etc.)
│   ├── dto/                # Data Transfer Objects
│   ├── integration/        # Spring Integration компоненты
│   ├── repository/         # Spring Data JPA репозитории
│   ├── service/            # Бизнес-логика
│   └── shell/              # Spring Shell команды
├── src/main/resources/
│   ├── db/changelog/       # Liquibase миграции
│   ├── templates/          # Thymeleaf шаблоны
│   └── application.yml     # Конфигурация приложения
├── docker-compose.yml      # Docker Compose конфигурация
├── Dockerfile              # Многоступенчатая сборка Docker образа
└── pom.xml                 # Maven зависимости
```

## Разработка

### Сборка проекта

```bash
# Полная сборка с тестами
mvn clean install

# Сборка без тестов
mvn clean package -DskipTests

# Запуск тестов
mvn test
```

### Создание Docker образа

```bash
# Сборка образа
docker build -t vinyl-library:latest .

# Запуск контейнера
docker run -d \
  -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/vinyldb \
  -e DB_USER=postgres \
  -e DB_PASSWORD=postgres \
  vinyl-library:latest
```

## Troubleshooting

### Проблема: Приложение не может подключиться к БД

**Решение**: Проверьте, что PostgreSQL запущен и доступен:
```bash
docker ps | grep postgres
psql -h localhost -U postgres -d vinyldb
```

### Проблема: Liquibase ошибки при старте

**Решение**: Очистите БД и перезапустите:
```bash
docker-compose down -v
docker-compose up -d
```

### Проблема: WebSocket не подключается

**Решение**: Проверьте CORS настройки и убедитесь, что `/ws` эндпоинт доступен:
```bash
curl http://localhost:8080/ws/info
```

## Лицензия

Учебный проект для курса OTUS Spring Framework

## Автор

Разработано в рамках домашнего задания по курсу Spring Framework
