package ru.otus.vivlev.library.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.vivlev.library.mapper.BookMapper;
import ru.otus.vivlev.library.repository.ext.BookGenreRelation;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Genre;
import ru.otus.vivlev.library.mapper.BookResultSetExtractor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class BookJdbcImpl implements BookJdbc {

    private final NamedParameterJdbcOperations jdbcOperations;

    private final GenreJdbc genreJdbc;

    public BookJdbcImpl(NamedParameterJdbcOperations jdbcOperations, GenreJdbc genreJdbc) {
        this.jdbcOperations = jdbcOperations;
        this.genreJdbc = genreJdbc;
    }

    @Override
    public Optional<Book> getById(long id) {
        Map<Long, Book> books =
                jdbcOperations.query("select b.id, b.title, a.id author_id, a.full_name, g.id genre_id, g.genre_name " +
                                "from (book b left join author a on b.author_id = a.id " +
                                "left join book_genre bg on b.id = bg.book_id " +
                                "left join genre g on bg.genre_id = g.id) " +
                                "where b.id = :id", Map.of("id", id),
                        new BookResultSetExtractor());
        return Optional.ofNullable(Objects.requireNonNull(books).get(id));
    }

    @Override
    public List<Book> getAll() {
        List<Genre> genres = genreJdbc.getAll();
        List<BookGenreRelation> relations = getAllRelations();
        List<Book> books =
                jdbcOperations.query("select b.id, b.title, a.id author_id, a.full_name " +
                                "from (book b left join author a on b.author_id = a.id) ",
                        new BookMapper());

        mergeBooksInfo(books, genres, relations);
        return Objects.requireNonNull(books);
    }

    @Override
    public Book save(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        KeyHolder key = new GeneratedKeyHolder();
        jdbcOperations.update("insert into book (title, author_id) values (:title, :author_id)", params, key);

        insertIntoBookGenre(book, key.getKey().longValue());
        book.setId(key.getKey().longValue());
        return book;
    }

    @Override
    public void update(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", book.getId());
        params.addValue("title", book.getTitle());
        if (book.getAuthor().getId() != 0) {
            params.addValue("author_id", book.getAuthor().getId());
            jdbcOperations.update("update book set title = :title, author_id = :author_id where id = :id", params);
        } else {
            jdbcOperations.update("update book set title = :title where id = :id", params);
        }

        if (!book.getGenres().isEmpty()) {
            jdbcOperations.update("delete from book_genre where book_id = :id", Map.of("id", book.getId()));
            insertIntoBookGenre(book, book.getId());
        }
    }

    private void insertIntoBookGenre(Book book, long id) {
        for (Genre genre : book.getGenres()) {
            jdbcOperations.update("insert into book_genre (book_id, genre_id) values (:book_id, :genre_id)",
                    Map.of("book_id", id, "genre_id", genre.getId()));
        }
    }

    @Override
    public void deleteById(long id) {
        jdbcOperations.update("delete from book_genre where book_id = :id", Map.of("id", id));
        jdbcOperations.update("delete from book where id = :id", Map.of("id", id));
    }

    private List<BookGenreRelation> getAllRelations() {
        return jdbcOperations.query("select book_id, genre_id from book_genre sc order by book_id, genre_id",
                (rs, i) -> new BookGenreRelation(rs.getLong(1), rs.getLong(2)));
    }

    private void mergeBooksInfo(List<Book> books, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Genre> genreMap = genres.stream().collect(Collectors.toMap(Genre::getId, Function.identity()));
        Map<Long, Book> bookMap = books.stream().collect(Collectors.toMap(Book::getId, Function.identity()));
        relations.forEach(r -> {
            if (bookMap.containsKey(r.getBook_id()) && genreMap.containsKey(r.getGenre_id())) {
                bookMap.get(r.getBook_id()).getGenres().add(genreMap.get(r.getGenre_id()));
            }
        });
    }
}
