package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BookRepositoryJpa.class, AuthorRepositoryJpa.class, GenreRepositoryJpa.class})
@DisplayName("Тестирование BookRepositoryJpa")
@Sql(scripts = {"/schema.sql", "/data-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryJpaTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;
    
    @Autowired
    private GenreRepository genreRepository;

    private static final long EXISTING_BOOK_ID = 3L; // "Не время для драконов"
    private static final long BOOK_WITH_COMMENTS_ID = 1L; // "Стальная крыса..."

    @Test
    @DisplayName("Должен находить книгу по ID с автором и жанрами")
    void shouldFindBookById() {
        Optional<Book> bookOpt = bookRepository.getById(EXISTING_BOOK_ID);

        assertThat(bookOpt).isPresent();
        Book book = bookOpt.get();

        assertThat(book.getTitle()).isEqualTo("Не время для драконов");
        assertThat(book.getAuthor()).isNotNull();
        assertThat(book.getAuthor().getFullName()).isEqualTo("Перумов, Н.");
        assertThat(book.getGenres()).hasSize(1);
        assertThat(book.getGenres().get(0).getGenreName()).isEqualTo("Фентези");
    }

    @Test
    @DisplayName("Должен возвращать все книги")
    void shouldReturnAllBooks() {
        List<Book> books = bookRepository.getAll();

        assertThat(books).hasSize(3);
        assertThat(books)
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder(
                        "Стальная крыса идет на войну",
                        "Стальная крыса спасает мир",
                        "Не время для драконов"
                );
    }

    @Test
    @DisplayName("Должен удалять книгу")
    void shouldDeleteBook() {
        // Проверяем, что книга существует
        assertThat(bookRepository.getById(EXISTING_BOOK_ID)).isPresent();

        bookRepository.deleteById(EXISTING_BOOK_ID);

        // Проверяем, что книга удалена в рамках этой же транзакции
        // Для этого используем метод getAll(), который будет работать корректно
        assertThat(bookRepository.getAll()).hasSize(2);
    }

    @Test
    @DisplayName("Должен сохранять новую книгу")
    void shouldSaveNewBook() {
        Author author = authorRepository.getById(1L).orElseThrow();
        Genre genre = genreRepository.getById(1L).orElseThrow();
        
        Book book = new Book();
        book.setTitle("Новая книга");
        book.setAuthor(author);
        book.setGenres(List.of(genre));

        Book savedBook = bookRepository.save(book);
        
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Новая книга");
        assertThat(savedBook.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(savedBook.getGenres()).hasSize(1);
        assertThat(savedBook.getGenres().get(0).getId()).isEqualTo(genre.getId());
    }

    @Test
    @DisplayName("Должен обновлять книгу")
    void shouldUpdateBook() {
        Optional<Book> bookOpt = bookRepository.getById(EXISTING_BOOK_ID);
        assertThat(bookOpt).isPresent();

        Book book = bookOpt.get();
        String newTitle = "Обновленное название";
        book.setTitle(newTitle);

        Book updatedBook = bookRepository.update(book);

        Optional<Book> foundBook = bookRepository.getById(EXISTING_BOOK_ID);
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo(newTitle);
    }
}