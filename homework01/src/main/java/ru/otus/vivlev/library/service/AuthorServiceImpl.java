package ru.otus.vivlev.library.service;

import org.springframework.stereotype.Service;
import ru.otus.vivlev.library.repository.AuthorJdbc;
import ru.otus.vivlev.library.domain.Author;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorJdbc authorJdbc;

    public AuthorServiceImpl(AuthorJdbc authorJdbc) {
        this.authorJdbc = authorJdbc;
    }

    @Override
    public Optional<Author> getById(long id) {
        return authorJdbc.getById(id);
    }

    @Override
    public Optional<Author> getByName(String name) {
        return authorJdbc.getByName(name);
    }

    @Override
    public List<Author> getAll() {
        return authorJdbc.getAll();
    }

    @Override
    public Author save(Author author) {
        return authorJdbc.save(author);
    }
}
