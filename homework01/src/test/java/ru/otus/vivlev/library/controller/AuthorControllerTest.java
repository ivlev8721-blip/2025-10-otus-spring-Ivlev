package ru.otus.vivlev.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.otus.vivlev.library.domain.Author;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("The AuthorController class")
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, String> tokenMap = new HashMap<>();

    public static final String USER = "user";
    public static final String USER_PASS = "user";
    public static final String ADMIN = "admin";
    public static final String ADMIN_PASS = "admin";
    public static final long AUTHOR_ID_1 = 1L;
    public static final long AUTHOR_ID_2 = 2L;
    public static final long AUTHOR_ID_3 = 3L;
    public static final String AUTHOR_1 = "Гаррисон, Г.";
    public static final String AUTHOR_2 = "Перумов, Н.";
    public static final String AUTHOR_3 = "Толстой, Л.Н.";

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() throws Exception {
        Author author = new Author(AUTHOR_ID_1, AUTHOR_1);

        String expectedResponse = objectMapper.writeValueAsString(author);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/author/1")
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
    void checkingGetAll() throws Exception {
        Author author1 = new Author(AUTHOR_ID_1, AUTHOR_1);
        Author author2 = new Author(AUTHOR_ID_2, AUTHOR_2);
        Author author3 = new Author(AUTHOR_ID_3, AUTHOR_3);
        List<Author> authors = List.of(author1, author2, author3);

        String expectedResponse = objectMapper.writeValueAsString(authors);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/author")
                .contentType("application/json")
                .header("Authorization", TokenUtils.getToken(tokenMap, mockMvc, USER, USER_PASS))
                .content(expectedResponse))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @DisplayName("is checking saveAuthor method.")
    @Test
    void checkingSave() throws Exception {
        Author expectedAuthor = new Author(AUTHOR_ID_2, AUTHOR_2);

        String expectedResponse = objectMapper.writeValueAsString(expectedAuthor);
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/author")
                .contentType("application/json")
                .header("Authorization", TokenUtils.getToken(tokenMap, mockMvc, ADMIN, ADMIN_PASS))
                .content(expectedResponse))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @DisplayName("is checking saveAuthor method with invalid authorities.")
    @Test
    void checkingSaveWithInvalidAuthorities() throws Exception {
        Author expectedAuthor = new Author(AUTHOR_ID_2, AUTHOR_2);

        String expectedResponse = objectMapper.writeValueAsString(expectedAuthor);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/author")
                .contentType("application/json")
                .header("Authorization", TokenUtils.getToken(tokenMap, mockMvc, USER, USER_PASS))
                .content(expectedResponse))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @DisplayName("is checking delete method with invalid authorities.")
    @Test
    void checkingDeleteWithInvalidAuthorities() throws Exception {
        Author expectedAuthor = new Author(AUTHOR_ID_2, AUTHOR_2);

        String expectedResponse = objectMapper.writeValueAsString(expectedAuthor);

        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/author/1")
                .contentType("application/json")
                .header("Authorization", TokenUtils.getToken(tokenMap, mockMvc, USER, USER_PASS))
                .content(expectedResponse))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
