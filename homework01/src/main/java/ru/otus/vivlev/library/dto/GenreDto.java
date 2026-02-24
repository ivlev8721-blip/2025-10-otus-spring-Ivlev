package ru.otus.vivlev.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {

    private Long id;

    @NotBlank(message = "Название жанра не может быть пустым")
    @Size(min = 2, max = 100, message = "Название жанра должно быть от 2 до 100 символов")
    private String genreName;
}
