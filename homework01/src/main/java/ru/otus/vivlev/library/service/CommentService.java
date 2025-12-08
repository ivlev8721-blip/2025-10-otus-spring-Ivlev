package ru.otus.vivlev.library.service;

import ru.otus.vivlev.library.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Optional<Comment> getById(Long id);

    List<Comment> getAll();

    Comment save(Comment comment);

    public List<Comment> getCommentByBookId(Long bookId);

    void deleteById(Long id);
}
