package ru.otus.vivlev.library.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.vivlev.library.domain.Genre;
import ru.otus.vivlev.library.mapper.GenreMapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class GenreJdbcImpl implements GenreJdbc {

    private final NamedParameterJdbcOperations jdbcOperations;

    public GenreJdbcImpl(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public Optional<Genre> getById(long id) {
        return jdbcOperations.query("select id, genre_name from genre where id = :id", Map.of("id", id), new GenreMapper()).stream().findFirst();
    }

    @Override
    public Optional<Genre> getByName(String name) {
        return jdbcOperations.query("select id, genre_name from genre where genre_name = :name", Map.of("name", name), new GenreMapper()).stream().findFirst();
    }

    @Override
    public List<Genre> getAll() {
        return jdbcOperations.query("select id, genre_name from genre", new GenreMapper());
    }

    @Override
    public Genre save(Genre genre) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("genre_name", genre.getGenreName());
        KeyHolder key = new GeneratedKeyHolder();
        jdbcOperations.update("insert into genre (genre_name) values (:genre_name)", params, key);
        return new Genre(Objects.requireNonNull(key.getKey()).longValue(), genre.getGenreName());
    }
}
