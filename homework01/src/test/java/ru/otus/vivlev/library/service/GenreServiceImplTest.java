package ru.otus.vivlev.library.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.vivlev.library.domain.Genre;
import ru.otus.vivlev.library.repository.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@DisplayName("The GenreServiceImpl class")
@SpringBootTest
class GenreServiceImplTest {

    private static final long GENRE_ID_1 = 1;
    private static final long GENRE_ID_2 = 2;
    public static final String FANTASY = "Фентези";
    public static final String FANTASTIC = "Фантастика";
    public static final String NEW_GENRE = "Приключение";

    @MockBean
    private GenreRepository genreRepository;

    @Autowired
    private GenreService genreService;

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        Genre expectedGenre = new Genre();
        expectedGenre.setId(GENRE_ID_1);
        expectedGenre.setGenreName(FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreRepository).getById(GENRE_ID_1);

        Genre actualGenre = genreService.getById(GENRE_ID_1).get();

        assertThat(actualGenre).usingRecursiveComparison().isEqualTo(expectedGenre);
    }

    @DisplayName("is checking getAll method.")
    @Test
    void checkingGetAll() {
        Genre genre1 = new Genre();
        genre1.setId(GENRE_ID_1);
        genre1.setGenreName(FANTASTIC);
        Genre genre2 = new Genre();
        genre2.setId(GENRE_ID_2);
        genre2.setGenreName(FANTASY);
        List<Genre> list = List.of(genre1, genre2);

        doReturn(list).when(genreRepository).getAll();

        List<Genre> actList = genreService.getAll();

        assertThat(actList).isEqualTo(list);
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        Genre toSave = new Genre();
        toSave.setGenreName(NEW_GENRE);
        Genre persisted = new Genre();
        persisted.setId(GENRE_ID_1);
        persisted.setGenreName(NEW_GENRE);

        doReturn(persisted).when(genreRepository).save(toSave);

        Genre actualGenre = genreService.save(toSave);

        assertThat(actualGenre.getId()).isEqualTo(GENRE_ID_1);
        assertThat(actualGenre.getGenreName()).isEqualTo(NEW_GENRE);
    }
}