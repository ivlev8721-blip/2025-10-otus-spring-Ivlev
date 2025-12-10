package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.vivlev.library.domain.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@JdbcTest
@Import(GenreJdbcImpl.class)
@DisplayName("The GenreJdbcImpl class")
class GenreJdbcImplTest {

    public static final String NEW_GENRE = "Приключения";
    public static final int EXPECTED_LIST_GENRES_SIZE = 3;
    public static final int ZERO = 0;

    @Autowired
    private GenreJdbc genreJdbc;

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        Genre expectedGenre = genreJdbc.save(new Genre(NEW_GENRE));
        Genre actualGenre = genreJdbc.getById(expectedGenre.getId()).get();
        assertThat(actualGenre).usingRecursiveComparison().isEqualTo(expectedGenre);
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() {
        Genre expectedGenre = genreJdbc.save(new Genre(NEW_GENRE));
        List<Genre> genres = genreJdbc.getAll();
        assertThat(genres.size()).isEqualTo(EXPECTED_LIST_GENRES_SIZE);
        assertThat(genres).contains(expectedGenre);
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        Genre genreToSave = new Genre(NEW_GENRE);
        Genre savedGenre = genreJdbc.save(genreToSave);
        assertThat(savedGenre.getId()).isGreaterThan(ZERO);
        assertThat(savedGenre.getGenreName()).isEqualTo(NEW_GENRE);
    }
}