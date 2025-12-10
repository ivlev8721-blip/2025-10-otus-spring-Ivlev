# OTUS Spring (homework01) — Spring JDBC + Spring Shell + H2

## Требования
- JDK 11+
- (Опционально) Maven/Gradle. В репо есть Maven Wrapper `mvnw.cmd`.
- Доступ в интернет или настроенный корпоративный репозиторий/сертификаты для скачивания зависимостей.

## Запуск приложения
```bash
cd homework01
.\mvnw.cmd spring-boot:run
```

После старта доступна консоль Spring Shell. Используйте команды:
- `books` или `b` — список всех книг
- `add-book "Title" "Author" "Genre1, Genre2"` или `add-b` — добавить книгу
- `update-book <id> "Title" ["Author"] ["Genre1, Genre2"]` или `upd-b` — обновить книгу
- `delete-book <id>` или `del-b` — удалить книгу

## Схема и данные
- `src/main/resources/schema.sql` — создаёт таблицы AUTHOR, GENRE, BOOK, BOOK_GENRE (FK автор/жанры, отношение многие-ко-многим).
- `src/main/resources/data.sql` — начальные данные (авторы, жанры, книги).

## Сборка jar
```bash
cd homework01
.\mvnw.cmd clean package
java -jar target/hw-06-0.0.1-SNAPSHOT.jar
```

## Тесты
```bash
cd homework01
.\mvnw.cmd test
```

Если меняли `schema.sql`, обязательно запускать `clean`, чтобы скрипты обновились в `target/test-classes`.

## Технологии
- Spring JDBC с NamedParameterJdbcTemplate
- Spring Shell для консольного интерфейса
- H2 Database (in-memory)
- Автоматическая инициализация БД через spring-boot-starter-jdbc

