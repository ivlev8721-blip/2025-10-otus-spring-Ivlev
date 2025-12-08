package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.vivlev.library.domain.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@DataJpaTest
@Import(GenreRepositoryJpa.class)
@DisplayName("The GenreRepositoryJpa class")
class GenreRepositoryJpaTest {

    private static final String NEW_GENRE = "Приключения";
    private static final int EXPECTED_LIST_GENRES_SIZE = 3;
    private static final long ZERO = 0;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        Genre genre = new Genre();
        genre.setGenreName(NEW_GENRE);
        em.persist(genre);
        em.flush();

        assertThat(genreRepository.getById(genre.getId())).isNotEmpty();
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() {
        Genre genre = new Genre();
        genre.setGenreName(NEW_GENRE);
        em.persist(genre);
        em.flush();

        List<Genre> genres = genreRepository.getAll();
        assertThat(genres.size()).isEqualTo(EXPECTED_LIST_GENRES_SIZE);
        assertThat(genres).contains(genre);
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        Genre genre = new Genre();
        genre.setGenreName(NEW_GENRE);
        genreRepository.save(genre);

        assertThat(genre.getId()).isGreaterThan(ZERO);
    }

    @DisplayName("is checking deleteById method.")
    @Test
    void checkingDeleteById() {
        Genre genre = new Genre();
        genre.setGenreName(NEW_GENRE);
        em.persist(genre);
        em.flush();

        genreRepository.deleteById(genre.getId());

        assertThat(genreRepository.getAll()).doesNotContain(genre);
    }
}