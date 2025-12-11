package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@DataJpaTest
@DisplayName("The BookRepository class")
class BookRepositoryTest {

    public static final String AUTHOR = "Перумов, Н.";
    public static final String BOOK_TITLE = "Сумеречный дозор";
    public static final String UPDATE_BOOK_TITLE = "Мальчик и тьма";
    public static final int ZERO = 0;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    private Author persistAuthor() {
        Author author = new Author();
        author.setFullName(AUTHOR);
        em.persist(author);
        return author;
    }

    private Genre persistGenre() {
        Genre genre = new Genre();
        genre.setGenreName("Фантастика");
        em.persist(genre);
        return genre;
    }

    private Book persistBook() {
        Author author = persistAuthor();
        Genre genre = persistGenre();

        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(author);
        book.setGenres(new ArrayList<>(List.of(genre)));

        em.persist(book);
        em.flush();
        return book;
    }

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        Book book = persistBook();

        Optional<Book> actual = bookRepository.findById(book.getId());
        assertThat(actual).isPresent();
        assertThat(actual.get().getTitle()).isEqualTo(BOOK_TITLE);
        assertThat(actual.get().getAuthor().getFullName()).isEqualTo(AUTHOR);
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() {
        Book book = persistBook();

        List<Book> books = bookRepository.findAll();
        assertThat(books)
                .extracting(Book::getTitle)
                .contains(BOOK_TITLE);
        assertThat(books.size()).isGreaterThanOrEqualTo(1);
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        Author author = persistAuthor();
        Genre genre = persistGenre();

        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(author);
        book.setGenres(new ArrayList<>(List.of(genre)));

        bookRepository.save(book);

        assertThat(book.getId()).isGreaterThan(ZERO);
        assertThat(book.getTitle()).isEqualTo(BOOK_TITLE);
    }

    @DisplayName("is checking update method.")
    @Test
    void checkingUpdate() {
        Book book = persistBook();

        book.setTitle(UPDATE_BOOK_TITLE);
        bookRepository.save(book);

        Book actualBook = em.find(Book.class, book.getId());
        assertThat(actualBook.getTitle()).isEqualTo(UPDATE_BOOK_TITLE);
    }

    @DisplayName("is checking deleteById method.")
    @Test
    void checkingDeleteById() {
        Book book = persistBook();

        Long bookId = book.getId();
        bookRepository.deleteById(bookId);
        em.flush();

        assertThat(bookRepository.findById(bookId)).isEmpty();
    }
}