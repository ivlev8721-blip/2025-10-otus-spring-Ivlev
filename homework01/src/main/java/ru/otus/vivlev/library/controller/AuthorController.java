package ru.otus.vivlev.library.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.vivlev.library.domain.Author;
import ru.otus.vivlev.library.service.AuthorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping(value = "/api/v1/author/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Author> getById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getById(id).orElseThrow());
    }

    @GetMapping(value = "/api/v1/author", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Author>> getAll() {
        return ResponseEntity.ok(authorService.getAll());
    }

    @PostMapping(value = "/api/v1/author", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Author> saveAuthor(@RequestBody Author author) {
        return ResponseEntity.ok(authorService.save(author));
    }

    @DeleteMapping(value = "/api/v1/author/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable("id") Long id) {
        authorService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
