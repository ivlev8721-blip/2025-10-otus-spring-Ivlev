package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.otus.vivlev.library.domain.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(GenreRepositoryJpa.class)
@DisplayName("Тестирование GenreRepositoryJpa")
@Sql(scripts = {"/schema.sql", "/data-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GenreRepositoryJpaTest {

    public static final String NEW_GENRE = "Приключения";
    public static final String EXISTING_GENRE = "Фантастика";

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Должен находить жанр по ID")
    void shouldFindGenreById() {
        Optional<Genre> genreOpt = genreRepository.getById(1L);

        assertThat(genreOpt)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre.getGenreName()).isEqualTo(EXISTING_GENRE)
                );
    }

    @Test
    @DisplayName("Должен возвращать все жанры")
    void shouldReturnAllGenres() {
        List<Genre> genres = genreRepository.getAll();

        assertThat(genres).hasSize(2);
        assertThat(genres)
                .extracting(Genre::getGenreName)
                .containsExactlyInAnyOrder("Фантастика", "Фентези");
    }

    @Test
    @DisplayName("Должен сохранять новый жанр")
    void shouldSaveNewGenre() {
        Genre genre = new Genre();
        genre.setGenreName(NEW_GENRE);

        Genre savedGenre = genreRepository.save(genre);
        em.flush();
        em.clear();

        assertThat(savedGenre.getId()).isNotNull().isGreaterThan(0L);
        assertThat(savedGenre.getGenreName()).isEqualTo(NEW_GENRE);

        Optional<Genre> foundGenre = genreRepository.getById(savedGenre.getId());
        assertThat(foundGenre).isPresent();
        assertThat(foundGenre.get().getGenreName()).isEqualTo(NEW_GENRE);
    }

    @Test
    @DisplayName("Должен удалять жанр по ID")
    void shouldDeleteGenreById() {
        Genre genre = new Genre();
        genre.setGenreName("Тестовый жанр");
        Genre savedGenre = em.persistAndFlush(genre);
        em.clear(); // очищаем контекст
        
        long genreId = savedGenre.getId();
        assertThat(genreRepository.getById(genreId)).isPresent();

        genreRepository.deleteById(genreId);
        em.flush();
        em.clear(); // очищаем контекст

        assertThat(genreRepository.getById(genreId)).isEmpty();
    }
}