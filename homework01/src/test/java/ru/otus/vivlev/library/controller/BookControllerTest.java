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
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final Map<String, String> tokenMap = new HashMap<>();

    public static final String USER = "user";
    public static final String USER_PASS = "user";
    public static final long AUTHOR_ID_1 = 1L;
    public static final long AUTHOR_ID_2 = 2L;
    public static final String AUTHOR_1 = "Гаррисон, Г.";
    public static final String AUTHOR_2 = "Перумов, Н.";
    public static final long GENRE_ID_1 = 1L;
    public static final long GENRE_ID_2 = 2L;
    public static final String GENRE_1 = "Фантастика";
    public static final String GENRE_2 = "Фентези";
    public static final long BOOK_ID_1 = 1L;
    public static final long BOOK_ID_2 = 2L;
    public static final long BOOK_ID_3 = 3L;
    public static final String BOOK_1 = "Стальная крыса идет на войну";
    public static final String BOOK_2 = "Стальная крыса спасает мир";
    public static final String BOOK_3 = "Не время для драконов";

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() throws Exception {
        Author author = new Author(AUTHOR_ID_1, AUTHOR_1);
        Genre genre = new Genre(GENRE_ID_1, GENRE_1);
        Book book = new Book(BOOK_ID_1, BOOK_1, author, List.of(genre), new ArrayList<>());

        String expectedResponse = objectMapper.writeValueAsString(book);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/book/1")
                .contentType("application/json")
                .header("Authorization", TokenUtils.getToken(tokenMap, mockMvc, USER, USER_PASS))
                .content(expectedResponse))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() throws Exception {
        Author author1 = new Author(AUTHOR_ID_1, AUTHOR_1);
        Author author2 = new Author(AUTHOR_ID_2, AUTHOR_2);
        Genre genre1 = new Genre(GENRE_ID_1, GENRE_1);
        Genre genre2 = new Genre(GENRE_ID_2, GENRE_2);
        Book book1 = new Book(BOOK_ID_1, BOOK_1, author1, List.of(genre1), new ArrayList<>());
        Book book2 = new Book(BOOK_ID_2, BOOK_2, author1, List.of(genre1), new ArrayList<>());
        Book book3 = new Book(BOOK_ID_3, BOOK_3, author2, List.of(genre2), new ArrayList<>());
        List<Book> books = List.of(book1, book2, book3);

        String expectedResponse = objectMapper.writeValueAsString(books);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/book")
                .contentType("application/json")
                .header("Authorization", TokenUtils.getToken(tokenMap, mockMvc, USER, USER_PASS))
                .content(expectedResponse))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @DisplayName("is checking saveBook method.")
    @Test
    void checkingSave() throws Exception {
        Author author = new Author(AUTHOR_ID_1, AUTHOR_1);
        Genre genre = new Genre(GENRE_ID_1, GENRE_1);
        Book book = new Book(BOOK_ID_1, BOOK_1, author, List.of(genre), new ArrayList<>());

        String expectedResponse = objectMapper.writeValueAsString(book);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/book")
                .contentType("application/json")
                .header("Authorization", TokenUtils.getToken(tokenMap, mockMvc, USER, USER_PASS))
                .content(expectedResponse))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @DisplayName("is checking updateBook method.")
    @DirtiesContext(methodMode = BEFORE_METHOD)
    @Test
    void checkingUpdate() throws Exception {
        Author author = new Author(AUTHOR_ID_1, AUTHOR_1);
        Genre genre = new Genre(GENRE_ID_1, GENRE_1);
        Book book = new Book(BOOK_ID_1, BOOK_1, author, List.of(genre), new ArrayList<>());

        String expectedResponse = objectMapper.writeValueAsString(book);

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/book/1")
                .contentType("application/json")
                .header("Authorization", TokenUtils.getToken(tokenMap, mockMvc, USER, USER_PASS))
                .content(expectedResponse))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @DisplayName("is checking deleteBook method.")
    @Test
    void checkingDelete() throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/book/1")
                .contentType("application/json")
                .header("Authorization", TokenUtils.getToken(tokenMap, mockMvc, USER, USER_PASS)))
                .andExpect(status().isNoContent())
                .andReturn();

        Integer status = mvcResult.getResponse().getStatus();

        assertThat(status).isEqualTo(204);
    }
}
