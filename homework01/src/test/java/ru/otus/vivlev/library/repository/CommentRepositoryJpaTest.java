package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Comment;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({CommentRepositoryJpa.class})
@DisplayName("Тестирование CommentRepositoryJpa")
@Sql(scripts = {"/schema.sql", "/data-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryJpaTest {

    public static final int EXPECTED_LIST_COMMENT_SIZE = 3;
    public static final long COMMENT_ID = 1L;
    public static final long BOOK_ID = 1L;
    public static final String USER_NAME = "TEST_USER";
    public static final String COMMENT_TEXT = "Тестовый комментарий";

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Должен находить комментарий по ID")
    void shouldFindCommentById() {
        Optional<Comment> commentOpt = commentRepository.getById(COMMENT_ID);
        
        assertThat(commentOpt).isPresent();
        assertThat(commentOpt.get().getUserName()).isEqualTo("ADMIN");
        assertThat(commentOpt.get().getText()).isEqualTo("Классная книга, рекомендую!");
    }

    @Test
    @DisplayName("Должен возвращать все комментарии")
    void shouldReturnAllComments() {
        List<Comment> comments = commentRepository.getAll();
        assertThat(comments).hasSize(EXPECTED_LIST_COMMENT_SIZE);
    }

    @Test
    @DisplayName("Должен сохранять новый комментарий")
    void shouldSaveNewComment() {
        Book book = em.find(Book.class, BOOK_ID);
        Comment comment = new Comment();
        comment.setBook(book);
        comment.setUserName(USER_NAME);
        comment.setText(COMMENT_TEXT);

        Comment savedComment = commentRepository.save(comment);
        em.flush();
        em.clear();
        
        assertThat(savedComment.getId()).isNotNull().isGreaterThan(0L);
        assertThat(savedComment.getUserName()).isEqualTo(USER_NAME);
        assertThat(savedComment.getText()).isEqualTo(COMMENT_TEXT);
        assertThat(savedComment.getBook().getId()).isEqualTo(BOOK_ID);
    }

    @Test
    @DisplayName("Должен удалять комментарий по ID")
    void shouldDeleteCommentById() {
        // Создаем новый комментарий для удаления
        Book book = em.find(Book.class, BOOK_ID);
        Comment comment = new Comment();
        comment.setBook(book);
        comment.setUserName(USER_NAME);
        comment.setText(COMMENT_TEXT);
        Comment savedComment = em.persistAndFlush(comment);
        em.clear(); // очищаем контекст
        
        long commentId = savedComment.getId();
        assertThat(commentRepository.getById(commentId)).isPresent();

        commentRepository.deleteById(commentId);
        em.flush(); // принудительно синхронизируем состояние
        em.clear(); // очищаем контекст

        assertThat(commentRepository.getById(commentId)).isEmpty();
    }
}