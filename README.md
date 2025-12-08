# OTUS Spring (homework01) — JPA + Spring Shell + H2

## Требования
- JDK 11+
- (Опционально) Maven/Gradle. В репо есть Maven Wrapper `mvnw.cmd`.
- Доступ в интернет или настроенный корпоративный репозиторий/сертификаты для скачивания зависимостей.

## Запуск приложения
cd homework01
.\mvnw.cmd spring-boot:runПосле старта доступна консоль Spring Shell: авторизуйтесь командой `auth <login>`, затем используйте:
- `books` — список книг
- `add-book "Title" "Author" "Genre1, Genre2"` — добавить
- `update-book <id> "Title" ["Author"] ["Genre1, Genre2"]` — обновить
- `delete-book <id>` — удалить
- `create-comment <bookId> "text"` — добавить комментарий
- `get-all-comment-by-book-id <bookId>` — комментарии книги

## Схема и данные
- `src/main/resources/schema.sql` — создаёт таблицы AUTHOR, GENRE, BOOK, BOOK_GENRE, COMMENT (FK автор/жанры/комментарии, ON DELETE CASCADE для комментариев).
- `src/main/resources/data.sql` — начальные данные (авторы, жанры, книги, комментарии).

## Сборка jar
cd homework01
.\mvnw.cmd clean package
java -jar target/hw-07-0.0.1-SNAPSHOT.jar## Тесты
cd homework01
.\mvnw.cmd testЕсли меняли `schema.sql`, обязательно запускать `clean`, чтобы скрипты обновились в `target/test-classes`.

## Частые проблемы
- `PKIX path building failed` при скачивании зависимостей: нужно импортировать корпоративный корневой сертификат в truststore JDK или использовать внутреннее зеркало Maven (настроить `~/.m2/settings.xml` / `maven-wrapper.properties`).
- Ошибки дропа таблиц в H2: проверьте порядок `DROP TABLE` (сначала COMMENT, BOOK_GENRE, потом BOOK, GENRE, AUTHOR) и выполните `mvn clean test`.