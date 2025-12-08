package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Genre;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@DataJpaTest
@Import({BookRepositoryJpa.class})
@DisplayName("The BookRepositoryJpa class")
class BookRepositoryJpaTest {

    private static final String AUTHOR_NAME = "Перумов, Н.";
    private static final String GENRE_FANTASTIC = "Фантастика";
    private static final String GENRE_FANTASY = "Фентези";
    private static final String BOOK_TITLE = "Сумеречный дозор";
    private static final String UPDATE_BOOK_TITLE = "Мальчик и тьма";
    private static final int EXPECTED_LIST_BOOK_SIZE = 4; // 3 из data-test + 1 добавленный
    private static final int ZERO = 0;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    private List<Genre> persistDefaultGenres() {
        Genre g1 = new Genre();
        g1.setGenreName(GENRE_FANTASTIC);
        Genre g2 = new Genre();
        g2.setGenreName(GENRE_FANTASY);
        em.persist(g1);
        em.persist(g2);
        em.flush();
        return new ArrayList<>(List.of(g1, g2)); // возвращаем мутабельный список
    }

    private Author persistAuthor() {
        Author author = new Author();
        author.setFullName(AUTHOR_NAME);
        em.persist(author);
        em.flush();
        return author;
    }

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        List<Genre> genres = persistDefaultGenres();
        Author author = persistAuthor();
        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(author);
        book.setGenres(new ArrayList<>(genres));

        em.persist(book);
        em.flush();

        assertThat(bookRepository.getById(book.getId())).isNotEmpty();
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() {
        List<Genre> genres = persistDefaultGenres();
        Author author = persistAuthor();
        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(author);
        book.setGenres(new ArrayList<>(genres));

        em.persist(book);
        em.flush();

        List<Book> books = bookRepository.getAll();
        assertThat(books.size()).isEqualTo(EXPECTED_LIST_BOOK_SIZE);
        assertThat(books).contains(book);
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        List<Genre> genres = persistDefaultGenres();
        Author author = persistAuthor();
        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(author);
        book.setGenres(new ArrayList<>(genres));

        bookRepository.save(book);

        assertThat(book.getId()).isGreaterThan(ZERO);
        assertThat(book.getTitle()).isEqualTo(BOOK_TITLE);
    }

    @DisplayName("is checking update method.")
    @Test
    void checkingUpdate() {
        List<Genre> genres = persistDefaultGenres();
        Author author = persistAuthor();
        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(author);
        book.setGenres(new ArrayList<>(genres));

        em.persist(book);
        em.flush();

        book.setTitle(UPDATE_BOOK_TITLE);
        bookRepository.update(book);
        Book actualBook = em.find(Book.class, book.getId());

        assertThat(book.getTitle()).isEqualTo(actualBook.getTitle());
    }

    @DisplayName("is checking deleteById method.")
    @Test
    void checkingDeleteById() {
        List<Genre> genres = persistDefaultGenres();
        Author author = persistAuthor();
        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(author);
        book.setGenres(new ArrayList<>(genres));

        em.persist(book);
        em.flush();

        Long id = book.getId();
        bookRepository.deleteById(id);
        em.flush();
        em.clear();

        assertThat(bookRepository.getById(id)).isEmpty();
    }
}