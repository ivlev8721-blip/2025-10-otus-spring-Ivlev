package ru.otus.vivlev.library.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Genre;
import ru.otus.vivlev.library.repository.BookRepository;
import ru.otus.vivlev.library.repository.GenreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;


@DisplayName("The BookServiceImpl class")
@SpringBootTest
class BookServiceImplTest {

    private static final long ID_ONE = 1;
    private static final long ID_TWO = 2;
    private static final int EXPECTED_LIST_BOOK_SIZE = 1;
    private static final int ZERO = 0;
    private static final long GENRE_ID_1 = 1;
    private static final String FANTASTIC = "Фантастика";
    private static final String BOOK_TITLE_1 = "Стальная крыса идет на войну";
    private static final String BOOK_TITLE_2 = "Стальная крыса поет блюз";

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private GenreService genreService;

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        Genre expectedGenre = new Genre();
        expectedGenre.setId(GENRE_ID_1);
        expectedGenre.setGenreName(FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreRepository).getById(GENRE_ID_1);

        Book expectedBook = new Book();
        expectedBook.setId(ID_ONE);
        expectedBook.setTitle(BOOK_TITLE_1);
        doReturn(Optional.of(expectedBook)).when(bookRepository).getById(ID_ONE);

        Book actualBook = bookService.getById(ID_ONE).get();

        assertThat(actualBook).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("is checking getAll method.")
    @Test
    void checkingGetAll() {
        Genre expectedGenre = new Genre();
        expectedGenre.setId(GENRE_ID_1);
        expectedGenre.setGenreName(FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreRepository).getById(GENRE_ID_1);

        Book expectedBook = new Book();
        expectedBook.setId(ID_ONE);
        expectedBook.setTitle(BOOK_TITLE_1);
        List<Book> expectedBookList = List.of(expectedBook);
        doReturn(expectedBookList).when(bookRepository).getAll();

        List<Book> books = bookService.getAll();

        assertThat(books.size()).isEqualTo(EXPECTED_LIST_BOOK_SIZE);
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        Genre expectedGenre = new Genre();
        expectedGenre.setId(GENRE_ID_1);
        expectedGenre.setGenreName(FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreRepository).getById(GENRE_ID_1);

        Book book = new Book();
        book.setId(ID_ONE);
        book.setTitle(BOOK_TITLE_2);
        doReturn(book).when(bookRepository).save(book);

        Book expectedBook = bookService.save(book);

        assertThat(expectedBook.getId()).isGreaterThan(ZERO);
        assertThat(expectedBook.getTitle()).isEqualTo(BOOK_TITLE_2);
    }

    @DisplayName("is checking update method.")
    @Test
    void checkingUpdate() {
        Genre expectedGenre = new Genre();
        expectedGenre.setId(GENRE_ID_1);
        expectedGenre.setGenreName(FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreRepository).getById(GENRE_ID_1);

        Book book = new Book();
        book.setId(ID_ONE);
        book.setTitle(BOOK_TITLE_1);
        doReturn(Optional.of(book)).when(bookRepository).getById(ID_ONE);

        Book expectedBook = bookService.getById(ID_ONE).get();
        expectedBook.setTitle(BOOK_TITLE_2);
        doReturn(expectedBook).when(bookRepository).update(expectedBook);

        bookService.update(expectedBook);
        doReturn(Optional.of(expectedBook)).when(bookRepository).getById(ID_ONE);

        Book actualBook = bookService.getById(ID_ONE).get();
        assertThat(expectedBook.getTitle()).isEqualTo(actualBook.getTitle());
    }

    @DisplayName("is checking deleteById method.")
    @Test
    void checkingDeleteById() {
        Genre expectedGenre = new Genre();
        expectedGenre.setId(GENRE_ID_1);
        expectedGenre.setGenreName(FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreRepository).getById(GENRE_ID_1);

        Book book1 = new Book();
        book1.setId(ID_ONE);
        book1.setTitle(BOOK_TITLE_1);
        Book book2 = new Book();
        book2.setId(ID_TWO);
        book2.setTitle(BOOK_TITLE_2);
        List<Book> books = new ArrayList<>(List.of(book1, book2));

        doAnswer(i -> books.remove(0)).when(bookRepository).deleteById(ID_ONE);
        doReturn(books).when(bookRepository).getAll();

        bookService.deleteById(ID_ONE);

        assertThat(bookService.getAll()).doesNotContain(book1);
    }
}