package ru.otus.vivlev.library.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.vivlev.library.domain.Book;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class BookRepositoryJpa implements BookRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Book> getById(Long id) {
        return Optional.ofNullable(em.find(Book.class, id));
    }

    @Override
    public List<Book> getAll() {
        return em.createQuery("select b from Book b join fetch b.author a", Book.class).getResultList();
    }

    @Override
    public Book save(Book book) {
        em.persist(book);
        return book;
    }

    @Override
    public Book update(Book book) {
        em.merge(book);
        return book;
    }

    @Override
    public void deleteById(Long id) {
        Query q = em.createQuery("delete from Book b where b.id = :id");
        q.setParameter("id", id);
        q.executeUpdate();
    }
}
