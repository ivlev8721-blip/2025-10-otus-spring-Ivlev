package ru.otus.vivlev.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.otus.vivlev.library.dto.CommentDto;
import ru.otus.vivlev.library.kafka.producer.LibraryEventProducer;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("The CommentController class")
public class CommentControllerTest {

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
    public static final String AUTHOR_1 = "Гаррисон, Г.";
    public static final long GENRE_ID_1 = 1L;
    public static final String GENRE_1 = "Фантастика";
    public static final long COMMENT_ID_1 = 1L;
    public static final long COMMENT_ID_2 = 2L;
    public static final long BOOK_ID_1 = 1L;
    public static final String COMMENT_1 = "Классная книга, рекомендую!";
    public static final String COMMENT_2 = "Прочитал в один заход!";
    public static final String AUTHOR = "ADMIN";

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/comment/1")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        CommentDto result = objectMapper.readValue(actualResponse, CommentDto.class);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserName()).isNotNull();
        assertThat(result.getText()).isNotNull();
        assertThat(result.getBookId()).isNotNull();
    }

    @DisplayName("is checking getAll method.")
    @Test
    void checkingGetAllByBookId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/comment/book/1")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        CommentDto[] comments = objectMapper.readValue(actualResponse, CommentDto[].class);

        assertThat(comments).isNotEmpty();
        assertThat(comments).allMatch(comment -> 
            comment.getId() != null && 
            comment.getUserName() != null && 
            comment.getText() != null && 
            comment.getBookId() != null
        );
    }
}
