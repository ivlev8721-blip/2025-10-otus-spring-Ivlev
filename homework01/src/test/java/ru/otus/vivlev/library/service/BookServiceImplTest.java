package ru.otus.vivlev.library.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Genre;
import ru.otus.vivlev.library.repository.BookRepository;
import ru.otus.vivlev.library.repository.GenreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static ru.otus.vivlev.library.service.GenreServiceImplTest.FANTASTIC;
import static ru.otus.vivlev.library.service.GenreServiceImplTest.GENRE_ID_1;

@DisplayName("Тестирование BookServiceImpl")
@SpringBootTest
class BookServiceImplTest {

    public static final long ID_ONE = 1;
    public static final long ID_TWO = 2;
    public static final int EXPECTED_LIST_BOOK_SIZE = 1;
    public static final String BOOK_TITLE_1 = "Стальная крыса идет на войну";
    public static final String BOOK_TITLE_2 = "Стальная крыса поет блюз";
    public static final String AUTHOR_1 = "Гаррисон, Г.";

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Test
    @DisplayName("Должен находить книгу по ID")
    void shouldFindBookById() {
        Genre expectedGenre = new Genre();
        expectedGenre.setId(GENRE_ID_1);
        expectedGenre.setGenreName(FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreRepository).getById(GENRE_ID_1);
        
        Author author = new Author();
        author.setId(1L);
        author.setFullName(AUTHOR_1);
        
        Book expectedBook = new Book();
        expectedBook.setId(ID_ONE);
        expectedBook.setTitle(BOOK_TITLE_1);
        expectedBook.setAuthor(author);
        expectedBook.setGenres(List.of(expectedGenre));
        
        doReturn(Optional.of(expectedBook)).when(bookRepository).getById(ID_ONE);
        Optional<Book> actualBook = bookService.getById(ID_ONE);
        
        assertThat(actualBook).isPresent();
        assertThat(actualBook.get()).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("Должен возвращать все книги")
    void shouldReturnAllBooks() {
        Genre expectedGenre = new Genre();
        expectedGenre.setId(GENRE_ID_1);
        expectedGenre.setGenreName(FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreRepository).getById(GENRE_ID_1);
        
        Author author = new Author();
        author.setId(1L);
        author.setFullName(AUTHOR_1);
        
        Book expectedBook = new Book();
        expectedBook.setId(ID_ONE);
        expectedBook.setTitle(BOOK_TITLE_1);
        expectedBook.setAuthor(author);
        expectedBook.setGenres(List.of(expectedGenre));
        
        List<Book> expectedBookList = List.of(expectedBook);
        doReturn(expectedBookList).when(bookRepository).getAll();
        List<Book> books = bookService.getAll();
        assertThat(books.size()).isEqualTo(EXPECTED_LIST_BOOK_SIZE);
    }

    @Test
    @DisplayName("Должен сохранять книгу")
    void shouldSaveBook() {
        Genre expectedGenre = new Genre();
        expectedGenre.setId(GENRE_ID_1);
        expectedGenre.setGenreName(FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreRepository).getById(GENRE_ID_1);
        
        Author author = new Author();
        author.setId(1L);
        author.setFullName(AUTHOR_1);
        
        Book book = new Book();
        book.setId(null);
        book.setTitle(BOOK_TITLE_2);
        book.setAuthor(author);
        book.setGenres(List.of(expectedGenre));
        
        Book savedBook = new Book();
        savedBook.setId(ID_ONE);
        savedBook.setTitle(BOOK_TITLE_2);
        savedBook.setAuthor(author);
        savedBook.setGenres(List.of(expectedGenre));
        
        doReturn(savedBook).when(bookRepository).save(book);
        Book expectedBook = bookService.save(book);
        assertThat(expectedBook.getId()).isNotNull();
        assertThat(expectedBook.getTitle()).isEqualTo(BOOK_TITLE_2);
    }

    @Test
    @DisplayName("Должен обновлять книгу")
    void shouldUpdateBook() {
        Genre expectedGenre = new Genre();
        expectedGenre.setId(GENRE_ID_1);
        expectedGenre.setGenreName(FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreRepository).getById(GENRE_ID_1);
        
        Author author = new Author();
        author.setId(1L);
        author.setFullName(AUTHOR_1);
        
        Book book = new Book();
        book.setId(ID_ONE);
        book.setTitle(BOOK_TITLE_1);
        book.setAuthor(author);
        book.setGenres(List.of(expectedGenre));
        
        doReturn(Optional.of(book)).when(bookRepository).getById(ID_ONE);
        Book expectedBook = bookService.getById(ID_ONE).get();
        expectedBook.setTitle(BOOK_TITLE_2);
        doReturn(expectedBook).when(bookRepository).update(expectedBook);
        bookService.update(expectedBook);
        doReturn(Optional.of(expectedBook)).when(bookRepository).getById(ID_ONE);
        Book actualBook = bookService.getById(ID_ONE).get();
        assertThat(expectedBook.getTitle()).isEqualTo(actualBook.getTitle());
    }

    @Test
    @DisplayName("Должен удалять книгу по ID")
    void shouldDeleteBookById() {
        Genre expectedGenre = new Genre();
        expectedGenre.setId(GENRE_ID_1);
        expectedGenre.setGenreName(FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreRepository).getById(GENRE_ID_1);
        
        Author author = new Author();
        author.setId(1L);
        author.setFullName(AUTHOR_1);
        
        Book book1 = new Book();
        book1.setId(ID_ONE);
        book1.setTitle(BOOK_TITLE_1);
        book1.setAuthor(author);
        book1.setGenres(List.of(expectedGenre));
        
        Book book2 = new Book();
        book2.setId(ID_TWO);
        book2.setTitle(BOOK_TITLE_2);
        book2.setAuthor(author);
        book2.setGenres(List.of(expectedGenre));
        
        List<Book> books = new ArrayList<>(List.of(book1, book2));
        doAnswer(i -> {
            books.removeIf(b -> b.getId().equals(ID_ONE));
            return null;
        }).when(bookRepository).deleteById(ID_ONE);
        doReturn(books).when(bookRepository).getAll();
        bookService.deleteById(ID_ONE);

        assertThat(bookService.getAll()).doesNotContain(book1);
    }
}