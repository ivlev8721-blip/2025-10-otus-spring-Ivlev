package ru.otus.vivlev.library.service;

import ru.otus.vivlev.library.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Optional<Book> getById(Long id);

    List<Book> getAll();

    Book save(Book book);

    void update(Book book);

    void deleteById(Long id);
}
