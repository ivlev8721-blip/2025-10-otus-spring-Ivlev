package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.vivlev.library.domain.Author;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@JdbcTest
@Import(AuthorJdbcImpl.class)
@DisplayName("The AuthorJdbcImpl class")
class AuthorJdbcImplTest {

    public static final String NEW_AUTHOR = "Лукьяненко, С.";
    public static final int AUTHOR_ID = 1;
    public static final int EXPECTED_LIST_AUTHORS_SIZE = 3;
    public static final int ZERO_ID = 0;

    @Autowired
    private AuthorJdbc authorJdbc;

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        Author expectedAuthor = authorJdbc.save(new Author(NEW_AUTHOR));
        Author actualAuthor = authorJdbc.getById(expectedAuthor.getId()).get();
        assertThat(actualAuthor).usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() {
        Author expectedAuthor = authorJdbc.save(new Author(NEW_AUTHOR));
        List<Author> authors = authorJdbc.getAll();
        assertThat(authors.size()).isEqualTo(EXPECTED_LIST_AUTHORS_SIZE);
        assertThat(authors).contains(expectedAuthor);
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        Author expectedAuthor = new Author(NEW_AUTHOR);
        Author actualAuthor = authorJdbc.save(expectedAuthor);
        assertThat(actualAuthor.getId()).isGreaterThan(ZERO_ID);
        assertThat(actualAuthor.getFullName()).isEqualTo(expectedAuthor.getFullName());
    }

    @DisplayName("is checking deleteById method.")
    @Test
    void checkingDeleteById() {
        Author author = authorJdbc.getById(AUTHOR_ID).get();
        assertThat(author.getId()).isEqualTo(AUTHOR_ID);

        authorJdbc.deleteById(AUTHOR_ID);

        assertThat(authorJdbc.getAll()).doesNotContain(author);
    }
}