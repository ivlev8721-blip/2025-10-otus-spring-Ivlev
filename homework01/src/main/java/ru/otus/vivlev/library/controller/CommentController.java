package ru.otus.vivlev.library.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.vivlev.library.domain.Comment;
import ru.otus.vivlev.library.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping(value = "/api/v1/comment/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Comment> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getById(id).orElseThrow());
    }

    @GetMapping(value = "/api/v1/comment/book/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Comment>> getAllCommentsByBookId(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentByBookId(id));
    }

    @PostMapping(value = "/api/v1/comment", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Comment> saveComment(@RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.save(comment));
    }

    @DeleteMapping(value = "/api/v1/comment/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Long id) {
        commentService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
