package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.vivlev.library.domain.Book;
import ru.otus.vivlev.library.domain.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@DataJpaTest
@DisplayName("The CommentRepository class")
class CommentRepositoryTest {

    public static final int EXPECTED_LIST_COMMENT_SIZE = 3;
    public static final int ZERO = 0;
    public static final long COMMENT_ID = 1;
    public static final Long BOOK_ID = 1L;
    public static final String ADMIN = "ADMIN";
    public static final String COMMENT_1 = "Чушь!";

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
        assertThat(commentRepository.findById(comment.getId())).isNotEmpty();
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() {
        List<Comment> genres = commentRepository.findAll();
        assertThat(genres.size()).isEqualTo(EXPECTED_LIST_COMMENT_SIZE);
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

        assertThat(commentRepository.findAll()).doesNotContain(comment);
    }
}