# Проверка работы Hystrix

## 1. Запуск приложения

Запустите приложение из IntelliJ IDEA или через Maven:
```bash
cd C:\WORK\DSHTRANIN\2025-10-otus-spring-Ivlev\homework01
mvn spring-boot:run
```

## 2. Проверка Actuator endpoints

### Проверка здоровья приложения
```bash
curl http://localhost:8080/actuator/health
```

### Проверка метрик Hystrix (поток данных)
```bash
curl http://localhost:8080/actuator/hystrix.stream
```

Этот эндпоинт будет отдавать поток метрик в реальном времени.

## 3. Тестирование Circuit Breaker

### Шаг 1: Создание книги (успешный сценарий)
```bash
curl -X POST http://localhost:8080/api/v1/book \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "title": "Test Book",
    "author": {"id": 1, "fullName": "Test Author"},
    "genres": [{"id": 1, "genreName": "Test Genre"}]
  }'
```

**Ожидаемый результат в логах:**
```
INFO - Отправка события книги: CREATED - Test Book
INFO - Событие успешно отправлено в топик: book-events с ключом: ...
```

### Шаг 2: Симуляция ошибки Kafka

Остановите Kafka или измените конфигурацию на неверный адрес в `application.yaml`:
```yaml
kafka:
  bootstrap-servers: localhost:9999  # неверный порт
```

Перезапустите приложение и попробуйте создать книгу снова.

**Ожидаемый результат в логах:**
```
ERROR - Circuit breaker активирован для события книги: CREATED - Test Book. Причина: ...
```

### Шаг 3: Проверка срабатывания Circuit Breaker

После 5 неудачных попыток (согласно настройке `requestVolumeThreshold: 5`) circuit breaker откроется.

Следующие запросы будут сразу попадать в fallback без попытки отправки в Kafka:
```
ERROR - Circuit breaker активирован для события книги: CREATED - Test Book. Причина: Hystrix circuit short-circuited and is OPEN
```

### Шаг 4: Восстановление

Через 10 секунд (согласно `sleepWindowInMilliseconds: 10000`) circuit breaker перейдет в состояние HALF_OPEN и попробует выполнить запрос снова.

## 4. Мониторинг через логи

Запустите приложение и следите за логами:
```bash
tail -f logs/application-log.log
```

Или в IntelliJ IDEA смотрите консоль при запуске приложения.

## 5. Настройки Hystrix в проекте

### Текущие параметры:
- **Timeout**: 3000ms (3 секунды)
- **Request Volume Threshold**: 5 запросов
- **Error Threshold**: 50% ошибок
- **Sleep Window**: 10000ms (10 секунд)

### Что это означает:
1. Если запрос выполняется дольше 3 секунд → fallback
2. Circuit breaker откроется после 5 запросов с 50% ошибок
3. После открытия circuit breaker будет закрыт на 10 секунд
4. Через 10 секунд попробует выполнить один тестовый запрос

## 6. Проверка через интеграционный тест

Запустите созданный тест:
```bash
cd C:\WORK\DSHTRANIN\2025-10-otus-spring-Ivlev\homework01
mvn test -Dtest=HystrixIntegrationTest
```

## 7. Визуализация через Hystrix Dashboard (опционально)

Для визуального мониторинга можно добавить Hystrix Dashboard:

1. Добавьте зависимость в `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
```

2. Добавьте аннотацию в главный класс:
```java
@EnableHystrixDashboard
```

3. Откройте в браузере:
```
http://localhost:8080/hystrix
```

4. Введите URL потока метрик:
```
http://localhost:8080/actuator/hystrix.stream
```

## Признаки успешной работы Hystrix:

+ Приложение запускается без ошибок
+ В логах видны сообщения об отправке событий
+ При ошибках срабатывают fallback-методы
+ Circuit breaker открывается после порога ошибок
+ Эндпоинт `/actuator/hystrix.stream` отдает метрики
