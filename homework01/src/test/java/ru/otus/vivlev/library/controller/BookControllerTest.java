package ru.otus.vivlev.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.repository.BookRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("The BookController class")
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() throws Exception {
        Book book = bookRepository.findById(1L).orElseThrow();
        String expectedResponse = objectMapper.writeValueAsString(book);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/book/1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() throws Exception {
        List<Book> books = bookRepository.findAll();
        String expectedResponse = objectMapper.writeValueAsString(books);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/book")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @DisplayName("is checking saveBook method.")
    @Test
    void checkingSave() throws Exception {
        // Берём существующую книгу как шаблон для тела запроса
        Book book = bookRepository.findById(1L).orElseThrow();
        String requestBody = objectMapper.writeValueAsString(book);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/book")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        // Ответ должен совпасть с тем, что вернёт контроллер после сохранения.
        // Чтобы не завязываться на id, можно просто проверить, что JSON парсится
        // и структура корректна. Здесь оставим сравнение строк для простоты:
        // actual == requestBody с учётом возможного изменения id.
        // Проще: убедимся, что поля title/author не пустые.
        Book saved = objectMapper.readValue(actualResponse, Book.class);
        assertThat(saved.getTitle()).isEqualTo(book.getTitle());
        assertThat(saved.getAuthor().getId()).isEqualTo(book.getAuthor().getId());
    }

    @DisplayName("is checking updateBook method.")
    @DirtiesContext(methodMode = BEFORE_METHOD)
    @Test
    void checkingUpdate() throws Exception {
        Book book = bookRepository.findById(1L).orElseThrow();
        String requestBody = objectMapper.writeValueAsString(book);

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/book/1")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Book updated = objectMapper.readValue(actualResponse, Book.class);
        assertThat(updated.getId()).isEqualTo(1L);
        assertThat(updated.getTitle()).isEqualTo(book.getTitle());
        assertThat(updated.getAuthor().getId()).isEqualTo(book.getAuthor().getId());
    }

    @DisplayName("is checking deleteBook method.")
    @Test
    void checkingDelete() throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/book/1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        Integer status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);
    }
}