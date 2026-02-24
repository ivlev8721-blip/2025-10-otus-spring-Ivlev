# Docker инструкция для Library Application

## Предварительные требования

- Docker Desktop установлен и запущен
- Docker Compose установлен (входит в Docker Desktop)

## Способы запуска

### Вариант 1: Использование Docker Compose (Рекомендуется)

#### Сборка и запуск:
```powershell
docker-compose up --build
```

#### Запуск в фоновом режиме:
```powershell
docker-compose up -d
```

#### Просмотр логов:
```powershell
docker-compose logs -f
```

#### Остановка:
```powershell
docker-compose down
```

### Вариант 2: Использование Docker напрямую

#### Сборка образа:
```powershell
docker build -t library-app:latest .
```

#### Запуск контейнера:
```powershell
docker run -d -p 8080:8080 --name library-app library-app:latest
```

#### Просмотр логов:
```powershell
docker logs -f library-app
```

#### Остановка и удаление:
```powershell
docker stop library-app
docker rm library-app
```

## Проверка работы приложения

После запуска контейнера откройте в браузере:

### Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

### REST API эндпоинты:
```
http://localhost:8080/api/v1/author
http://localhost:8080/api/v1/book
http://localhost:8080/api/v1/genre
http://localhost:8080/api/v1/comment
```

### Health Check:
```
http://localhost:8080/actuator/health
```

## Полезные команды

### Просмотр запущенных контейнеров:
```powershell
docker ps
```

### Просмотр всех контейнеров:
```powershell
docker ps -a
```

### Просмотр образов:
```powershell
docker images
```

### Удаление образа:
```powershell
docker rmi library-app:latest
```

### Очистка неиспользуемых ресурсов:
```powershell
docker system prune -a
```

### Вход в контейнер (для отладки):
```powershell
docker exec -it library-app sh
```

## Структура Docker файлов

- **Dockerfile** - многоступенчатая сборка для оптимизации размера образа
- **.dockerignore** - исключение ненужных файлов из контекста сборки
- **docker-compose.yml** - оркестрация контейнеров

## Особенности реализации

✅ **Многоступенчатая сборка** - уменьшает размер финального образа  
✅ **Непривилегированный пользователь** - повышает безопасность  
✅ **Health check** - автоматическая проверка состояния приложения  
✅ **Оптимизация слоев** - кэширование зависимостей Maven  
✅ **JVM настройки** - оптимизация памяти для контейнера  

## Переменные окружения

Вы можете настроить приложение через переменные окружения в `docker-compose.yml`:

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod
  - JAVA_OPTS=-Xmx1g -Xms512m
  - SERVER_PORT=8080
```

## Troubleshooting

### Порт 8080 уже занят:
Измените порт в `docker-compose.yml`:
```yaml
ports:
  - "9090:8080"
```

### Проблемы с памятью:
Увеличьте лимиты в `JAVA_OPTS`:
```yaml
environment:
  - JAVA_OPTS=-Xmx1g -Xms512m
```

### Контейнер не запускается:
Проверьте логи:
```powershell
docker-compose logs
```
