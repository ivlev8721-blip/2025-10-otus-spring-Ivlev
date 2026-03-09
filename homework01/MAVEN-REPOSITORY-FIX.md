# Решение проблемы с Maven репозиториями

## Проблема
```
Cannot resolve org.springframework.boot:spring-boot-starter-batch:2.4.4
Cannot resolve org.springframework.batch:spring-batch-test:4.3.2
Cannot resolve org.springframework.boot:spring-boot-starter-websocket:2.4.4
```

## Причины
1. Блокировка доступа к Maven Central (repo.maven.apache.org)
2. Проблемы с сетью или прокси
3. Поврежденный локальный Maven репозиторий

## Решения

### ✅ Решение 1: Добавлены альтернативные репозитории

Я добавил в `pom.xml` альтернативные репозитории:
- Maven Central (repo1.maven.org)
- Spring Milestones
- Spring Releases

Теперь попробуйте обновить зависимости в IDE:
- IntelliJ IDEA: Maven → Reload All Maven Projects
- Eclipse: Правый клик на проект → Maven → Update Project

### 🔧 Решение 2: Очистить локальный репозиторий

Если зависимости повреждены, удалите их:

**Windows:**
```powershell
# Удалить Spring Boot зависимости
Remove-Item -Recurse -Force "$env:USERPROFILE\.m2\repository\org\springframework\boot"

# Удалить Spring Batch зависимости
Remove-Item -Recurse -Force "$env:USERPROFILE\.m2\repository\org\springframework\batch"

# Затем обновите проект в IDE
```

**Linux/Mac:**
```bash
rm -rf ~/.m2/repository/org/springframework/boot
rm -rf ~/.m2/repository/org/springframework/batch
```

### 🌐 Решение 3: Использовать зеркало (для РФ)

Если доступ к Maven Central заблокирован, создайте файл `settings.xml`:

**Расположение:**
- Windows: `C:\Users\<ваш_пользователь>\.m2\settings.xml`
- Linux/Mac: `~/.m2/settings.xml`

**Содержимое:**
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
    
    <mirrors>
        <!-- Зеркало Maven Central через Aliyun (Китай) -->
        <mirror>
            <id>aliyun-central</id>
            <mirrorOf>central</mirrorOf>
            <name>Aliyun Maven Central</name>
            <url>https://maven.aliyun.com/repository/central</url>
        </mirror>
        
        <!-- Зеркало Spring через Aliyun -->
        <mirror>
            <id>aliyun-spring</id>
            <mirrorOf>spring-*</mirrorOf>
            <name>Aliyun Spring</name>
            <url>https://maven.aliyun.com/repository/spring</url>
        </mirror>
    </mirrors>
    
    <profiles>
        <profile>
            <id>default</id>
            <repositories>
                <repository>
                    <id>central</id>
                    <url>https://maven.aliyun.com/repository/central</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                </repository>
                <repository>
                    <id>spring</id>
                    <url>https://maven.aliyun.com/repository/spring</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>
    
    <activeProfiles>
        <activeProfile>default</activeProfile>
    </activeProfiles>
</settings>
```

### 🔐 Решение 4: Настроить прокси (если используется)

Добавьте в `settings.xml`:

```xml
<settings>
    <proxies>
        <proxy>
            <id>myproxy</id>
            <active>true</active>
            <protocol>http</protocol>
            <host>proxy.company.com</host>
            <port>8080</port>
            <username>proxyuser</username>
            <password>proxypass</password>
            <nonProxyHosts>localhost|127.0.0.1</nonProxyHosts>
        </proxy>
    </proxies>
</settings>
```

### 🚀 Решение 5: Использовать VPN

Если репозитории заблокированы в вашей стране:
1. Включите VPN
2. Очистите Maven кэш (см. Решение 2)
3. Обновите проект в IDE

### 📦 Решение 6: Скачать зависимости вручную (крайний случай)

Если ничего не помогает, можно скачать JAR файлы вручную:

1. Перейдите на https://repo1.maven.org/maven2/
2. Найдите нужные зависимости:
   - `org/springframework/boot/spring-boot-starter-batch/2.4.4/`
   - `org/springframework/batch/spring-batch-test/4.3.2/`
   - `org/springframework/boot/spring-boot-starter-websocket/2.4.4/`
3. Скачайте `.jar` файлы
4. Поместите в `~/.m2/repository/` в соответствующие папки

**Но это очень трудоемко, так как нужно скачать все транзитивные зависимости!**

## Проверка решения

После применения любого из решений:

1. **Обновите Maven проект:**
   - IntelliJ IDEA: Maven → Reload All Maven Projects
   - Eclipse: Правый клик → Maven → Update Project

2. **Проверьте логи Maven:**
   Посмотрите в консоль IDE, должны быть сообщения о скачивании:
   ```
   Downloading from central: https://repo1.maven.org/maven2/...
   Downloaded from central: ...
   ```

3. **Проверьте External Libraries:**
   Должны появиться:
   - spring-boot-starter-batch-2.4.4.jar
   - spring-batch-core-4.3.2.jar
   - spring-boot-starter-websocket-2.4.4.jar

## Диагностика

Проверьте доступность репозиториев:

**PowerShell:**
```powershell
# Проверка Maven Central
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/" -UseBasicParsing

# Проверка Aliyun Mirror
Invoke-WebRequest -Uri "https://maven.aliyun.com/repository/central" -UseBasicParsing
```

**Командная строка:**
```bash
curl -I https://repo1.maven.org/maven2/
curl -I https://maven.aliyun.com/repository/central
```

Если получаете ошибки подключения - проблема в сети или блокировке.

## Рекомендуемый порядок действий

1. ✅ Обновите проект в IDE (уже добавлены репозитории в pom.xml)
2. Если не помогло → Очистите локальный репозиторий (Решение 2)
3. Если не помогло → Создайте settings.xml с зеркалом (Решение 3)
4. Если не помогло → Используйте VPN (Решение 5)
5. Если не помогло → Настройте прокси (Решение 4)

## Альтернатива: Использовать H2 вместо PostgreSQL временно

Если проблема только с Batch и WebSocket, можно временно использовать H2:

1. Закомментируйте PostgreSQL зависимость
2. Раскомментируйте H2
3. Измените `application.yml` на H2
4. Запустите приложение

Но это **временное решение** - для полноценной работы нужны все зависимости.
