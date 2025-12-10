package ru.otus.vivlev.library.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Genre;
import ru.otus.vivlev.library.exception.BookRemoveException;
import ru.otus.vivlev.library.service.AuthorService;
import ru.otus.vivlev.library.service.BookService;
import ru.otus.vivlev.library.service.GenreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ShellComponent
public class LibraryShell {

    public static final String ADDED_SUCCESSFULLY = "The book added successfully";
    public static final String UPDATED_SUCCESSFULLY = "The book updated successfully";
    public static final String DELETE_SUCCESSFULLY = "The book deleted successfully";

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    public LibraryShell(BookService bookService, AuthorService authorService, GenreService genreService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    @ShellMethod(key = {"books", "b"}, value = "Display the list of all books: <ID>, <Title>, <Author>, List<Genre>")
    public List<Book> displayAllBooks() {
        return bookService.getAll();
    }

    @ShellMethod(key = {"add-book", "add-b"}, value = "Add a book to library: add-b \"Title\" \"Author\" \"Genre1, Genre2, etc.\"")
    public String addBook(@ShellOption String bookTitle,
                          @ShellOption String authorName,
                          @ShellOption String genres) {
        return saveOrUpdateBook(0, bookTitle, authorName, genres);
    }

    @ShellMethod(key = {"update-book", "upd-b"}, value = "Edit a book by ID: upd-b ID \"Title\" \"Author\" \"Genre1, Genre2\" (Author and Genre are optional)")
    public String updateBook(@ShellOption long id,
                             @ShellOption String bookTitle,
                             @ShellOption(defaultValue = "") String authorName,
                             @ShellOption(defaultValue = "") String genres) {
        return saveOrUpdateBook(id, bookTitle, authorName, genres);
    }

    @ShellMethod(key = {"delete-book", "del-b"}, value = "Remove a book from library by ID:del-b ID")
    public String deleteBook(@ShellOption long id) throws BookRemoveException {
        bookService.deleteById(id);
        return DELETE_SUCCESSFULLY;
    }

    private String saveOrUpdateBook(long id, String bookTitle, String authorName, String genres) {
        String result = ADDED_SUCCESSFULLY;
        List<Genre> genresForSave = getGenres(genres);
        Author author = getAuthor(authorName);
        if (id != 0) {
            Optional<Book> book = bookService.getById(id);
            if (book.isPresent()) {
                if (genresForSave.isEmpty()) {
                    genresForSave = book.get().getGenres();
                }

                if (author.getId() == 0) {
                    author = book.get().getAuthor();
                }
            }
            bookService.update(new Book(id, bookTitle, author, genresForSave));
            result = UPDATED_SUCCESSFULLY;
        } else {
            bookService.save(new Book(bookTitle, author, genresForSave));
        }
        return result;
    }

    private List<Genre> getGenres(String genres) {
        List<Genre> genresForSave = new ArrayList<>();
        if (!genres.isEmpty()) {
            String[] genresArr = genres.replaceAll(" ", "").split(",");
            for (String genreName : genresArr) {
                Optional<Genre> genre = genreService.getByName(genreName);
                if (genre.isPresent()) {
                    genresForSave.add(genre.get());
                } else {
                    genresForSave.add(genreService.save(new Genre(genreName)));
                }
            }
        }
        return genresForSave;
    }

    private Author getAuthor(String authorName) {
        Author author = new Author(0, "");
        if (!authorName.isEmpty()) {
            Optional<Author> authorFromDB = authorService.getByName(authorName);
            if (authorFromDB.isEmpty()) {
                author = authorService.save(new Author(authorName));
            } else {
                author = authorFromDB.get();
            }
        }
        return author;
    }
}
