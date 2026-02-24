package ru.otus.vivlev.library.mapper;

import org.springframework.stereotype.Component;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Comment;
import ru.otus.vivlev.library.domain.Genre;
import ru.otus.vivlev.library.dto.AuthorDto;
import ru.otus.vivlev.library.dto.BookDto;
import ru.otus.vivlev.library.dto.CommentDto;
import ru.otus.vivlev.library.dto.GenreDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public AuthorDto toDto(Author author) {
        if (author == null) {
            return null;
        }
        return new AuthorDto(author.getId(), author.getFullName());
    }

    public Author toEntity(AuthorDto dto) {
        if (dto == null) {
            return null;
        }
        return new Author(dto.getId(), dto.getFullName());
    }

    public GenreDto toDto(Genre genre) {
        if (genre == null) {
            return null;
        }
        return new GenreDto(genre.getId(), genre.getGenreName());
    }

    public Genre toEntity(GenreDto dto) {
        if (dto == null) {
            return null;
        }
        return new Genre(dto.getId(), dto.getGenreName());
    }

    public BookDto toDto(Book book) {
        if (book == null) {
            return null;
        }
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(toDto(book.getAuthor()));
        dto.setGenres(book.getGenres().stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
        return dto;
    }

    public Book toEntity(BookDto dto) {
        if (dto == null) {
            return null;
        }
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setAuthor(toEntity(dto.getAuthor()));
        book.setGenres(dto.getGenres().stream()
                .map(this::toEntity)
                .collect(Collectors.toList()));
        return book;
    }

    public CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setUserName(comment.getUserName());
        dto.setText(comment.getText());
        if (comment.getBook() != null) {
            dto.setBookId(comment.getBook().getId());
        }
        return dto;
    }

    public Comment toEntity(CommentDto dto, Book book) {
        if (dto == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setUserName(dto.getUserName());
        comment.setText(dto.getText());
        comment.setBook(book);
        return comment;
    }

    public List<AuthorDto> toAuthorDtoList(List<Author> authors) {
        return authors.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<GenreDto> toGenreDtoList(List<Genre> genres) {
        return genres.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<BookDto> toBookDtoList(List<Book> books) {
        return books.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<CommentDto> toCommentDtoList(List<Comment> comments) {
        return comments.stream().map(this::toDto).collect(Collectors.toList());
    }
}
