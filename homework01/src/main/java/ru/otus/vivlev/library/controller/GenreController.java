package ru.otus.vivlev.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.vivlev.library.dto.GenreDto;
import ru.otus.vivlev.library.exeption.ResourceNotFoundException;
import ru.otus.vivlev.library.kafka.event.GenreEvent;
import ru.otus.vivlev.library.kafka.producer.LibraryEventProducer;
import ru.otus.vivlev.library.mapper.DtoMapper;
import ru.otus.vivlev.library.service.GenreService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/genre")
@RequiredArgsConstructor
@Tag(name = "Жанры", description = "API для управления жанрами")
public class GenreController {

    private final GenreService genreService;
    private final DtoMapper mapper;
    private final LibraryEventProducer eventProducer;

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    @Operation(summary = "Получить жанр по ID")
    public ResponseEntity<GenreDto> getById(@PathVariable Long id) {
        return genreService.getById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Жанр с ID " + id + " не найден"));
    }

    @GetMapping(produces = "application/json;charset=UTF-8")
    @Operation(summary = "Получить все жанры")
    public ResponseEntity<List<GenreDto>> getAll() {
        return ResponseEntity.ok(mapper.toGenreDtoList(genreService.getAll()));
    }

    @PostMapping(produces = "application/json;charset=UTF-8")
    @Operation(summary = "Создать новый жанр")
    public ResponseEntity<GenreDto> createGenre(@Valid @RequestBody GenreDto genreDto) {
        genreDto.setId(null);
        var saved = genreService.save(mapper.toEntity(genreDto));
        var savedDto = mapper.toDto(saved);
        eventProducer.sendGenreEvent(new GenreEvent(GenreEvent.EventType.CREATED, savedDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    @PutMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    @Operation(summary = "Обновить жанр")
    public ResponseEntity<GenreDto> updateGenre(@PathVariable Long id, @Valid @RequestBody GenreDto genreDto) {
        genreService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Жанр с ID " + id + " не найден"));
        genreDto.setId(id);
        var updated = genreService.save(mapper.toEntity(genreDto));
        var updatedDto = mapper.toDto(updated);
        eventProducer.sendGenreEvent(new GenreEvent(GenreEvent.EventType.UPDATED, updatedDto));
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить жанр")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        var genre = genreService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Жанр с ID " + id + " не найден"));
        var genreDto = mapper.toDto(genre);
        genreService.deleteById(id);
        eventProducer.sendGenreEvent(new GenreEvent(GenreEvent.EventType.DELETED, genreDto));
        return ResponseEntity.noContent().build();
    }
}
