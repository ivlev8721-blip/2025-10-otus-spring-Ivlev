package ru.otus.vivlev.library.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Comment;
import ru.otus.vivlev.library.domain.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static ru.otus.vivlev.library.controller.BookControllerTest.BOOK_1;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("The ControllersTest class")
public class ControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public static final String USER = "user";
    public static final String USER_PASS = "user";
    public static final String ADMIN = "admin";
    public static final String ADMIN_PASS = "admin";
    public static final long AUTHOR_ID_1 = 1L;
    public static final String AUTHOR_1 = "Гаррисон, Г.";
    public static final long GENRE_ID_1 = 1L;
    public static final String GENRE_1 = "Фантастика";
    public static final long BOOK_ID_1 = 1L;
    public static final long COMMENT_ID_1 = 1L;
    public static final String COMMENT_1 = "Классная книга, рекомендую!";

    @DirtiesContext(methodMode = BEFORE_METHOD)
    @DisplayName("is checking users auth.")
    @ParameterizedTest
    @MethodSource("ru.otus.vivlev.library.controller.DataForAllControllerTest#getUrlsForUser")
    void checkingUsersAuth(Map<String, Map<String, ResultMatcher>> urlsForUser) throws Exception {

        for (Map.Entry<String, Map<String, ResultMatcher>> mapEntry : urlsForUser.entrySet()) {

            for (Map.Entry<String, ResultMatcher> map : mapEntry.getValue().entrySet()) {

                mockMvc.perform(getMethod(map.getKey(), mapEntry.getKey())
                        .contentType("application/json")
                        .header("Authorization", TokenUtils.getToken(mockMvc, USER, USER_PASS))
                        .content(getContent(mapEntry.getKey())))
                        .andExpect(map.getValue())
                        .andReturn();
            }
        }
    }

    @DirtiesContext(methodMode = BEFORE_METHOD)
    @DisplayName("is checking admin auth.")
    @ParameterizedTest
    @MethodSource("ru.otus.vivlev.library.controller.DataForAllControllerTest#getUrlsForAdmin")
    void checkingAdminAuth(Map<String, Map<String, ResultMatcher>> urlsForAdmin) throws Exception {

        for (Map.Entry<String, Map<String, ResultMatcher>> mapEntry : urlsForAdmin.entrySet()) {

            for (Map.Entry<String, ResultMatcher> map : mapEntry.getValue().entrySet()) {

                mockMvc.perform(getMethod(map.getKey(), mapEntry.getKey())
                        .contentType("application/json")
                        .header("Authorization", TokenUtils.getToken(mockMvc, ADMIN, ADMIN_PASS))
                        .content(getContent(mapEntry.getKey())))
                        .andExpect(map.getValue())
                        .andReturn();
            }
        }
    }

    @DirtiesContext(methodMode = BEFORE_METHOD)
    @DisplayName("is checking no auth.")
    @ParameterizedTest
    @MethodSource("ru.otus.vivlev.library.controller.DataForAllControllerTest#getUrlsNoAuth")
    void checkingNoAuth(Map<String, Map<String, ResultMatcher>> urlsNoAuth) throws Exception {

        for (Map.Entry<String, Map<String, ResultMatcher>> mapEntry : urlsNoAuth.entrySet()) {

            for (Map.Entry<String, ResultMatcher> map : mapEntry.getValue().entrySet()) {

                mockMvc.perform(getMethod(map.getKey(), mapEntry.getKey())
                        .contentType("application/json"))
                        .andExpect(map.getValue())
                        .andReturn();
            }
        }
    }

    private MockHttpServletRequestBuilder getMethod(String methodName, String url) {
        switch (methodName) {
            case "get":
                return get(url);
            case "post":
                return post(url);
            case "put":
                return put(url);
            case "delete":
                return delete(url);
        }
        return null;
    }

    private String getContent(String url) throws JsonProcessingException {

        String result = "";
        if (url.contains("author")) {
            Author author1 = new Author(AUTHOR_ID_1, AUTHOR_1);
            result = objectMapper.writeValueAsString(author1);
        } else if (url.contains("genre")) {
            Genre genre1 = new Genre(GENRE_ID_1, GENRE_1);
            result = objectMapper.writeValueAsString(genre1);
        } else if (url.contains("comment")) {
            Author author = new Author(AUTHOR_ID_1, AUTHOR_1);
            Genre genre = new Genre(GENRE_ID_1, GENRE_1);
            Book book = new Book(BOOK_ID_1, BOOK_1, author, List.of(genre), new ArrayList<>());
            Comment comment = new Comment(COMMENT_ID_1, AUTHOR_1, COMMENT_1, book);
            result = objectMapper.writeValueAsString(comment);
        } else {
            Author author = new Author(AUTHOR_ID_1, AUTHOR_1);
            Genre genre = new Genre(GENRE_ID_1, GENRE_1);
            Book book = new Book(BOOK_ID_1, BOOK_1, author, List.of(genre), new ArrayList<>());

            result = objectMapper.writeValueAsString(book);
        }
        return result;
    }
}
