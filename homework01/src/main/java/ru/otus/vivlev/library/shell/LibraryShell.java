package ru.otus.vivlev.library.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Comment;
import ru.otus.vivlev.library.service.*;
import ru.otus.vivlev.library.service.ShellService;

import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class LibraryShell {

    public static final String DELETE_SUCCESSFULLY = "The book deleted successfully";

    private final ShellService shellService;

    private Boolean isAuth = false;

    private String userLogin;

    @ShellMethod(key = {"a", "auth"}, value = "For authorization enter login: a \"login\"")
    public void auth(
            @ShellOption String login
    ) {
        isAuth = true;
        userLogin = login;
    }

    @ShellMethod(key = {"books", "b"}, value = "Display the list of all books: <ID>, <Title>, <Author>, List<Genre>")
    @ShellMethodAvailability(value = "isAuthUser")
    public List<Book> displayAllBooks() {
        return shellService.findAllBook();
    }

    @ShellMethod(key = {"add-book", "add-b"}, value = "Add a book to library: add-b \"Title\" \"Author\" \"Genre1, Genre2, etc.\"")
    @ShellMethodAvailability(value = "isAuthUser")
    public String addBook(@ShellOption String bookTitle,
                          @ShellOption String authorName,
                          @ShellOption String genres) {
        return shellService.saveBook(null, bookTitle, authorName, genres).toString();
    }

    @ShellMethod(key = {"update-book", "upd-b"}, value = "Edit a book by ID: upd-b ID \"Title\" \"Author\" \"Genre1, Genre2\" (Author and Genre are optional)")
    @ShellMethodAvailability(value = "isAuthUser")
    public String updateBook(@ShellOption long id,
                             @ShellOption String bookTitle,
                             @ShellOption(defaultValue = "") String authorName,
                             @ShellOption(defaultValue = "") String genres) {
        return shellService.saveBook(id, bookTitle, authorName, genres).toString();
    }

    @ShellMethod(key = {"delete-book", "del-b"}, value = "Remove a book from library by ID:del-b ID")
    @ShellMethodAvailability(value = "isAuthUser")
    public String deleteBook(@ShellOption Long id) {
        shellService.deleteBookByID(id);
        return DELETE_SUCCESSFULLY;
    }

    @ShellMethod(key = {"create-comment", "cc"}, value = "Create comment: cc ID \"Your comment\"")
    @ShellMethodAvailability(value = "isAuthUser")
    public void createComment(
            @ShellOption Long bookId,
            @ShellOption String text
    ) {
        shellService.saveComment(bookId, userLogin, text);
    }

    @ShellMethod(key = {"get-all-comment-by-book-id", "gac"}, value = "gac ID. Display the list of all comments: <ID>, <User>, <Text>, book_id")
    @ShellMethodAvailability(value = "isAuthUser")
    public List<Comment> findAllCommentByBookId(@ShellOption Long bookId) {
        return shellService.getAllCommentByBookID(bookId);
    }

    private Availability isAuthUser() {
        if (!isAuth) {
            return Availability.unavailable("Please log in!");
        } else {
            return Availability.available();
        }
    }
}
