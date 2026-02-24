package ru.otus.vivlev.library.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Genre;
import ru.otus.vivlev.library.dto.AuthorDto;
import ru.otus.vivlev.library.dto.BookDto;
import ru.otus.vivlev.library.dto.GenreDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DtoMapper")
class DtoMapperTest {

    private final DtoMapper mapper = new DtoMapper();

    @Test
    @DisplayName("should convert Author to AuthorDto")
    void shouldConvertAuthorToDto() {
        Author author = new Author(1L, "Толстой Л.Н.");
        
        AuthorDto dto = mapper.toDto(author);
        
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFullName()).isEqualTo("Толстой Л.Н.");
    }

    @Test
    @DisplayName("should convert AuthorDto to Author")
    void shouldConvertDtoToAuthor() {
        AuthorDto dto = new AuthorDto(1L, "Толстой Л.Н.");
        
        Author author = mapper.toEntity(dto);
        
        assertThat(author.getId()).isEqualTo(1L);
        assertThat(author.getFullName()).isEqualTo("Толстой Л.Н.");
    }

    @Test
    @DisplayName("should convert Genre to GenreDto")
    void shouldConvertGenreToDto() {
        Genre genre = new Genre(1L, "Роман");
        
        GenreDto dto = mapper.toDto(genre);
        
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getGenreName()).isEqualTo("Роман");
    }

    @Test
    @DisplayName("should convert Book to BookDto")
    void shouldConvertBookToDto() {
        Author author = new Author(1L, "Толстой Л.Н.");
        Genre genre = new Genre(1L, "Роман");
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Война и мир");
        book.setAuthor(author);
        book.setGenres(List.of(genre));
        
        BookDto dto = mapper.toDto(book);
        
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Война и мир");
        assertThat(dto.getAuthor().getFullName()).isEqualTo("Толстой Л.Н.");
        assertThat(dto.getGenres()).hasSize(1);
    }

    @Test
    @DisplayName("should handle null values")
    void shouldHandleNullValues() {
        assertThat(mapper.toDto((Author) null)).isNull();
        assertThat(mapper.toEntity((AuthorDto) null)).isNull();
    }
}
