package ru.otus.vivlev.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.otus.vivlev.library.dto.AuthorDto;
import ru.otus.vivlev.library.kafka.producer.LibraryEventProducer;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;
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

    @MockBean
    private LibraryEventProducer eventProducer;

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
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/author/1")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        AuthorDto result = objectMapper.readValue(actualResponse, AuthorDto.class);

        assertThat(result.getId()).isEqualTo(AUTHOR_ID_1);
        assertThat(result.getFullName()).isEqualTo(AUTHOR_1);
    }

    @DisplayName("is checking getAll method.")
    @Test
    void checkingGetAll() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/author")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        AuthorDto[] authors = objectMapper.readValue(actualResponse, AuthorDto[].class);
        
        assertThat(authors).isNotEmpty();
        assertThat(authors.length).isGreaterThanOrEqualTo(1);
        assertThat(authors).allMatch(author -> author.getId() != null && author.getFullName() != null);
    }

    @DisplayName("is checking saveAuthor method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingSave() throws Exception {
        AuthorDto authorDto = new AuthorDto(null, AUTHOR_2);

        String requestBody = objectMapper.writeValueAsString(authorDto);
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/author")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        AuthorDto result = objectMapper.readValue(actualResponse, AuthorDto.class);

        assertThat(result.getFullName()).isEqualTo(AUTHOR_2);
        assertThat(result.getId()).isNotNull();
    }

    @DisplayName("is checking saveAuthor method with invalid authorities.")
    @Test
    void checkingSaveWithInvalidAuthorities() throws Exception {
        AuthorDto authorDto = new AuthorDto(null, AUTHOR_2);

        String requestBody = objectMapper.writeValueAsString(authorDto);

        mockMvc.perform(post("/api/v1/author")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isCreated());
    }

    @DisplayName("is checking delete method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/author/1")
                .contentType("application/json"))
                .andExpect(status().isNoContent());
    }
}
