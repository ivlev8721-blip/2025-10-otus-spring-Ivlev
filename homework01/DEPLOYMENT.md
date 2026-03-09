# VinylLib - Руководство по развертыванию

## Содержание

1. [Локальная разработка](#локальная-разработка)
2. [Docker](#docker)
3. [Kubernetes](#kubernetes)
4. [Spring Cloud Config](#spring-cloud-config)
5. [Production рекомендации](#production-рекомендации)

---

## Локальная разработка

### Предварительные требования

- JDK 11+
- Maven 3.6+
- PostgreSQL 14+ (или Docker)

### Запуск PostgreSQL

```bash
# Через Docker
docker run -d \
  --name vinyl-postgres \
  -e POSTGRES_DB=vinyldb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:14-alpine

# Или используйте docker-compose
docker-compose up -d postgres
```

### Сборка и запуск приложения

```bash
# Сборка
mvn clean package -DskipTests

# Запуск
java -jar target/vinyl-library-0.0.1-SNAPSHOT.jar

# Или через Maven
mvn spring-boot:run
```

Приложение будет доступно по адресу: http://localhost:8080

### Учетные записи по умолчанию

- **Admin**: admin / password
- **User**: user / password

---

## Docker

### Сборка образа

```bash
# Сборка образа приложения
docker build -t vinyl-library:latest .

# Проверка образа
docker images | grep vinyl-library
```

### Запуск через Docker Compose

```bash
# Запуск всех сервисов (PostgreSQL + PgAdmin + VinylLib)
docker-compose up -d

# Просмотр логов
docker-compose logs -f vinyl-app

# Остановка
docker-compose down

# Остановка с удалением volumes
docker-compose down -v
```

### Доступ к сервисам

- **VinylLib**: http://localhost:8080
- **PgAdmin**: http://localhost:5050 (admin@vinyl.com / admin)
- **PostgreSQL**: localhost:5432

### Управление контейнерами

```bash
# Перезапуск приложения
docker-compose restart vinyl-app

# Пересборка и запуск
docker-compose up -d --build

# Просмотр статуса
docker-compose ps

# Вход в контейнер
docker exec -it vinyl-spring-app sh
```

---

## Kubernetes

### Предварительные требования

- Kubernetes кластер (minikube, kind, GKE, EKS, AKS)
- kubectl установлен и настроен
- Docker образ собран и доступен

### Быстрый старт с Minikube

```bash
# Запуск Minikube
minikube start --cpus=4 --memory=8192

# Загрузка образа в Minikube
minikube image load vinyl-library:latest

# Применение манифестов
kubectl apply -f k8s/

# Проверка статуса
kubectl get pods
kubectl get services

# Доступ к приложению
minikube service vinyl-library-service
```

### Развертывание в облачном кластере

#### 1. Подготовка образа

```bash
# Тегирование для Docker Hub
docker tag vinyl-library:latest your-username/vinyl-library:latest

# Публикация
docker push your-username/vinyl-library:latest

# Обновление deployment.yaml
# Замените image: vinyl-library:latest на image: your-username/vinyl-library:latest
```

#### 2. Применение манифестов

```bash
# Создание namespace
kubectl create namespace vinyl-library

# Применение конфигурации
kubectl apply -f k8s/ -n vinyl-library

# Проверка развертывания
kubectl get all -n vinyl-library
```

#### 3. Настройка Ingress

```bash
# Установка Nginx Ingress Controller
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.1/deploy/static/provider/cloud/deploy.yaml

# Обновите k8s/ingress.yaml с вашим доменом
# Примените Ingress
kubectl apply -f k8s/ingress.yaml -n vinyl-library
```

### Мониторинг и отладка

```bash
# Логи приложения
kubectl logs -f deployment/vinyl-library -n vinyl-library

# Описание пода
kubectl describe pod <pod-name> -n vinyl-library

# Вход в контейнер
kubectl exec -it <pod-name> -n vinyl-library -- /bin/sh

# Проверка health endpoints
kubectl port-forward service/vinyl-library-service 8080:80 -n vinyl-library
curl http://localhost:8080/actuator/health
```

### Масштабирование

```bash
# Ручное масштабирование
kubectl scale deployment vinyl-library --replicas=3 -n vinyl-library

# Автомасштабирование
kubectl autoscale deployment vinyl-library \
  --min=2 --max=10 --cpu-percent=70 \
  -n vinyl-library

# Проверка HPA
kubectl get hpa -n vinyl-library
```

### Обновление приложения

```bash
# Rolling update
kubectl set image deployment/vinyl-library \
  vinyl-library=your-username/vinyl-library:v2 \
  -n vinyl-library

# Проверка статуса
kubectl rollout status deployment/vinyl-library -n vinyl-library

# Откат
kubectl rollout undo deployment/vinyl-library -n vinyl-library

# История обновлений
kubectl rollout history deployment/vinyl-library -n vinyl-library
```

---

## Spring Cloud Config

### Настройка Config Server

#### 1. Создание Git репозитория для конфигурации

```bash
# Создайте репозиторий с конфигурационными файлами
mkdir vinyl-library-config
cd vinyl-library-config
git init

# Создайте файлы конфигурации
# vinyl-library.yml - общая конфигурация
# vinyl-library-dev.yml - для разработки
# vinyl-library-prod.yml - для production

git add .
git commit -m "Initial config"
git remote add origin https://github.com/your-username/vinyl-library-config.git
git push -u origin main
```

#### 2. Запуск Config Server

```bash
# Через Docker
docker run -d \
  --name config-server \
  -p 8888:8888 \
  -e SPRING_CLOUD_CONFIG_SERVER_GIT_URI=https://github.com/your-username/vinyl-library-config \
  hyness/spring-cloud-config-server

# Проверка
curl http://localhost:8888/vinyl-library/default
```

#### 3. Настройка VinylLib

```bash
# Включение Config Client
export CONFIG_SERVER_ENABLED=true
export CONFIG_SERVER_URI=http://localhost:8888

# Запуск приложения
java -jar target/vinyl-library-0.0.1-SNAPSHOT.jar
```

### Обновление конфигурации без перезапуска

```bash
# Обновите конфигурацию в Git репозитории
# Затем обновите приложение
curl -X POST http://localhost:8080/actuator/refresh
```

---

## Production рекомендации

### Безопасность

1. **Secrets Management**
   - Используйте Kubernetes Secrets или внешние системы (Vault, AWS Secrets Manager)
   - Не храните пароли в ConfigMaps

2. **Network Policies**
   ```bash
   kubectl apply -f k8s/network-policy.yaml
   ```

3. **RBAC**
   - Настройте Role-Based Access Control
   - Ограничьте доступ к namespace

### Мониторинг

1. **Prometheus + Grafana**
   ```bash
   # Установка Prometheus Operator
   kubectl apply -f https://raw.githubusercontent.com/prometheus-operator/prometheus-operator/main/bundle.yaml
   
   # Настройка ServiceMonitor для VinylLib
   kubectl apply -f k8s/servicemonitor.yaml
   ```

2. **Логирование**
   - ELK Stack (Elasticsearch, Logstash, Kibana)
   - Или Loki + Grafana

### Backup

1. **PostgreSQL**
   ```bash
   # Создание backup
   kubectl exec -it <postgres-pod> -- pg_dump -U postgres vinyldb > backup.sql
   
   # Восстановление
   kubectl exec -i <postgres-pod> -- psql -U postgres vinyldb < backup.sql
   ```

2. **Автоматические backup через CronJob**
   ```bash
   kubectl apply -f k8s/backup-cronjob.yaml
   ```

### High Availability

1. **Database**
   - Используйте PostgreSQL Operator для HA
   - Настройте репликацию

2. **Application**
   - Минимум 2 реплики
   - PodDisruptionBudget
   - Anti-affinity правила

### Performance

1. **Resource Limits**
   ```yaml
   resources:
     requests:
       memory: "512Mi"
       cpu: "250m"
     limits:
       memory: "1Gi"
       cpu: "500m"
   ```

2. **Connection Pooling**
   - Настройте HikariCP в application.yml
   - Оптимизируйте размер пула

3. **Caching**
   - Добавьте Redis для кэширования
   - Используйте Spring Cache

---

## Troubleshooting

### Приложение не запускается

```bash
# Проверка логов
kubectl logs <pod-name>

# Проверка событий
kubectl get events --sort-by='.lastTimestamp'

# Проверка конфигурации
kubectl describe pod <pod-name>
```

### База данных недоступна

```bash
# Проверка PostgreSQL
kubectl exec -it <postgres-pod> -- psql -U postgres -c "\l"

# Проверка сетевого подключения
kubectl exec -it <app-pod> -- nc -zv postgres-service 5432
```

### Проблемы с памятью

```bash
# Увеличение лимитов
kubectl set resources deployment vinyl-library \
  --limits=memory=2Gi,cpu=1000m \
  --requests=memory=1Gi,cpu=500m
```

---

## Полезные команды

```bash
# Просмотр всех ресурсов
kubectl get all -n vinyl-library

# Просмотр использования ресурсов
kubectl top pods -n vinyl-library
kubectl top nodes

# Экспорт конфигурации
kubectl get deployment vinyl-library -o yaml > deployment-backup.yaml

# Применение изменений
kubectl apply -f deployment-backup.yaml

# Удаление всех ресурсов
kubectl delete namespace vinyl-library
```

---

## Контакты и поддержка

- **Email**: vivlev8721@mail.ru
- **GitHub**: https://github.com/your-username/vinyl-library
- **Documentation**: См. README.md и k8s/README.md
