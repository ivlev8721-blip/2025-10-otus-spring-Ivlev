package ru.otus.vivlev.library.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Genre;
import ru.otus.vivlev.library.repository.BookJdbc;
import ru.otus.vivlev.library.repository.GenreJdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;
import static ru.otus.vivlev.library.service.GenreServiceImplTest.FANTASTIC;
import static ru.otus.vivlev.library.service.GenreServiceImplTest.GENRE_ID_1;

@DisplayName("The BookServiceImpl class")
@SpringBootTest
class BookServiceImplTest {

    public static final int ID_ONE = 1;
    public static final int ID_TWO = 2;
    public static final int EXPECTED_LIST_BOOK_SIZE = 1;
    public static final int ZERO = 0;
    public static final String BOOK_TITLE_1 = "Стальная крыса идет на войну";
    public static final String BOOK_TITLE_2 = "Стальная крыса поет блюз";
    public static final String AUTHOR_1 = "Гаррисон, Г.";

    @MockBean
    private GenreJdbc genreJdbc;

    @MockBean
    private BookJdbc bookJdbc;

    @Autowired
    private BookService bookService;

    @Autowired
    private GenreService genreService;

    @DisplayName("is checking getById method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetById() {
        Genre expectedGenre = new Genre(GENRE_ID_1, FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreJdbc).getById(GENRE_ID_1);
        Book expectedBook = new Book(ID_ONE, BOOK_TITLE_1, new Author(ID_ONE, AUTHOR_1), List.of(genreService.getById(ID_ONE).get()));
        doReturn(Optional.of(expectedBook)).when(bookJdbc).getById(ID_ONE);
        Book actualBook = bookService.getById(ID_ONE).get();
        assertThat(actualBook).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() {
        Genre expectedGenre = new Genre(GENRE_ID_1, FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreJdbc).getById(GENRE_ID_1);
        List<Book> expectedBookList = List.of(new Book(ID_ONE, BOOK_TITLE_1, new Author(ID_ONE, AUTHOR_1), List.of(genreService.getById(ID_ONE).get())));
        doReturn(expectedBookList).when(bookJdbc).getAll();
        List<Book> books = bookService.getAll();
        assertThat(books.size()).isEqualTo(EXPECTED_LIST_BOOK_SIZE);
    }

    @DisplayName("is checking save method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingSave() {
        Genre expectedGenre = new Genre(GENRE_ID_1, FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreJdbc).getById(GENRE_ID_1);
        Book book = new Book(ID_ONE, BOOK_TITLE_2, new Author(ID_ONE, AUTHOR_1), List.of(genreService.getById(ID_ONE).get()));
        doReturn(book).when(bookJdbc).save(book);
        Book expectedBook = bookService.save(book);
        assertThat(expectedBook.getId()).isGreaterThan(ZERO);
        assertThat(expectedBook.getTitle()).isEqualTo(BOOK_TITLE_2);
    }

    @DisplayName("is checking update method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingUpdate() {
        Genre expectedGenre = new Genre(GENRE_ID_1, FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreJdbc).getById(GENRE_ID_1);
        Book book = new Book(ID_ONE, BOOK_TITLE_1, new Author(ID_ONE, AUTHOR_1), List.of(genreService.getById(ID_ONE).get()));
        doReturn(Optional.of(book)).when(bookJdbc).getById(ID_ONE);
        Book expectedBook = bookService.getById(ID_ONE).get();
        expectedBook.setTitle(BOOK_TITLE_2);
        doNothing().when(bookJdbc).update(expectedBook);
        bookService.update(expectedBook);
        doReturn(Optional.of(expectedBook)).when(bookJdbc).getById(ID_ONE);
        Book actualBook = bookService.getById(ID_ONE).get();
        assertThat(expectedBook.getTitle()).isEqualTo(actualBook.getTitle());
    }

    @DisplayName("is checking deleteById method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingDeleteById() {
        Genre expectedGenre = new Genre(GENRE_ID_1, FANTASTIC);
        doReturn(Optional.of(expectedGenre)).when(genreJdbc).getById(GENRE_ID_1);
        Book book1 = new Book(ID_ONE, BOOK_TITLE_1, new Author(ID_ONE, AUTHOR_1), List.of(genreService.getById(ID_ONE).get()));
        Book book2 = new Book(ID_TWO, BOOK_TITLE_2, new Author(ID_ONE, AUTHOR_1), List.of(genreService.getById(ID_ONE).get()));
        List<Book> books = new ArrayList<>(List.of(book1, book2));
        doAnswer(i -> books.remove(0)).when(bookJdbc).deleteById(ID_ONE);
        doReturn(books).when(bookJdbc).getAll();
        bookService.deleteById(ID_ONE);

        assertThat(bookService.getAll()).doesNotContain(book1);
    }
}