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
import ru.otus.vivlev.library.domain.Comment;
import ru.otus.vivlev.library.domain.Genre;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("The CommentController class")
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public static final long AUTHOR_ID_1 = 1L;
    public static final String AUTHOR_1 = "Гаррисон, Г.";
    public static final long GENRE_ID_1 = 1L;
    public static final String GENRE_1 = "Фантастика";
    public static final long COMMENT_ID_1 = 1L;
    public static final long COMMENT_ID_2 = 2L;
    public static final long BOOK_ID_1 = 1L;
    public static final String COMMENT_1 = "Классная книга, рекомендую!";
    public static final String COMMENT_2 = "Прочитал в один заход!";
    public static final String AUTHOR = "ADMIN";
    public static final String BOOK_1 = "Стальная крыса идет на войну";

    @DisplayName("is checking getById method.")
    @DirtiesContext(methodMode = BEFORE_METHOD)
    @Test
    void checkingGetById() throws Exception {
        Author author = new Author(AUTHOR_ID_1, AUTHOR_1);
        Genre genre = new Genre(GENRE_ID_1, GENRE_1);
        Book book = new Book(BOOK_ID_1, BOOK_1,
                author, new ArrayList<>(List.of(genre)), new ArrayList<>());
        Comment comment = new Comment(COMMENT_ID_1, AUTHOR, COMMENT_1, book);

        String expectedResponse = objectMapper.writeValueAsString(comment);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/comment/1")
                        .contentType("application/json")
                        .content(expectedResponse))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @DisplayName("is checking getAll method.")
    @DirtiesContext(methodMode = BEFORE_METHOD)
    @Test
    void checkingGetAllByBookId() throws Exception {
        Author author = new Author(AUTHOR_ID_1, AUTHOR_1);
        Genre genre = new Genre(GENRE_ID_1, GENRE_1);
        Book book = new Book(BOOK_ID_1, BOOK_1, author, new ArrayList<>(List.of(genre)), new ArrayList<>());
        Comment comment1 = new Comment(COMMENT_ID_1, AUTHOR, COMMENT_1, book);
        Comment comment2 = new Comment(COMMENT_ID_2, AUTHOR, COMMENT_2, book);

        String expectedResponse = objectMapper.writeValueAsString(List.of(comment1, comment2));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/comment/book/1")
                        .contentType("application/json")
                        .content(expectedResponse))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @DisplayName("is checking saveComment method.")
    @Test
    void checkingSave() throws Exception {
        Author author = new Author(AUTHOR_ID_1, AUTHOR_1);
        Genre genre = new Genre(GENRE_ID_1, GENRE_1);
        Book book = new Book(BOOK_ID_1, BOOK_1, author, new ArrayList<>(List.of(genre)), new ArrayList<>());
        Comment comment = new Comment(COMMENT_ID_1, AUTHOR, COMMENT_1, book);

        String expectedResponse = objectMapper.writeValueAsString(comment);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/comment")
                        .contentType("application/json")
                        .content(expectedResponse))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }
}