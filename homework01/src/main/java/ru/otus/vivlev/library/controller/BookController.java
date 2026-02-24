package ru.otus.vivlev.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.vivlev.library.dto.BookDto;
import ru.otus.vivlev.library.exeption.ResourceNotFoundException;
import ru.otus.vivlev.library.kafka.event.BookEvent;
import ru.otus.vivlev.library.kafka.producer.LibraryEventProducer;
import ru.otus.vivlev.library.mapper.DtoMapper;
import ru.otus.vivlev.library.service.BookService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
@Tag(name = "Книги", description = "API для управления книгами")
public class BookController {

    private final BookService bookService;
    private final DtoMapper mapper;
    private final LibraryEventProducer eventProducer;

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    @Operation(summary = "Получить книгу по ID")
    public ResponseEntity<BookDto> getById(@PathVariable Long id) {
        return bookService.getById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Книга с ID " + id + " не найдена"));
    }

    @GetMapping(produces = "application/json;charset=UTF-8")
    @Operation(summary = "Получить все книги")
    public ResponseEntity<List<BookDto>> getAll() {
        return ResponseEntity.ok(mapper.toBookDtoList(bookService.getAll()));
    }

    @PostMapping(produces = "application/json;charset=UTF-8")
    @Operation(summary = "Создать новую книгу")
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
        bookDto.setId(null);
        var saved = bookService.save(mapper.toEntity(bookDto));
        var savedDto = mapper.toDto(saved);
        eventProducer.sendBookEvent(new BookEvent(BookEvent.EventType.CREATED, savedDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    @PutMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    @Operation(summary = "Обновить книгу")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
        bookService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Книга с ID " + id + " не найдена"));
        bookDto.setId(id);
        var updated = bookService.update(mapper.toEntity(bookDto));
        var updatedDto = mapper.toDto(updated);
        eventProducer.sendBookEvent(new BookEvent(BookEvent.EventType.UPDATED, updatedDto));
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить книгу")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        var book = bookService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Книга с ID " + id + " не найдена"));
        var bookDto = mapper.toDto(book);
        bookService.deleteById(id);
        eventProducer.sendBookEvent(new BookEvent(BookEvent.EventType.DELETED, bookDto));
        return ResponseEntity.noContent().build();
    }
}
