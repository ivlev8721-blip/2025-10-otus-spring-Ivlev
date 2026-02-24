# Домашнее задание: REST API приложения "Библиотека".


## Функциональность

- Работа с сущностями:
    - Автор (`Author`)
    - Жанр (`Genre`)
    - Книга (`Book`)
    - Комментарий (`Comment`)
- REST‑эндпоинты:
    - `/api/v1/book` — операции с книгами
    - `/api/v1/author` — операции с авторами
    - `/api/v1/genre` — операции с жанрами
    - `/api/v1/comment` — операции с комментариями

- Сценарии тестипрования описаны в  spring-shell.log

## Технологии

- Java 11
- Spring Boot 2.4.4
- Spring Web, Spring Data JPA
- H2 Database
- JUnit 5, Spring Boot Test, AssertJ

## Запуск
### Приложение

```bash

mvn spring-boot:run
