package ru.otus.vivlev.library.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.otus.vivlev.library.domain.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreMapper implements RowMapper<Genre> {

    @Override
    public Genre mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Genre(resultSet.getLong("id"), resultSet.getString("genre_name"));
    }
}
