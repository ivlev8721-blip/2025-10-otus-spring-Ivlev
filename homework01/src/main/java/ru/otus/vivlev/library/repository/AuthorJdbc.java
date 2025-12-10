package ru.otus.vivlev.library.repository;

import ru.otus.vivlev.library.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorJdbc {

    Optional<Author> getById(long id);

    Optional<Author> getByName(String name);

    List<Author> getAll();

    Author save(Author author);

    void deleteById(long id);
}
