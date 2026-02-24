# Apache Kafka интеграция в Library Application

## Описание

Приложение использует Apache Kafka для организации асинхронного взаимодействия между сервисами через события (Event-Driven Architecture).

## Архитектура событий

### События (Events)

Приложение генерирует события для всех CRUD операций:

#### 1. **BookEvent** - События книг
- **CREATED** - книга создана
- **UPDATED** - книга обновлена
- **DELETED** - книга удалена
- **Топик**: `book-events`

#### 2. **AuthorEvent** - События авторов
- **CREATED** - автор создан
- **UPDATED** - автор обновлен
- **DELETED** - автор удален
- **Топик**: `author-events`

#### 3. **GenreEvent** - События жанров
- **CREATED** - жанр создан
- **UPDATED** - жанр обновлен
- **DELETED** - жанр удален
- **Топик**: `genre-events`

#### 4. **CommentEvent** - События комментариев
- **CREATED** - комментарий создан
- **UPDATED** - комментарий обновлен
- **DELETED** - комментарий удален
- **Топик**: `comment-events`

## Структура события

Каждое событие содержит:
```json
{
  "eventId": "uuid",
  "eventType": "CREATED|UPDATED|DELETED",
  "data": { /* DTO объект */ },
  "timestamp": "2026-02-24T12:00:00"
}
```

## Компоненты Kafka

### Producer (Отправитель событий)

**LibraryEventProducer** - отправляет события в Kafka топики при выполнении CRUD операций.

```java
@Service
public class LibraryEventProducer {
    public void sendBookEvent(BookEvent event);
    public void sendAuthorEvent(AuthorEvent event);
    public void sendGenreEvent(GenreEvent event);
    public void sendCommentEvent(CommentEvent event);
}
```

### Consumer (Получатель событий)

**LibraryEventConsumer** - обрабатывает события из Kafka топиков и логирует их.

```java
@Service
public class LibraryEventConsumer {
    @KafkaListener(topics = "book-events")
    public void consumeBookEvent(BookEvent event);
    
    @KafkaListener(topics = "author-events")
    public void consumeAuthorEvent(AuthorEvent event);
    
    // и т.д.
}
```

## Запуск с Kafka

### Вариант 1: Docker Compose (Рекомендуется)

```powershell
docker-compose up -d
```

Это запустит:
- **Zookeeper** (порт 2181)
- **Kafka** (порт 9092)
- **Library Application** (порт 8080)

### Вариант 2: Локальный Kafka

1. Установите и запустите Kafka локально
2. Запустите приложение:
```powershell
mvn spring-boot:run
```

## Проверка работы Kafka

### 1. Создайте книгу через API:

```bash
curl -X POST http://localhost:8080/api/v1/book \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Война и мир",
    "author": {"id": 1, "fullName": "Лев Толстой"},
    "genres": [{"id": 1, "genreName": "Роман"}]
  }'
```

### 2. Проверьте логи приложения:

```powershell
docker-compose logs -f library-app
```

Вы увидите:
```
Отправка события книги: CREATED - Война и мир
Событие успешно отправлено в топик: book-events
Получено событие книги: CREATED - ID: 1, Название: Война и мир
```

### 3. Просмотр топиков Kafka:

```powershell
# Войти в контейнер Kafka
docker exec -it kafka bash

# Список топиков
kafka-topics --list --bootstrap-server localhost:9092

# Чтение сообщений из топика
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic book-events --from-beginning
```

## Конфигурация

### application.yaml

```yaml
kafka:
  bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
  consumer:
    group-id: library-group
    auto-offset-reset: earliest
  topics:
    book-events: book-events
    author-events: author-events
    genre-events: genre-events
    comment-events: comment-events
```

### Переменные окружения

- **KAFKA_BOOTSTRAP_SERVERS** - адрес Kafka брокера (по умолчанию: localhost:9092)

## Примеры использования

### Отслеживание всех операций с книгами

Consumer автоматически логирует все события:

```
[INFO] Получено событие книги: CREATED - ID: 1, Название: Война и мир
[INFO] Книга создана: Война и мир
[INFO] Получено событие книги: UPDATED - ID: 1, Название: Война и мир (исправлено)
[INFO] Книга обновлена: Война и мир (исправлено)
[INFO] Получено событие книги: DELETED - ID: 1
[INFO] Книга удалена: ID 1
```

### Расширение функциональности

Вы можете добавить дополнительные Consumer'ы для:
- Отправки уведомлений пользователям
- Синхронизации с другими системами
- Аудита и логирования
- Построения аналитики
- Кэширования данных

Пример:
```java
@Service
public class NotificationConsumer {
    
    @KafkaListener(topics = "book-events")
    public void sendNotification(BookEvent event) {
        if (event.getEventType() == EventType.CREATED) {
            // Отправить уведомление о новой книге
            notificationService.send("Новая книга: " + event.getBook().getTitle());
        }
    }
}
```

## Мониторинг

### Проверка состояния Kafka:

```powershell
# Проверка работы Kafka
docker exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092

# Описание топика
docker exec kafka kafka-topics --describe --topic book-events --bootstrap-server localhost:9092
```

### Метрики через Actuator:

```
http://localhost:8080/actuator/metrics
```

## Troubleshooting

### Kafka не запускается:

Проверьте, что порты 2181 и 9092 свободны:
```powershell
netstat -ano | findstr "2181"
netstat -ano | findstr "9092"
```

### События не отправляются:

1. Проверьте подключение к Kafka:
```powershell
docker-compose logs kafka
```

2. Убедитесь, что топики созданы:
```powershell
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Consumer не получает события:

1. Проверьте group-id в конфигурации
2. Проверьте логи приложения
3. Убедитесь, что consumer запущен

## Преимущества использования Kafka

✅ **Асинхронность** - операции не блокируют основной поток  
✅ **Масштабируемость** - легко добавить новых consumer'ов  
✅ **Надежность** - события сохраняются и могут быть переобработаны  
✅ **Отказоустойчивость** - при падении consumer'а события не теряются  
✅ **Аудит** - полная история всех операций  
✅ **Интеграция** - простое подключение других сервисов  

## Дальнейшее развитие

Возможные улучшения:
- Добавление Kafka Streams для обработки потоков
- Реализация SAGA паттерна для распределенных транзакций
- Добавление Schema Registry для версионирования событий
- Настройка партиционирования для масштабирования
- Добавление мониторинга через Kafka Manager или Confluent Control Center
