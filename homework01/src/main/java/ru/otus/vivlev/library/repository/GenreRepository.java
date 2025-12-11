package ru.otus.vivlev.library.repository;

import ru.otus.vivlev.library.domain.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {

    Optional<Genre> getById(Long id);

    Optional<Genre> getByName(String name);

    List<Genre> getAll();

    Genre save(Genre genre);

    void deleteById(Long id);
}
