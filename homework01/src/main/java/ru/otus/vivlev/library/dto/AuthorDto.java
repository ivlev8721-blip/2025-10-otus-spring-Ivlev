package ru.otus.vivlev.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    private Long id;

    @NotBlank(message = "Имя автора не может быть пустым")
    @Size(min = 2, max = 255, message = "Имя автора должно быть от 2 до 255 символов")
    private String fullName;
}
