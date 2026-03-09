# Решение проблемы с Spring Batch зависимостями

## Проблема
```
java: package org.springframework.batch.item does not exist
java: cannot find symbol - class ItemProcessor
```

## Причина
Maven не загрузил зависимости Spring Batch, хотя они указаны в `pom.xml`.

## Решения

### Вариант 1: Через IntelliJ IDEA (рекомендуется)

1. **Обновить Maven зависимости:**
   - Откройте панель Maven (View → Tool Windows → Maven)
   - Нажмите кнопку "Reload All Maven Projects" (иконка с круговыми стрелками)
   - Или: правый клик на `pom.xml` → Maven → Reload Project

2. **Если не помогло, очистите кэш:**
   - File → Invalidate Caches / Restart
   - Выберите "Invalidate and Restart"

### Вариант 2: Через командную строку

Если Maven установлен:
```bash
cd C:\WORK\DSHTRANIN\2025-10-otus-spring-Ivlev\homework01
mvn clean install -DskipTests
```

Если Maven не установлен, используйте Maven Wrapper:
```bash
cd C:\WORK\DSHTRANIN\2025-10-otus-spring-Ivlev\homework01
.\mvnw.cmd clean install -DskipTests
```

### Вариант 3: Установить Maven

1. Скачайте Maven: https://maven.apache.org/download.cgi
2. Распакуйте в `C:\Program Files\Apache\maven`
3. Добавьте в PATH:
   - Системные переменные → Path → Добавить: `C:\Program Files\Apache\maven\bin`
4. Проверьте: `mvn -version`

### Вариант 4: Через IDE (Eclipse/VSCode)

**Eclipse:**
- Правый клик на проект → Maven → Update Project
- Поставьте галочку "Force Update of Snapshots/Releases"
- OK

**VSCode:**
- Откройте Command Palette (Ctrl+Shift+P)
- Введите "Java: Clean Java Language Server Workspace"
- Перезагрузите окно

## Проверка решения

После обновления зависимостей проверьте:

1. **В IDE должны появиться библиотеки:**
   - External Libraries → Maven: org.springframework.batch:spring-batch-core
   - External Libraries → Maven: org.springframework.batch:spring-batch-infrastructure

2. **Ошибки компиляции должны исчезнуть:**
   - `AlbumItemProcessor.java` - без ошибок
   - `BatchConfiguration.java` - без ошибок

3. **Попробуйте запустить Application.java**

## Если проблема сохраняется

### Проверьте версию Spring Boot

В `pom.xml` должна быть версия Spring Boot 2.4.4:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.4.4</version>
</parent>
```

### Проверьте наличие зависимости

В `pom.xml` должна быть:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-batch</artifactId>
</dependency>
```

### Удалите локальный Maven репозиторий

Если зависимости повреждены:
```bash
# Windows
rmdir /s /q %USERPROFILE%\.m2\repository\org\springframework\batch

# Затем обновите проект
```

## Альтернативное решение (временное)

Если ничего не помогает, можно временно закомментировать Spring Batch компоненты:

1. Закомментируйте в `Application.java` аннотацию `@EnableBatchProcessing` (если есть)
2. Переименуйте папку `batch/` в `batch_disabled/`
3. Запустите приложение без Batch функциональности

**Важно:** Это временное решение. Spring Batch - важная часть проекта для импорта данных.

## Дополнительная диагностика

Проверьте логи Maven при загрузке зависимостей:
```bash
mvn dependency:tree -Dverbose
```

Это покажет все зависимости и возможные конфликты.
