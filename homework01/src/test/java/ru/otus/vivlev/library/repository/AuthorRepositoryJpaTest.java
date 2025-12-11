package ru.otus.vivlev.library.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.otus.vivlev.library.domain.Author;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(AuthorRepositoryJpa.class)
@DisplayName("Тестирование AuthorRepositoryJpa")
@Sql(scripts = {"/schema.sql", "/data-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthorRepositoryJpaTest {

    public static final String NEW_AUTHOR = "Лукьяненко, С.";

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Должен находить автора по ID")
    void shouldFindAuthorById() {
        var authorOpt = authorRepository.getById(1L);
        assertThat(authorOpt)
                .isPresent()
                .hasValueSatisfying(a -> assertThat(a.getFullName()).isEqualTo("Гаррисон, Г."));
    }

    @Test
    @DisplayName("Должен возвращать всех авторов")
    void shouldReturnAllAuthors() {
        List<Author> authors = authorRepository.getAll();
        assertThat(authors).hasSize(2);
        assertThat(authors)
                .extracting(Author::getFullName)
                .containsExactlyInAnyOrder("Гаррисон, Г.", "Перумов, Н.");
    }

    @Test
    @DisplayName("Должен сохранять нового автора")
    void shouldSaveNewAuthor() {
        Author author = new Author();
        author.setFullName(NEW_AUTHOR);

        Author savedAuthor = authorRepository.save(author);
        em.flush(); // гарантируем, что изменения записаны
        em.clear(); // очищаем контекст для получения свежих данных

        assertThat(savedAuthor.getId()).isNotNull().isGreaterThan(0L);

        var saved = authorRepository.getById(savedAuthor.getId());
        assertThat(saved).isPresent();
        assertThat(saved.get().getFullName()).isEqualTo(NEW_AUTHOR);
    }

    @Test
    @DisplayName("Должен удалять автора по ID")
    void shouldDeleteAuthorById() {
        // Создаём нового автора
        Author author = new Author();
        author.setFullName("Автор без книг");
        Author savedAuthor = em.persistAndFlush(author);
        em.clear(); // очищаем контекст

        // Убеждаемся, что он сохранён
        assertThat(authorRepository.getById(savedAuthor.getId())).isPresent();

        // Удаляем
        authorRepository.deleteById(savedAuthor.getId());
        em.flush(); // принудительно синхронизируем состояние
        em.clear(); // очищаем контекст

        // Проверяем
        assertThat(authorRepository.getById(savedAuthor.getId())).isEmpty();
    }
}