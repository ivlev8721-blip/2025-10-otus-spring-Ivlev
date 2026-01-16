package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.vivlev.library.domain.Author;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@DataJpaTest
@DisplayName("The AuthorRepository class")
class AuthorRepositoryTest {

    public static final String AUTHOR_1 = "Гаррисон, Г.";
    public static final String AUTHOR_2 = "Перумов, Н.";
    public static final String AUTHOR_UNIQUE = "Уникальный автор";
    public static final String UPDATED_AUTHOR = "Обновлённый Автор";
    public static final int ZERO = 0;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private TestEntityManager em;

    private Author persistAuthor(String fullName) {
        Author author = new Author();
        author.setFullName(fullName);
        em.persist(author);
        em.flush();
        return author;
    }

    @DisplayName("is checking save method.")
    @Test
    void checkingSave() {
        Author author = new Author();
        author.setFullName(AUTHOR_1);

        authorRepository.save(author);

        assertThat(author.getId()).isNotNull();
        assertThat(author.getId()).isGreaterThan(ZERO);
        assertThat(author.getFullName()).isEqualTo(AUTHOR_1);
    }

    @DisplayName("is checking getById method.")
    @Test
    void checkingGetById() {
        Author expected = persistAuthor(AUTHOR_1);

        Optional<Author> actual = authorRepository.findById(expected.getId());

        assertThat(actual).isPresent();
        assertThat(actual.get().getFullName()).isEqualTo(AUTHOR_1);
    }

    @DisplayName("is checking getAll method.")
    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    void checkingGetAll() {
        persistAuthor(AUTHOR_1);
        persistAuthor(AUTHOR_2);

        List<Author> authors = authorRepository.findAll();

        assertThat(authors)
                .extracting(Author::getFullName)
                .contains(AUTHOR_1, AUTHOR_2);
    }

    @DisplayName("is checking getByFullName method.")
    @Test
    void checkingGetByFullName() {
        persistAuthor(AUTHOR_UNIQUE);

        Optional<Author> actual = authorRepository.getByFullName(AUTHOR_UNIQUE);

        assertThat(actual).isPresent();
        assertThat(actual.get().getFullName()).isEqualTo(AUTHOR_UNIQUE);
    }

    @DisplayName("is checking update method.")
    @Test
    void checkingUpdate() {
        Author author = persistAuthor(AUTHOR_1);

        author.setFullName(UPDATED_AUTHOR);
        authorRepository.save(author);

        Author actual = em.find(Author.class, author.getId());
        assertThat(actual.getFullName()).isEqualTo(UPDATED_AUTHOR);
    }

    @DisplayName("is checking deleteById method.")
    @Test
    void checkingDeleteById() {
        Author author = persistAuthor(AUTHOR_1);

        Long id = author.getId();
        authorRepository.deleteById(id);
        em.flush();

        assertThat(authorRepository.findById(id)).isEmpty();
    }
}