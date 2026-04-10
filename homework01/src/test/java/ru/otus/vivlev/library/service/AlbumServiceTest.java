package ru.otus.vivlev.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.vivlev.library.domain.Album;
import ru.otus.vivlev.library.domain.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Сервис для работы с альбомами")
class AlbumServiceTest {

    @Autowired
    private AlbumService albumService;

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
    @DisplayName("должен создавать новый альбом")
    void shouldCreateAlbum() {
        Genre genre = testGenre;
        
        Album album = new Album();
        album.setTitle("Test Album");
        album.setArtist("Test Artist");
        album.setGenre(genre);
        album.setReleaseYear(2024);
        album.setCoverImageUrl("http://example.com/cover.jpg");
        album.setDescription("Test description");

        Album created = albumService.createAlbum(album);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Test Album");
        assertThat(created.getArtist()).isEqualTo("Test Artist");
    }

    @Test
    @DisplayName("должен находить альбом по ID")
    void shouldFindAlbumById() {
        Album album = new Album();
        album.setTitle("Test Album");
        album.setArtist("Test Artist");
        album.setGenre(testGenre);
        album.setReleaseYear(2024);
        album.setCoverImageUrl("http://example.com/cover.jpg");
        album.setDescription("Test");
        Album created = albumService.createAlbum(album);

        Album found = albumService.getAlbumById(created.getId()).orElseThrow();

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
    }

    @Test
    @DisplayName("должен обновлять альбом")
    void shouldUpdateAlbum() {
        Album album = new Album();
        album.setTitle("Original Title");
        album.setArtist("Test Artist");
        album.setGenre(testGenre);
        album.setReleaseYear(2024);
        album.setCoverImageUrl("http://example.com/cover.jpg");
        album.setDescription("Test");
        album = albumService.createAlbum(album);
        String newTitle = "Updated Title";
        album.setTitle(newTitle);

        Album updated = albumService.updateAlbum(album.getId(), album);

        assertThat(updated.getTitle()).isEqualTo(newTitle);
    }

    @Test
    @DisplayName("должен находить альбомы по жанру")
    void shouldFindAlbumsByGenre() {
        Album album = new Album();
        album.setTitle("Test Album");
        album.setArtist("Test Artist");
        album.setGenre(testGenre);
        album.setReleaseYear(2024);
        album.setCoverImageUrl("http://example.com/cover.jpg");
        album.setDescription("Test");
        albumService.createAlbum(album);

        List<Album> albums = albumService.getAlbumsByGenre(testGenre.getId());

        assertThat(albums).isNotEmpty();
        assertThat(albums.get(0).getGenre().getId()).isEqualTo(testGenre.getId());
    }
}
