package ru.otus.vivlev.library.repository;

import ru.otus.vivlev.library.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    Optional<Book> getById(Long id);

    List<Book> getAll();

    Book save(Book book);

    Book update(Book book);

    void deleteById(Long id);
}
