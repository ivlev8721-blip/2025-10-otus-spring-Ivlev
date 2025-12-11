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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@DisplayName("Тестирование AuthorServiceImpl")
@SpringBootTest
class AuthorServiceImplTest {

    private static final long EXISTING_AUTHOR_ID = 1L;
    private static final String GARRISSON = "Гаррисон, Г.";
    private static final String PERUMOV = "Перумов, Н.";
    private static final String NEW_AUTHOR_NAME = "Лукьяненко, С.";

    @MockBean
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorService authorService;

    @Test
    @DisplayName("Должен возвращать автора по ID")
    void shouldReturnAuthorById() {
        Author expected = new Author(EXISTING_AUTHOR_ID, GARRISSON);
        doReturn(Optional.of(expected)).when(authorRepository).getById(EXISTING_AUTHOR_ID);

        Optional<Author> actual = authorService.getById(EXISTING_AUTHOR_ID);

        assertThat(actual).isPresent();
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Должен возвращать всех авторов")
    void shouldReturnAllAuthors() {
        Author author1 = new Author(1L, GARRISSON);
        Author author2 = new Author(2L, PERUMOV);
        List<Author> expected = List.of(author1, author2);
        doReturn(expected).when(authorRepository).getAll();

        List<Author> actual = authorService.getAll();

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Должен сохранять нового автора и возвращать его с присвоенным ID")
    void shouldSaveNewAuthor() {
        Author authorToSave = new Author(null, NEW_AUTHOR_NAME); // новый автор без ID

        // Эмулируем поведение: save устанавливает ID = 3 и возвращает обновлённую сущность
        doAnswer(invocation -> {
            Author a = invocation.getArgument(0);
            // Присваиваем ID, как это сделал бы репозиторий
            Author saved = new Author(3L, a.getFullName());
            return saved;
        }).when(authorRepository).save(any(Author.class));

        Author savedAuthor = authorService.save(authorToSave);

        assertThat(savedAuthor.getId()).isNotNull().isGreaterThan(0L);
        assertThat(savedAuthor.getFullName()).isEqualTo(NEW_AUTHOR_NAME);
    }
}