package ru.otus.vivlev.library.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.mapper.AuthorMapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class AuthorJdbcImpl implements AuthorJdbc {

    private final NamedParameterJdbcOperations jdbcOperations;

    public AuthorJdbcImpl(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public Optional<Author> getById(long id) {
        return jdbcOperations.query("select id, full_name from author where id = :id", Map.of("id", id), new AuthorMapper()).stream().findFirst();
    }

    @Override
    public Optional<Author> getByName(String name) {
        return jdbcOperations.query("select id, full_name from author where full_name = :name", Map.of("name", name), new AuthorMapper()).stream().findFirst();
    }

    @Override
    public List<Author> getAll() {
        return jdbcOperations.query("select id, full_name from author", new AuthorMapper());
    }

    @Override
    public Author save(Author author) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("full_name", author.getFullName());
        KeyHolder key = new GeneratedKeyHolder();
        jdbcOperations.update("insert into author (full_name) values (:full_name)", params, key);
        return new Author(Objects.requireNonNull(key.getKey()).longValue(), author.getFullName());
    }

    @Override
    public void deleteById(long id) {
        jdbcOperations.update("delete from author where id = :id", Map.of("id", id));
    }
}
