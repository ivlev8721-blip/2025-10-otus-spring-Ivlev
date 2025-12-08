package ru.otus.vivlev.library.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.repository.AuthorRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@DisplayName("The AuthorServiceImpl class")
@SpringBootTest
class AuthorServiceImplTest {

    private static final long AUTHOR_ID_1 = 1;
    private static final long AUTHOR_ID_2 = 2;
    private static final String GARRISSON = "Гаррисон, Г.";
    private static final String PERUMOV = "Перумов, Н.";
    private static final String NEW_AUTHOR = "Лукьяненко, С.";

    @MockBean
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorService authorService;

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        Author expectedAuthor = new Author(AUTHOR_ID_1, GARRISSON);

        doReturn(Optional.of(expectedAuthor)).when(authorRepository).getById(AUTHOR_ID_1);
        Author actualAuthor = authorService.getById(AUTHOR_ID_1).get();

        assertThat(actualAuthor).usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @DisplayName("is checking getAll method.")
    @Test
    void checkingGetAll() {
        Author author1 = new Author(AUTHOR_ID_1, GARRISSON);
        Author author2 = new Author(AUTHOR_ID_2, PERUMOV);
        List<Author> list = List.of(author1, author2);
        doReturn(list).when(authorRepository).getAll();

        List<Author> actList = authorService.getAll();

        assertThat(actList).isEqualTo(list);
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        Author expectedAuthor = new Author(null, NEW_AUTHOR);
        Author persisted = new Author(AUTHOR_ID_2, NEW_AUTHOR);
        doReturn(persisted).when(authorRepository).save(expectedAuthor);

        Author actualAuthor = authorService.save(expectedAuthor);

        assertThat(actualAuthor.getId()).isNotNull().isEqualTo(AUTHOR_ID_2);
        assertThat(actualAuthor.getFullName()).isEqualTo(NEW_AUTHOR);
    }
}