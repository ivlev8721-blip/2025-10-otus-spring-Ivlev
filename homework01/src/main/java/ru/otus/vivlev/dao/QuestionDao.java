package ru.otus.vivlev.dao;

import ru.otus.vivlev.domain.Question;

import java.util.List;

public interface QuestionDao {
    List<Question> findAll();
}
