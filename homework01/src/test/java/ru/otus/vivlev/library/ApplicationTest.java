package ru.otus.vivlev.library;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Тест запуска Spring Boot приложения")
class ApplicationTest {

    @Test
    @DisplayName("должен успешно загружать контекст приложения")
    void contextLoads() {
        // Тест проверяет, что Spring контекст загружается без ошибок
    }
}
