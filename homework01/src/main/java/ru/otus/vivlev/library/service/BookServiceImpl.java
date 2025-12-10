package ru.otus.vivlev.library.service;

import org.springframework.stereotype.Service;
import ru.otus.vivlev.library.repository.BookJdbc;
import ru.otus.vivlev.library.domain.Book;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookJdbc bookJdbc;

    public BookServiceImpl(BookJdbc bookJdbc) {
        this.bookJdbc = bookJdbc;
    }

    @Override
    public Optional<Book> getById(long id) {
        return bookJdbc.getById(id);
    }

    @Override
    public List<Book> getAll() {
        return bookJdbc.getAll();
    }

    @Override
    public Book save(Book book) {
        return bookJdbc.save(book);
    }

    @Override
    public void update(Book book) {
        bookJdbc.update(book);
    }

    @Override
    public void deleteById(long id) {
        bookJdbc.deleteById(id);
    }
}
