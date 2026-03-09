# Kubernetes Deployment для VinylLib

## Предварительные требования

- Kubernetes кластер (minikube, kind, или облачный провайдер)
- kubectl установлен и настроен
- Docker образ приложения собран

## Сборка Docker образа

```bash
# Сборка образа
docker build -t vinyl-library:latest .

# Для minikube - загрузка образа в minikube
minikube image load vinyl-library:latest

# Для Docker Hub
docker tag vinyl-library:latest your-username/vinyl-library:latest
docker push your-username/vinyl-library:latest
```

## Развертывание в Kubernetes

### 1. Создание namespace (опционально)

```bash
kubectl create namespace vinyl-library
```

### 2. Применение манифестов

```bash
# Применить все манифесты
kubectl apply -f k8s/

# Или по отдельности в правильном порядке:
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
```

### 3. Проверка статуса

```bash
# Проверить поды
kubectl get pods

# Проверить сервисы
kubectl get services

# Проверить логи приложения
kubectl logs -f deployment/vinyl-library

# Проверить события
kubectl get events --sort-by='.lastTimestamp'
```

### 4. Доступ к приложению

#### Через LoadBalancer (облачный провайдер)
```bash
kubectl get service vinyl-library-service
# Используйте EXTERNAL-IP для доступа
```

#### Через NodePort (локальный кластер)
```bash
kubectl port-forward service/vinyl-library-service 8080:80
# Откройте http://localhost:8080
```

#### Через Ingress
```bash
# Убедитесь, что Ingress Controller установлен
kubectl get ingress
# Настройте DNS для vinyl-library.example.com
```

## Масштабирование

```bash
# Увеличить количество реплик
kubectl scale deployment vinyl-library --replicas=3

# Автомасштабирование
kubectl autoscale deployment vinyl-library --min=2 --max=5 --cpu-percent=80
```

## Обновление приложения

```bash
# Обновить образ
kubectl set image deployment/vinyl-library vinyl-library=vinyl-library:v2

# Откатить обновление
kubectl rollout undo deployment/vinyl-library

# Проверить статус обновления
kubectl rollout status deployment/vinyl-library
```

## Мониторинг

```bash
# Метрики через Actuator
kubectl port-forward service/vinyl-library-service 8080:80
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics

# Prometheus метрики
curl http://localhost:8080/actuator/prometheus
```

## Удаление

```bash
# Удалить все ресурсы
kubectl delete -f k8s/

# Или удалить по отдельности
kubectl delete deployment vinyl-library
kubectl delete service vinyl-library-service
kubectl delete configmap vinyl-library-config
kubectl delete secret vinyl-library-secrets
```

## Troubleshooting

```bash
# Проверить описание пода
kubectl describe pod <pod-name>

# Войти в контейнер
kubectl exec -it <pod-name> -- /bin/sh

# Проверить логи PostgreSQL
kubectl logs <postgres-pod-name>

# Проверить переменные окружения
kubectl exec <pod-name> -- env
```

## Production рекомендации

1. **Secrets**: Используйте внешние системы управления секретами (HashiCorp Vault, AWS Secrets Manager)
2. **Persistent Storage**: Настройте StorageClass для production
3. **Resource Limits**: Настройте правильные лимиты CPU/Memory
4. **Health Checks**: Настройте liveness и readiness probes
5. **Monitoring**: Интегрируйте Prometheus и Grafana
6. **Logging**: Настройте централизованное логирование (ELK, Loki)
7. **Backup**: Настройте регулярные бэкапы PostgreSQL
8. **Security**: Используйте NetworkPolicies и PodSecurityPolicies
