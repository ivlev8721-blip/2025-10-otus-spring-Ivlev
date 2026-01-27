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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@DataJpaTest
@DisplayName("The BookRepository class")
class BookRepositoryTest {

    public static final String AUTHOR = "Перумов, Н.";
    public static final String BOOK_TITLE = "Сумеречный дозор";
    public static final String UPDATE_BOOK_TITLE = "Мальчик и тьма";
    public static final long BOOK_ID = 1;
    public static final int EXPECTED_LIST_BOOK_SIZE = 4;
    public static final int ZERO = 0;

    public static final String QUERY_FIND_ALL_GENRES = "select g from Genre g";
    public static final String QUERY_FIND_AUTHOR_BY_NAME = "select a from Author a where a.fullName = :name";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        List<Genre> genre = em.getEntityManager().createQuery(QUERY_FIND_ALL_GENRES, Genre.class).getResultList();
        Author author = em.getEntityManager().createQuery(
                QUERY_FIND_AUTHOR_BY_NAME, Author.class).setParameter("name", AUTHOR)
                .getResultList().stream().findFirst().get();
        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(author);
        book.setGenres(genre);

        em.persist(book);
        assertThat(bookRepository.findById(author.getId())).isNotEmpty();
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() {
        List<Genre> genre = em.getEntityManager().createQuery(QUERY_FIND_ALL_GENRES, Genre.class).getResultList();
        Author author = em.getEntityManager().createQuery(
                QUERY_FIND_AUTHOR_BY_NAME, Author.class).setParameter("name", AUTHOR)
                .getResultList().stream().findFirst().get();
        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(author);
        book.setGenres(genre);

        em.persist(book);
        List<Book> books = bookRepository.findAll();
        assertThat(books.size()).isEqualTo(EXPECTED_LIST_BOOK_SIZE);
        assertThat(books).contains(book);
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        List<Genre> genre = em.getEntityManager().createQuery(QUERY_FIND_ALL_GENRES, Genre.class).getResultList();
        Author author = em.getEntityManager().createQuery(
                QUERY_FIND_AUTHOR_BY_NAME, Author.class).setParameter("name", AUTHOR)
                .getResultList().stream().findFirst().get();
        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(author);
        book.setGenres(genre);

        bookRepository.save(book);
        assertThat(book.getId()).isGreaterThan(ZERO);
        assertThat(book.getTitle()).isEqualTo(BOOK_TITLE);
    }

    @DisplayName("is checking update method.")
    @Test
    void checkingUpdate() {
        List<Genre> genre = em.getEntityManager().createQuery(QUERY_FIND_ALL_GENRES, Genre.class).getResultList();
        Author author = em.getEntityManager().createQuery(
                QUERY_FIND_AUTHOR_BY_NAME, Author.class).setParameter("name", AUTHOR)
                .getResultList().stream().findFirst().get();
        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(author);
        book.setGenres(genre);

        em.persist(book);
        book.setTitle(UPDATE_BOOK_TITLE);
        bookRepository.save(book);
        Book actualBook = em.find(Book.class, book.getId());
        assertThat(book.getTitle()).isEqualTo(actualBook.getTitle());
    }

    @DisplayName("is checking deleteById method.")
    @Test
    void checkingDeleteById() {
        List<Genre> genre = em.getEntityManager().createQuery(QUERY_FIND_ALL_GENRES, Genre.class).getResultList();
        Author author = em.getEntityManager().createQuery(
                QUERY_FIND_AUTHOR_BY_NAME, Author.class).setParameter("name", AUTHOR)
                .getResultList().stream().findFirst().get();
        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        book.setAuthor(author);
        book.setGenres(genre);

        em.persist(book);
        book.setTitle(UPDATE_BOOK_TITLE);
        bookRepository.save(book);
        Book actualBook = em.find(Book.class, book.getId());

        assertThat(book.getTitle()).isEqualTo(actualBook.getTitle());
    }
}