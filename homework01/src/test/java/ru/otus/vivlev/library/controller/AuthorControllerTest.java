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
import ru.otus.vivlev.library.repository.AuthorRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;

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

    @Autowired
    private AuthorRepository authorRepository;

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() throws Exception {
        // ожидаемое значение берём из БД, не хардкодим кириллицу
        Author author = authorRepository.findById(1L).orElseThrow();
        String expectedResponse = objectMapper.writeValueAsString(author);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/author/1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @DisplayName("is checking getAll method.")
    @Test
    void checkingGetAll() throws Exception {
        List<Author> authors = authorRepository.findAll();
        String expectedResponse = objectMapper.writeValueAsString(authors);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/author")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }
}