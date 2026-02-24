package ru.otus.vivlev.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.vivlev.library.dto.CommentDto;
import ru.otus.vivlev.library.exeption.ResourceNotFoundException;
import ru.otus.vivlev.library.kafka.event.CommentEvent;
import ru.otus.vivlev.library.kafka.producer.LibraryEventProducer;
import ru.otus.vivlev.library.mapper.DtoMapper;
import ru.otus.vivlev.library.service.BookService;
import ru.otus.vivlev.library.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
@Tag(name = "Комментарии", description = "API для управления комментариями")
public class CommentController {

    private final CommentService commentService;
    private final BookService bookService;
    private final DtoMapper mapper;
    private final LibraryEventProducer eventProducer;

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    @Operation(summary = "Получить комментарий по ID")
    public ResponseEntity<CommentDto> getById(@PathVariable Long id) {
        return commentService.getById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Комментарий с ID " + id + " не найден"));
    }

    @GetMapping(value = "/book/{bookId}", produces = "application/json;charset=UTF-8")
    @Operation(summary = "Получить все комментарии к книге")
    public ResponseEntity<List<CommentDto>> getAllCommentsByBookId(@PathVariable Long bookId) {
        bookService.getById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Книга с ID " + bookId + " не найдена"));
        return ResponseEntity.ok(mapper.toCommentDtoList(commentService.getCommentByBookId(bookId)));
    }

    @PostMapping(produces = "application/json;charset=UTF-8")
    @Operation(summary = "Создать новый комментарий")
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentDto commentDto) {
        var book = bookService.getById(commentDto.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Книга с ID " + commentDto.getBookId() + " не найдена"));
        commentDto.setId(null);
        var saved = commentService.save(mapper.toEntity(commentDto, book));
        var savedDto = mapper.toDto(saved);
        eventProducer.sendCommentEvent(new CommentEvent(CommentEvent.EventType.CREATED, savedDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    @PutMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    @Operation(summary = "Обновить комментарий")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id, @Valid @RequestBody CommentDto commentDto) {
        commentService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Комментарий с ID " + id + " не найден"));
        var book = bookService.getById(commentDto.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Книга с ID " + commentDto.getBookId() + " не найдена"));
        commentDto.setId(id);
        var updated = commentService.save(mapper.toEntity(commentDto, book));
        var updatedDto = mapper.toDto(updated);
        eventProducer.sendCommentEvent(new CommentEvent(CommentEvent.EventType.UPDATED, updatedDto));
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить комментарий")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        var comment = commentService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Комментарий с ID " + id + " не найден"));
        var commentDto = mapper.toDto(comment);
        commentService.deleteById(id);
        eventProducer.sendCommentEvent(new CommentEvent(CommentEvent.EventType.DELETED, commentDto));
        return ResponseEntity.noContent().build();
    }
}
