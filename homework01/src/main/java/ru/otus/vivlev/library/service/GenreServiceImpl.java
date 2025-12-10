package ru.otus.vivlev.library.service;

import org.springframework.stereotype.Service;
import ru.otus.vivlev.library.repository.GenreJdbc;
import ru.otus.vivlev.library.domain.Genre;

import java.util.List;
import java.util.Optional;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreJdbc genreJdbc;

    public GenreServiceImpl(GenreJdbc genreJdbc) {
        this.genreJdbc = genreJdbc;
    }

    @Override
    public Optional<Genre> getById(long id) {
        return genreJdbc.getById(id);
    }

    @Override
    public Optional<Genre> getByName(String name) {
        return genreJdbc.getByName(name);
    }

    @Override
    public List<Genre> getAll() {
        return genreJdbc.getAll();
    }

    @Override
    public Genre save(Genre genre) {
        return genreJdbc.save(genre);
    }
}
