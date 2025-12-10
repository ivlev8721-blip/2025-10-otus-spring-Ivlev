package ru.otus.vivlev.library.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.domain.Book;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet resultSet, int i) throws SQLException {
        Author author = new Author(resultSet.getLong("author_id"), resultSet.getString("full_name"));
        return new Book(resultSet.getLong("id"), resultSet.getString("title"), author);
    }
}
