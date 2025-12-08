package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.vivlev.library.domain.Author;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@DataJpaTest
@Import(AuthorRepositoryJpa.class)
@DisplayName("The AuthorRepositoryJpa class")
class AuthorRepositoryJpaTest {

    private static final String NEW_AUTHOR = "Лукьяненко, С.";
    private static final int EXPECTED_LIST_AUTHORS_SIZE = 3;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        Author author = new Author();
        author.setFullName(NEW_AUTHOR);
        em.persist(author);
        em.flush();

        assertThat(author.getId()).isNotNull();
        assertThat(authorRepository.getById(author.getId())).isNotEmpty();
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() {
        Author author = new Author();
        author.setFullName(NEW_AUTHOR);
        em.persist(author);
        em.flush();

        List<Author> authors = authorRepository.getAll();
        assertThat(authors).hasSize(EXPECTED_LIST_AUTHORS_SIZE);
        assertThat(authors).anyMatch(a -> NEW_AUTHOR.equals(a.getFullName()));
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        Author author = new Author();
        author.setFullName(NEW_AUTHOR);

        authorRepository.save(author);

        assertThat(author.getId()).isNotNull().isGreaterThan(0L);
    }

    @DisplayName("is checking deleteById method.")
    @Test
    void checkingDeleteById() {
        Author author = new Author();
        author.setFullName(NEW_AUTHOR);
        em.persist(author);
        em.flush();

        Long id = author.getId();
        authorRepository.deleteById(id);
        em.flush();
        em.clear();

        assertThat(authorRepository.getById(id)).isEmpty();
    }
}