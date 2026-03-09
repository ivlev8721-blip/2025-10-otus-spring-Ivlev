package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.vivlev.library.domain.Album;
import ru.otus.vivlev.library.domain.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Репозиторий для работы с альбомами")
class AlbumRepositoryTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Genre testGenre;
    private Album testAlbum;

    @BeforeEach
    void setUp() {
        testGenre = new Genre();
        testGenre.setName("Rock");
        entityManager.persist(testGenre);

        testAlbum = new Album();
        testAlbum.setTitle("Dark Side of the Moon");
        testAlbum.setArtist("Pink Floyd");
        testAlbum.setGenre(testGenre);
        testAlbum.setReleaseYear(1973);
        testAlbum.setCoverImageUrl("http://example.com/cover.jpg");
        testAlbum.setDescription("Test album");
        entityManager.persist(testAlbum);
        entityManager.flush();
    }

    @Test
    @DisplayName("должен загружать список всех альбомов")
    void shouldReturnCorrectAlbumsList() {
        List<Album> albums = albumRepository.findAll();
        assertThat(albums).isNotEmpty();
    }

    @Test
    @DisplayName("должен находить альбомы по жанру")
    void shouldFindAlbumsByGenre() {
        List<Album> albums = albumRepository.findByGenreId(testGenre.getId());
        assertThat(albums).isNotEmpty();
        assertThat(albums.get(0).getGenre().getId()).isEqualTo(testGenre.getId());
    }

    @Test
    @DisplayName("должен находить альбомы по исполнителю")
    void shouldFindAlbumsByArtist() {
        String artist = "Pink Floyd";
        List<Album> albums = albumRepository.findByArtistContainingIgnoreCase(artist);
        assertThat(albums).isNotEmpty();
        assertThat(albums.get(0).getArtist()).containsIgnoringCase(artist);
    }

    @Test
    @DisplayName("должен находить альбомы по названию")
    void shouldFindAlbumsByTitle() {
        String title = "Dark Side";
        org.springframework.data.domain.Page<Album> albums = albumRepository.searchAlbums(title, org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(albums.getContent()).isNotEmpty();
        assertThat(albums.getContent().get(0).getTitle()).containsIgnoringCase(title);
    }
}
