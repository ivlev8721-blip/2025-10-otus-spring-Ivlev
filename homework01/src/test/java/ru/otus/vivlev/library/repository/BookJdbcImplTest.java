package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.domain.Book;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@JdbcTest
@Import({BookJdbcImpl.class, AuthorJdbcImpl.class, GenreJdbcImpl.class})
@DisplayName("The BookJdbcImpl class")
class BookJdbcImplTest {

    public static final String NEW_AUTHOR = "Лукьяненко, С.";
    public static final String NEW_BOOK_TITLE = "Рыцари сорока островов";
    public static final String UPDATE_BOOK_TITLE = "Мальчик и тьма";
    public static final int BOOK_ID = 1;
    public static final int EXPECTED_LIST_BOOK_SIZE = 4;
    public static final int ZERO = 0;

    @Autowired
    private AuthorJdbc authorJdbc;

    @Autowired
    private GenreJdbc genreJdbc;

    @Autowired
    private BookJdbcImpl bookJdbc;

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        Book expectedBook = bookJdbc.save(new Book(NEW_BOOK_TITLE, authorJdbc.save(new Author(NEW_AUTHOR)), genreJdbc.getAll()));
        Book actualBook = bookJdbc.getById(expectedBook.getId()).get();
        assertThat(actualBook).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() {
        Book expectedBook = bookJdbc.save(new Book(NEW_BOOK_TITLE, authorJdbc.save(new Author(NEW_AUTHOR)), genreJdbc.getAll()));
        List<Book> books = bookJdbc.getAll();
        assertThat(books.size()).isEqualTo(EXPECTED_LIST_BOOK_SIZE);
        assertThat(books).contains(expectedBook);
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        Book expectedBook = bookJdbc.save(new Book(NEW_BOOK_TITLE, authorJdbc.save(new Author(NEW_AUTHOR)), genreJdbc.getAll()));
        assertThat(expectedBook.getId()).isGreaterThan(ZERO);
        assertThat(expectedBook.getTitle()).isEqualTo(NEW_BOOK_TITLE);
    }

    @DisplayName("is checking update method.")
    @Test
    void checkingUpdate() {
        Book expectedBook = bookJdbc.save(new Book(NEW_BOOK_TITLE, authorJdbc.save(new Author(NEW_AUTHOR)), genreJdbc.getAll()));
        expectedBook.setTitle(UPDATE_BOOK_TITLE);
        bookJdbc.update(expectedBook);
        Book actualBook = bookJdbc.getById(expectedBook.getId()).get();
        assertThat(expectedBook.getTitle()).isEqualTo(actualBook.getTitle());
    }

    @DisplayName("is checking deleteById method.")
    @Test
    void checkingDeleteById() {
        Book book = bookJdbc.getById(BOOK_ID).get();
        assertThat(book.getId()).isEqualTo(BOOK_ID);

        bookJdbc.deleteById(BOOK_ID);

        assertThat(bookJdbc.getAll()).doesNotContain(book);
    }
}