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
import java.util.Optional;

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

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        Optional<Book> bookOptional = bookRepository.findById(BOOK_ID);
        assertThat(bookOptional).isPresent();
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() {
        List<Book> books = bookRepository.findAll();
        assertThat(books.size()).isEqualTo(3); // В тестовых данных 3 книги
        
        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        
        // Получаем существующего автора из БД
        Author author = em.find(Author.class, 2L); // "Перумов, Н." имеет ID=2 в тестовых данных
        book.setAuthor(author);
        
        // Получаем жанры из БД
        List<Genre> genres = em.getEntityManager()
            .createQuery("select g from Genre g", Genre.class)
            .getResultList();
        book.setGenres(genres);
        
        em.persist(book);
        
        books = bookRepository.findAll();
        assertThat(books.size()).isEqualTo(4); // После добавления должно быть 4 книги
        assertThat(books).contains(book);
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        Book book = new Book();
        book.setTitle(BOOK_TITLE);
        
        // Получаем существующего автора из БД
        Author author = em.find(Author.class, 2L); // "Перумов, Н." имеет ID=2 в тестовых данных
        book.setAuthor(author);
        
        // Получаем жанры из БД
        List<Genre> genres = em.getEntityManager()
            .createQuery("select g from Genre g", Genre.class)
            .getResultList();
        book.setGenres(genres);

        bookRepository.save(book);
        assertThat(book.getId()).isGreaterThan(ZERO);
        assertThat(book.getTitle()).isEqualTo(BOOK_TITLE);
    }

    @DisplayName("is checking update method.")
    @Test
    void checkingUpdate() {
        // Получаем существующую книгу
        Optional<Book> bookOptional = bookRepository.findById(BOOK_ID);
        assertThat(bookOptional).isPresent();
        Book book = bookOptional.get();
        
        book.setTitle(UPDATE_BOOK_TITLE);
        bookRepository.save(book);
        
        Book actualBook = em.find(Book.class, book.getId());
        assertThat(book.getTitle()).isEqualTo(actualBook.getTitle());
    }

    @DisplayName("is checking deleteById method.")
    @Test
    void checkingDeleteById() {
        // Проверяем, что книга существует
        Optional<Book> bookOptional = bookRepository.findById(BOOK_ID);
        assertThat(bookOptional).isPresent();
        Book book = bookOptional.get();
        
        // Удаляем книгу
        bookRepository.deleteById(BOOK_ID);
        
        // Проверяем, что книга удалена
        Optional<Book> deletedBookOptional = bookRepository.findById(BOOK_ID);
        assertThat(deletedBookOptional).isEmpty();
    }
}