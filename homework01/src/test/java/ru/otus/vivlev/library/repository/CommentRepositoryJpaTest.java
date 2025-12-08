package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@DataJpaTest
@Import({CommentRepositoryJpa.class})
@DisplayName("The CommentRepositoryJpa class")
class CommentRepositoryJpaTest {

    private static final int EXPECTED_LIST_COMMENT_SIZE = 3;
    private static final int ZERO = 0;
    private static final long COMMENT_ID = 1;
    private static final Long BOOK_ID = 1L;
    private static final String ADMIN = "ADMIN";
    private static final String COMMENT_1 = "Чушь!";

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        Book book = em.find(Book.class, BOOK_ID);
        Comment comment = new Comment();
        comment.setBook(book);
        comment.setUserName(ADMIN);
        comment.setText(COMMENT_1);
        em.persist(comment);
        em.flush();

        assertThat(commentRepository.getById(comment.getId())).isNotEmpty();
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() {
        List<Comment> comments = commentRepository.getAll();
        assertThat(comments.size()).isEqualTo(EXPECTED_LIST_COMMENT_SIZE);
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        Book book = em.find(Book.class, BOOK_ID);
        Comment comment = new Comment();
        comment.setBook(book);
        comment.setUserName(ADMIN);
        comment.setText(COMMENT_1);
        commentRepository.save(comment);

        assertThat(comment.getId()).isGreaterThan(ZERO);
    }

    @DisplayName("is checking deleteById method.")
    @Test
    void checkingDeleteById() {
        Comment comment = em.find(Comment.class, COMMENT_ID);
        assertThat(comment.getId()).isEqualTo(COMMENT_ID);

        commentRepository.deleteById(comment.getId());

        assertThat(commentRepository.getAll()).doesNotContain(comment);
    }
}