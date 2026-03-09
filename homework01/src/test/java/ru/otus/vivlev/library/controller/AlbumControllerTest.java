package ru.otus.vivlev.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.vivlev.library.domain.Album;
import ru.otus.vivlev.library.domain.Genre;
import ru.otus.vivlev.library.service.GenreService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("REST контроллер для работы с альбомами")
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GenreService genreService;

    private Genre testGenre;

    @BeforeEach
    void setUp() {
        testGenre = new Genre();
        testGenre.setName("Rock");
        testGenre = genreService.save(testGenre);
    }

    @Test
    @DisplayName("должен возвращать список всех альбомов")
    void shouldReturnAllAlbums() throws Exception {
        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("должен создавать новый альбом для администратора")
    void shouldCreateAlbumForAdmin() throws Exception {
        Genre genre = testGenre;

        Album album = new Album();
        album.setTitle("New Test Album");
        album.setArtist("Test Artist");
        album.setGenre(genre);
        album.setReleaseYear(2024);
        album.setCoverImageUrl("http://example.com/cover.jpg");
        album.setDescription("Test description");

        mockMvc.perform(post("/api/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("должен запрещать создание альбома для обычного пользователя")
    void shouldForbidCreateAlbumForUser() throws Exception {
        Genre genre = testGenre;

        Album album = new Album();
        album.setTitle("New Test Album");
        album.setArtist("Test Artist");
        album.setGenre(genre);
        album.setReleaseYear(2024);
        album.setCoverImageUrl("http://example.com/cover.jpg");
        album.setDescription("Test description");

        mockMvc.perform(post("/api/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("должен находить альбомы по жанру")
    void shouldFindAlbumsByGenre() throws Exception {
        mockMvc.perform(get("/api/albums/genre/" + testGenre.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
