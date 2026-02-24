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
import ru.otus.vivlev.library.dto.BookDto;
import ru.otus.vivlev.library.dto.GenreDto;
import ru.otus.vivlev.library.kafka.producer.LibraryEventProducer;

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

    @MockBean
    private LibraryEventProducer eventProducer;

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
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/book/1")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        BookDto result = objectMapper.readValue(actualResponse, BookDto.class);

        assertThat(result.getId()).isEqualTo(BOOK_ID_1);
        assertThat(result.getTitle()).isEqualTo(BOOK_1);
        assertThat(result.getAuthor()).isNotNull();
        assertThat(result.getGenres()).isNotEmpty();
    }

    @DisplayName("is checking getAll method.")
    @Test
    void checkingGetAll() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/book")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        BookDto[] books = objectMapper.readValue(actualResponse, BookDto[].class);

        assertThat(books).isNotEmpty();
        assertThat(books.length).isGreaterThanOrEqualTo(1);
        assertThat(books).allMatch(book -> 
            book.getId() != null && 
            book.getTitle() != null && 
            book.getAuthor() != null
        );
    }

    @DisplayName("is checking saveBook method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingSave() throws Exception {
        AuthorDto author = new AuthorDto(AUTHOR_ID_1, AUTHOR_1);
        GenreDto genre = new GenreDto(GENRE_ID_1, GENRE_1);
        BookDto book = new BookDto(null, BOOK_1, author, List.of(genre));

        String requestBody = objectMapper.writeValueAsString(book);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/book")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        BookDto result = objectMapper.readValue(actualResponse, BookDto.class);

        assertThat(result.getTitle()).isEqualTo(BOOK_1);
        assertThat(result.getId()).isNotNull();
    }

    @DisplayName("is checking updateBook method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingUpdate() throws Exception {
        AuthorDto author = new AuthorDto(AUTHOR_ID_1, AUTHOR_1);
        GenreDto genre = new GenreDto(GENRE_ID_1, GENRE_1);
        BookDto book = new BookDto(BOOK_ID_2, "Updated Title", author, List.of(genre));

        String requestBody = objectMapper.writeValueAsString(book);

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/book/2")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        BookDto result = objectMapper.readValue(actualResponse, BookDto.class);

        assertThat(result.getId()).isEqualTo(BOOK_ID_2);
        assertThat(result.getTitle()).isEqualTo("Updated Title");
    }

    @DisplayName("is checking delete method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/book/3")
                .contentType("application/json"))
                .andExpect(status().isNoContent());
    }
}
