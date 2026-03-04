package ru.otus.vivlev.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;

    @NotBlank(message = "Название книги не может быть пустым")
    @Size(min = 1, max = 255, message = "Название книги должно быть от 1 до 255 символов")
    private String title;

    @NotNull(message = "Автор книги обязателен")
    private AuthorDto author;

    private List<GenreDto> genres = new ArrayList<>();
}
