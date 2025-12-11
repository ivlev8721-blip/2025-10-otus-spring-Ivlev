package ru.otus.vivlev.library.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.vivlev.library.repository.AuthorRepository;
import ru.otus.vivlev.library.domain.Author;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<Author> getById(Long id) {
        return authorRepository.getById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Author> getByName(String name) {
        return authorRepository.getByName(name);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Author> getAll() {
        return authorRepository.getAll();
    }

    @Transactional
    @Override
    public Author save(Author author) {
        return authorRepository.save(author);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        authorRepository.deleteById(id);
    }
}
