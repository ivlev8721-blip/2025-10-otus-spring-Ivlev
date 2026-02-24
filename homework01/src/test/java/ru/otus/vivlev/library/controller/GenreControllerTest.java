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
import ru.otus.vivlev.library.dto.GenreDto;
import ru.otus.vivlev.library.kafka.producer.LibraryEventProducer;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("The GenreController class")
public class GenreControllerTest {

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
    public static final long GENRE_ID_1 = 1L;
    public static final long GENRE_ID_2 = 2L;
    public static final long GENRE_ID_3 = 3L;
    public static final String GENRE_1 = "Фантастика";
    public static final String GENRE_2 = "Фентези";
    public static final String GENRE_3 = "Роман";

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/genre/1")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        GenreDto result = objectMapper.readValue(actualResponse, GenreDto.class);

        assertThat(result.getId()).isEqualTo(GENRE_ID_1);
        assertThat(result.getGenreName()).isEqualTo(GENRE_1);
    }

    @DisplayName("is checking getAll method.")
    @Test
    void checkingGetAll() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/genre")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        GenreDto[] genres = objectMapper.readValue(actualResponse, GenreDto[].class);

        assertThat(genres).isNotEmpty();
        assertThat(genres.length).isGreaterThanOrEqualTo(1);
        assertThat(genres).allMatch(genre -> genre.getId() != null && genre.getGenreName() != null);
    }

    @DisplayName("is checking saveGenre method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingSave() throws Exception {
        GenreDto genreDto = new GenreDto(null, GENRE_1);

        String requestBody = objectMapper.writeValueAsString(genreDto);
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/genre")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        GenreDto result = objectMapper.readValue(actualResponse, GenreDto.class);

        assertThat(result.getGenreName()).isEqualTo(GENRE_1);
        assertThat(result.getId()).isNotNull();
    }

    @DisplayName("is checking delete method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/genre/3")
                .contentType("application/json"))
                .andExpect(status().isNoContent());
    }
}
