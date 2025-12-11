package ru.otus.vivlev.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Comment;
import ru.otus.vivlev.library.domain.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShellService {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;

    public List<Book> findAllBook() {
        return bookService.getAll();
    }

    public void deleteBookByID(Long id) {
        bookService.deleteById(id);
    }

    public Book saveBook(Long id, String bookTitle, String author, String genres) {
        Book bookTmp = new Book();
        bookTmp.setTitle(bookTitle);
        bookTmp.setAuthor(getAuthor(author));
        bookTmp.setGenres(getGenres(genres));
        if (id == null) {
            return bookService.save(bookTmp);
        } else {
            bookTmp.setId(id);
            bookService.update(bookTmp);
            return bookTmp;
        }
    }

    public Comment saveComment(Long bookId, String userLogin, String text) {
        Comment comment = new Comment();
        comment.setUserName(userLogin);
        comment.setText(text);
        bookService.getById(bookId).ifPresent(book -> {
            comment.setBook(book);
            commentService.save(comment);
        });
        return comment;
    }

    public List<Comment> getAllCommentByBookID(Long bookId) {
        return commentService.getCommentByBookId(bookId);
    }

    private List<Genre> getGenres(String genres) {
        List<Genre> genresForSave = new ArrayList<>();
        if (!genres.isEmpty()) {
            String[] genresArr = genres.replaceAll(" ", "").split(",");
            for (String genreName : genresArr) {
                Optional<Genre> genre = genreService.getByName(genreName);
                genre.ifPresent(genresForSave::add);
            }
        }
        return genresForSave;
    }

    private Author getAuthor(String authorName) {
        Author author = null;
        if (!authorName.isEmpty()) {
            Optional<Author> authorFromDB = authorService.getByName(authorName);
            if (!authorFromDB.isEmpty()) {
                author = authorFromDB.get();
            }
        }
        return author;
    }
}
