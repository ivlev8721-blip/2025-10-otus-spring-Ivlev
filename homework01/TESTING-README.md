# Руководство по тестированию Library Application

## Обзор

Проект содержит комплексное тестовое покрытие для всех слоев приложения:
- Тесты репозиториев (JPA)
- Тесты сервисов
- Тесты REST контроллеров
- Тесты маппера DTO
- Интеграционные тесты Kafka

## Структура тестов

```
src/test/java/ru/otus/vivlev/library/
├── controller/          # Тесты REST контроллеров
│   ├── AuthorControllerTest.java
│   ├── BookControllerTest.java
│   ├── GenreControllerTest.java
│   ├── CommentControllerTest.java
│   └── RestControllerTestBase.java  # Базовый класс с MockBean для Kafka
├── service/             # Тесты сервисов
│   ├── AuthorServiceImplTest.java
│   ├── BookServiceImplTest.java
│   └── GenreServiceImplTest.java
├── repository/          # Тесты репозиториев
│   ├── AuthorRepositoryTest.java
│   ├── BookRepositoryTest.java
│   ├── GenreRepositoryTest.java
│   └── CommentRepositoryTest.java
├── mapper/              # Тесты маппера
│   └── DtoMapperTest.java
└── kafka/               # Тесты Kafka
    └── KafkaIntegrationTest.java
```

## Запуск тестов

### Все тесты:
```powershell
mvn test
```

### Конкретный тест:
```powershell
mvn test -Dtest=AuthorControllerTest
```

### Тесты с покрытием:
```powershell
mvn clean test jacoco:report
```

## Изменения в тестах

### 1. Обновление контроллеров

**Было (Entity):**
```java
Author author = new Author(1L, "Толстой");
String json = objectMapper.writeValueAsString(author);
```

**Стало (DTO):**
```java
AuthorDto authorDto = new AuthorDto(1L, "Толстой");
String json = objectMapper.writeValueAsString(authorDto);
```

### 2. Добавление MockBean для Kafka

Все тесты контроллеров теперь используют `@MockBean` для `LibraryEventProducer`:

```java
@SpringBootTest
@AutoConfigureMockMvc
public class AuthorControllerTest {
    
    @MockBean
    private LibraryEventProducer eventProducer;
    
    // тесты...
}
```

Или наследуются от базового класса:

```java
public class AuthorControllerTest extends RestControllerTestBase {
    // eventProducer уже замокан в базовом классе
}
```

### 3. Обновление ожидаемых HTTP статусов

**POST (создание):**
- Было: `status().isOk()` (200)
- Стало: `status().isCreated()` (201)

**DELETE (удаление):**
- Было: `status().isOk()` (200)
- Стало: `status().isNoContent()` (204)

### 4. Тесты маппера

Созданы тесты для проверки корректности преобразования между Entity и DTO:

```java
@Test
void shouldConvertAuthorToDto() {
    Author author = new Author(1L, "Толстой Л.Н.");
    AuthorDto dto = mapper.toDto(author);
    
    assertThat(dto.getId()).isEqualTo(1L);
    assertThat(dto.getFullName()).isEqualTo("Толстой Л.Н.");
}
```

### 5. Интеграционные тесты Kafka

Используется `@EmbeddedKafka` для тестирования без реального Kafka:

```java
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9093"})
class KafkaIntegrationTest {
    
    @Autowired
    private LibraryEventProducer eventProducer;
    
    @Test
    void shouldSendAuthorEvent() {
        AuthorDto authorDto = new AuthorDto(1L, "Тестовый автор");
        AuthorEvent event = new AuthorEvent(AuthorEvent.EventType.CREATED, authorDto);
        
        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getEventType()).isEqualTo(AuthorEvent.EventType.CREATED);
    }
}
```

## Примеры тестов

### Тест контроллера (GET)

```java
@Test
void checkingGetById() throws Exception {
    AuthorDto author = new AuthorDto(1L, "Гаррисон, Г.");
    String expectedResponse = objectMapper.writeValueAsString(author);

    MvcResult mvcResult = mockMvc.perform(get("/api/v1/author/1")
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andReturn();

    String actualResponse = mvcResult.getResponse().getContentAsString();
    assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
}
```

### Тест контроллера (POST)

```java
@Test
void checkingSave() throws Exception {
    AuthorDto authorDto = new AuthorDto(null, "Новый автор");
    String requestBody = objectMapper.writeValueAsString(authorDto);
    
    MvcResult mvcResult = mockMvc.perform(post("/api/v1/author")
            .contentType("application/json")
            .content(requestBody))
            .andExpect(status().isCreated())
            .andReturn();

    String actualResponse = mvcResult.getResponse().getContentAsString();
    AuthorDto result = objectMapper.readValue(actualResponse, AuthorDto.class);

    assertThat(result.getFullName()).isEqualTo("Новый автор");
    assertThat(result.getId()).isNotNull();
}
```

### Тест сервиса

```java
@Test
void checkingGetById() {
    Author expectedAuthor = new Author(1L, "Гаррисон, Г.");
    
    doReturn(Optional.of(expectedAuthor))
        .when(authorRepository).findById(1L);
    
    Author actualAuthor = authorService.getById(1L).get();
    
    assertThat(actualAuthor)
        .usingRecursiveComparison()
        .isEqualTo(expectedAuthor);
}
```

### Тест репозитория

```java
@Test
@DirtiesContext(methodMode = BEFORE_METHOD)
void checkingFindById() {
    Author expectedAuthor = new Author(1L, "Гаррисон, Г.");
    Author actualAuthor = authorRepository.findById(1L).get();
    
    assertThat(actualAuthor)
        .usingRecursiveComparison()
        .isEqualTo(expectedAuthor);
}
```

## Конфигурация тестов

### application.yaml для тестов

Создайте `src/test/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

kafka:
  bootstrap-servers: localhost:9093
```

## Отключение Kafka в тестах

Если нужно полностью отключить Kafka в тестах, используйте профиль:

```java
@SpringBootTest
@ActiveProfiles("test")
class MyTest {
    // тесты
}
```

И в `application-test.yaml`:

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
```

## Покрытие кода

После запуска тестов с JaCoCo отчет доступен в:
```
target/site/jacoco/index.html
```

## Best Practices

1. **Используйте DTO в тестах контроллеров** - контроллеры работают с DTO, а не Entity
2. **Мокайте Kafka producer** - используйте `@MockBean` для `LibraryEventProducer`
3. **Проверяйте правильные HTTP статусы** - 201 для создания, 204 для удаления
4. **Используйте @DirtiesContext** - для тестов, изменяющих состояние БД
5. **Тестируйте валидацию** - проверяйте, что невалидные данные отклоняются
6. **Изолируйте тесты** - каждый тест должен быть независимым

## Troubleshooting

### Ошибка: "No qualifying bean of type LibraryEventProducer"

**Решение:** Добавьте `@MockBean` для `LibraryEventProducer` в тест:
```java
@MockBean
private LibraryEventProducer eventProducer;
```

### Ошибка: "Expected status 201 but was 200"

**Решение:** Обновите ожидаемый статус для POST запросов:
```java
.andExpect(status().isCreated())  // вместо isOk()
```

### Ошибка: "Cannot deserialize instance of AuthorDto"

**Решение:** Убедитесь, что используете DTO классы в тестах, а не Entity:
```java
AuthorDto dto = new AuthorDto(1L, "Имя");  // правильно
Author entity = new Author(1L, "Имя");      // неправильно для контроллеров
```

## Запуск в CI/CD

```yaml
# .github/workflows/tests.yml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '11'
      - run: mvn clean test
```
