# Spring Cloud Config Server для VinylLib

## Описание

Этот модуль предоставляет централизованное управление конфигурацией для приложения VinylLib через Spring Cloud Config Server.

## Структура конфигурационных файлов

```
config-repo/
├── vinyl-library.yml           # Общая конфигурация для всех профилей
├── vinyl-library-dev.yml       # Конфигурация для разработки
├── vinyl-library-prod.yml      # Конфигурация для production
└── vinyl-library-kubernetes.yml # Конфигурация для Kubernetes
```

## Запуск Config Server

### Локально

1. Создайте отдельный Spring Boot проект для Config Server:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

2. Добавьте аннотацию `@EnableConfigServer` в главный класс:

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

3. Настройте `application.yml`:

```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-username/vinyl-library-config
          default-label: main
          clone-on-start: true
        # Или используйте локальную файловую систему:
        native:
          search-locations: file:///path/to/config-repo
  profiles:
    active: native
```

### В Docker

```bash
docker run -d \
  --name config-server \
  -p 8888:8888 \
  -e SPRING_CLOUD_CONFIG_SERVER_GIT_URI=https://github.com/your-username/vinyl-library-config \
  hyness/spring-cloud-config-server
```

### В Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: config-server
spec:
  replicas: 1
  selector:
    matchLabels:
      app: config-server
  template:
    metadata:
      labels:
        app: config-server
    spec:
      containers:
      - name: config-server
        image: hyness/spring-cloud-config-server
        ports:
        - containerPort: 8888
        env:
        - name: SPRING_CLOUD_CONFIG_SERVER_GIT_URI
          value: "https://github.com/your-username/vinyl-library-config"
```

## Использование в VinylLib

### 1. Включение Config Client

В `bootstrap.yml` уже настроено подключение к Config Server:

```yaml
spring:
  cloud:
    config:
      enabled: true
      uri: http://localhost:8888
      fail-fast: false
```

### 2. Переменные окружения

```bash
# Включить Config Server
export CONFIG_SERVER_ENABLED=true
export CONFIG_SERVER_URI=http://config-server:8888

# Запустить приложение
java -jar vinyl-library.jar
```

### 3. Kubernetes

В Kubernetes Config Server URI автоматически определяется через Service Discovery:

```yaml
env:
- name: CONFIG_SERVER_ENABLED
  value: "true"
- name: CONFIG_SERVER_URI
  value: "http://config-server-service:8888"
```

## Обновление конфигурации без перезапуска

VinylLib поддерживает динамическое обновление конфигурации через Spring Cloud Bus или Actuator refresh endpoint:

```bash
# Обновить конфигурацию
curl -X POST http://localhost:8080/actuator/refresh
```

## Безопасность

### Шифрование конфиденциальных данных

Config Server поддерживает шифрование значений:

```yaml
# В config-repo/vinyl-library-prod.yml
spring:
  datasource:
    password: '{cipher}AQA...' # Зашифрованный пароль
jwt:
  secret: '{cipher}AQB...' # Зашифрованный секрет
```

### Базовая аутентификация

Настройте Config Server с базовой аутентификацией:

```yaml
# Config Server application.yml
spring:
  security:
    user:
      name: config-user
      password: config-password
```

В VinylLib:

```yaml
# bootstrap.yml
spring:
  cloud:
    config:
      username: config-user
      password: config-password
```

## Мониторинг

Config Server предоставляет Actuator endpoints:

```bash
# Проверить здоровье
curl http://localhost:8888/actuator/health

# Получить конфигурацию для приложения
curl http://localhost:8888/vinyl-library/default

# Получить конфигурацию для профиля
curl http://localhost:8888/vinyl-library/prod
```

## Примеры конфигурационных файлов

### vinyl-library.yml (общая конфигурация)

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus, refresh
  endpoint:
    health:
      show-details: always

logging:
  level:
    root: INFO
    ru.otus.vivlev: DEBUG
```

### vinyl-library-prod.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://prod-postgres:5432/vinyldb
    username: vinyl_user
    password: '{cipher}encrypted_password'
  jpa:
    show-sql: false

logging:
  level:
    root: WARN
    ru.otus.vivlev: INFO
```

## Troubleshooting

### Config Server недоступен

Если Config Server недоступен, приложение использует локальную конфигурацию из `application.yml` благодаря `fail-fast: false`.

### Проверка подключения

```bash
# Проверить, какую конфигурацию получает приложение
curl http://localhost:8888/vinyl-library/default
```

### Логи

```bash
# Включить debug логи для Config Client
export LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_CLOUD_CONFIG=DEBUG
```
