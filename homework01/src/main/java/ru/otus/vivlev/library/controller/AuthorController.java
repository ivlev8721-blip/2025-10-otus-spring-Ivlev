package ru.otus.vivlev.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.vivlev.library.dto.AuthorDto;
import ru.otus.vivlev.library.exeption.ResourceNotFoundException;
import ru.otus.vivlev.library.mapper.DtoMapper;
import ru.otus.vivlev.library.service.AuthorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/author")
@RequiredArgsConstructor
@Tag(name = "Авторы", description = "API для управления авторами")
public class AuthorController {

    private final AuthorService authorService;
    private final DtoMapper mapper;

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    @Operation(summary = "Получить автора по ID")
    public ResponseEntity<AuthorDto> getById(@PathVariable Long id) {
        return authorService.getById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Автор с ID " + id + " не найден"));
    }

    @GetMapping(produces = "application/json;charset=UTF-8")
    @Operation(summary = "Получить всех авторов")
    public ResponseEntity<List<AuthorDto>> getAll() {
        return ResponseEntity.ok(mapper.toAuthorDtoList(authorService.getAll()));
    }

    @PostMapping(produces = "application/json;charset=UTF-8")
    @Operation(summary = "Создать нового автора")
    public ResponseEntity<AuthorDto> createAuthor(@Valid @RequestBody AuthorDto authorDto) {
        authorDto.setId(null);
        var saved = authorService.save(mapper.toEntity(authorDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(saved));
    }

    @PutMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    @Operation(summary = "Обновить автора")
    public ResponseEntity<AuthorDto> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorDto authorDto) {
        authorService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Автор с ID " + id + " не найден"));
        authorDto.setId(id);
        var updated = authorService.save(mapper.toEntity(authorDto));
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить автора")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Автор с ID " + id + " не найден"));
        authorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
