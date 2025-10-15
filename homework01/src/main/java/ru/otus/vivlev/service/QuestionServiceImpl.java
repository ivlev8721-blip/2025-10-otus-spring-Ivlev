package ru.otus.vivlev.service;

import ru.otus.vivlev.dao.QuestionDao;
import ru.otus.vivlev.domain.Question;
import ru.otus.vivlev.domain.TestResult;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionServiceImpl implements QuestionService {
    private final QuestionDao questionDao;
    private final IOService ioService;
    private final TestProcess testProcess;
    private final int questionsCount;
    private final int passCount;
    private final boolean shuffleQuestions;

    public QuestionServiceImpl(QuestionDao questionDao,
                               IOService ioService,
                               TestProcess testProcess,
                               int questionsCount,
                               int passCount,
                               boolean shuffleQuestions) {
        this.questionDao = questionDao;
        this.ioService = ioService;
        this.testProcess = testProcess;
        this.questionsCount = questionsCount;
        this.passCount = passCount;
        this.shuffleQuestions = shuffleQuestions;
    }

    @Override
    public void printQuestions() {
        ioService.print("Enter your last name: ");
        String lastName = ioService.readLine();
        ioService.print("Enter your first name: ");
        String firstName = ioService.readLine();

        List<Question> questions = shuffleQuestions
                ? getShuffledQuestions()
                : questionDao.findAll().stream()
                .limit(questionsCount)
                .collect(Collectors.toList());

        TestResult result = testProcess.run(lastName, firstName, questions);
        ioService.println("\nResult for " + result.getLastName() + " " + result.getFirstName() + ":");
        ioService.println("Correct answers: " + result.getCorrectAnswers() + " out of " + result.getTotalQuestions());
        if (result.getCorrectAnswers() >= passCount) {
            ioService.println("Test passed! Congratulations!");
        } else {
            ioService.println("Test not passed. Please try again.");
        }
    }

    List<Question> getShuffledQuestions() {
        List<Question> all = questionDao.findAll();
        Collections.shuffle(all);
        return all.stream()
                .limit(questionsCount)
                .collect(Collectors.toList());
    }
}
