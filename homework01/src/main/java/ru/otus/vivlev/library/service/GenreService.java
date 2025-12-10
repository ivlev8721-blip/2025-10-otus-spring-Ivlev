package ru.otus.vivlev.library.service;

import ru.otus.vivlev.library.domain.Genre;
import java.util.List;
import java.util.Optional;

public interface GenreService {

    Optional<Genre> getById(long id);

    Optional<Genre> getByName(String name);

    List<Genre> getAll();

    Genre save(Genre genre);
}
