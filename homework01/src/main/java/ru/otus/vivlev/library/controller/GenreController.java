package ru.otus.vivlev.library.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.vivlev.library.domain.Genre;
import ru.otus.vivlev.library.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping(value = "/api/v1/genre/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Genre> getById(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.getById(id).orElseThrow());
    }

    @GetMapping(value = "/api/v1/genre", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Genre>> getAll() {
        return ResponseEntity.ok(genreService.getAll());
    }

    @PostMapping(value = "/api/v1/genre", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Genre> saveGenre(@RequestBody Genre genre) {
        return ResponseEntity.ok(genreService.save(genre));
    }

    @DeleteMapping(value = "/api/v1/genre/{id}")
    public ResponseEntity<?> deleteGenre(@PathVariable("id") Long id) {
        genreService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
