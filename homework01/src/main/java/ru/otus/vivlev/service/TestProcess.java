package ru.otus.vivlev.service;

import ru.otus.vivlev.domain.Question;
import ru.otus.vivlev.domain.TestResult;

import java.util.List;

public interface TestProcess {
    TestResult run(String lastName, String firstName, List<Question> questions);
}
