package ru.otus.vivlev.library.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.otus.vivlev.library.dto.AuthorDto;
import ru.otus.vivlev.library.dto.BookDto;
import ru.otus.vivlev.library.dto.CommentDto;
import ru.otus.vivlev.library.dto.GenreDto;
import ru.otus.vivlev.library.kafka.producer.LibraryEventProducer;

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

    @MockBean
    private LibraryEventProducer eventProducer;

    private final Map<String, String> tokenMap = new HashMap<>();

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
                        .header("Authorization", TokenUtils.getToken(tokenMap, mockMvc, USER, USER_PASS))
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
                        .header("Authorization", TokenUtils.getToken(tokenMap, mockMvc, ADMIN, ADMIN_PASS))
                        .content(getContent(mapEntry.getKey())))
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
            AuthorDto authorDto = new AuthorDto(null, AUTHOR_1);
            result = objectMapper.writeValueAsString(authorDto);
        } else if (url.contains("genre")) {
            GenreDto genreDto = new GenreDto(null, GENRE_1);
            result = objectMapper.writeValueAsString(genreDto);
        } else if (url.contains("comment")) {
            CommentDto commentDto = new CommentDto(null, AUTHOR_1, COMMENT_1, BOOK_ID_1);
            result = objectMapper.writeValueAsString(commentDto);
        } else {
            AuthorDto authorDto = new AuthorDto(AUTHOR_ID_1, AUTHOR_1);
            GenreDto genreDto = new GenreDto(GENRE_ID_1, GENRE_1);
            BookDto bookDto = new BookDto(null, BOOK_1, authorDto, List.of(genreDto));

            result = objectMapper.writeValueAsString(bookDto);
        }
        return result;
    }
}
