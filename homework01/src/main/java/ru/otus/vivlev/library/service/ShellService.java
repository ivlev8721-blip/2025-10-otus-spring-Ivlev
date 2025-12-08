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
        if (id == null) {
            Book book = new Book();
            book.setTitle(bookTitle);
            book.setAuthor(getAuthor(author));
            book.setGenres(getGenres(genres));
            return bookService.save(book);
        }
    
        Book book = bookService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book with id=" + id + " not found"));
        book.setTitle(bookTitle);
    
        if (!author.isEmpty()) {
            book.setAuthor(getAuthor(author));
        }
        if (!genres.isEmpty()) {
            book.setGenres(getGenres(genres));
        }
    
        bookService.update(book);
        return book;
    }

    public Comment saveComment(Long bookId, String userLogin, String text) {
        var book = bookService.getById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book with id=" + bookId + " not found"));
    
        Comment comment = new Comment();
        comment.setUserName(userLogin);
        comment.setText(text);
        comment.setBook(book);
    
        return commentService.save(comment);
    }

    public List<Comment> getAllCommentByBookID(Long bookId) {
        return commentService.getCommentByBookId(bookId);
    }

    private List<Genre> getGenres(String genres) {
        List<Genre> result = new ArrayList<>();
        if (!genres.isEmpty()) {
            for (String raw : genres.split(",")) {
                String name = raw.trim();
                if (name.isEmpty()) {
                    continue;
                }
                result.add(genreService.getByName(name)
                        .orElseGet(() -> genreService.save(new Genre(null, name))));
            }
        }
        return result;
    }

    private Author getAuthor(String authorName) {
        String name = authorName.trim();
        if (name.isEmpty()) {
            return null;
        }
        return authorService.getByName(name)
                .orElseGet(() -> authorService.save(new Author(null, name)));
    }
}
