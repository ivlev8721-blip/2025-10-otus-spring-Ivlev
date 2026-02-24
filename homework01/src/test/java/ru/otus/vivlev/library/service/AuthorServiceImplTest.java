package ru.otus.vivlev.library.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.repository.AuthorRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@DisplayName("The AuthorServiceImpl class")
@SpringBootTest
class AuthorServiceImplTest {

    public static final int ZERO_ID = 0;
    public static final long AUTHOR_ID_1 = 1;
    public static final long AUTHOR_ID_2 = 2;
    public static final String GARRISSON = "Гаррисон, Г.";
    public static final String PERUMOV = "Перумов, Н.";
    public static final String NEW_AUTHOR = "Лукьяненко, С.";

    @MockBean
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorService authorService;

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        Author expectedAuthor = new Author(AUTHOR_ID_1, GARRISSON);

        doReturn(Optional.of(expectedAuthor)).when(authorRepository).findById(AUTHOR_ID_1);
        Author actualAuthor = authorService.getById(AUTHOR_ID_1).get();

        assertThat(actualAuthor).usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() {
        Author author1 = new Author(AUTHOR_ID_1, GARRISSON);
        Author author2 = new Author(AUTHOR_ID_2, PERUMOV);
        List<Author> list = List.of(author1, author2);
        doReturn(list).when(authorRepository).findAll();

        List<Author> actList = authorService.getAll();

        assertThat(actList).isEqualTo(list);
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        Author expectedAuthor = new Author(AUTHOR_ID_2, NEW_AUTHOR);
        doReturn(expectedAuthor).when(authorRepository).save(expectedAuthor);
        Author actualAuthor = authorService.save(expectedAuthor);
        assertThat(actualAuthor.getId()).isGreaterThan(ZERO_ID);
        assertThat(actualAuthor.getFullName()).isEqualTo(expectedAuthor.getFullName());
    }
}