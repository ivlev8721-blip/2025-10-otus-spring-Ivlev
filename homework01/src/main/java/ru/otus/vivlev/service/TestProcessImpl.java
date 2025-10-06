package ru.otus.vivlev.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.vivlev.domain.AnswerOption;
import ru.otus.vivlev.domain.Question;
import ru.otus.vivlev.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestProcessImpl implements TestProcess {
    private final IOService ioService;

    /**
     * Проводит тестирование студента и возвращает результат
     */
    @Override
    public TestResult run(String lastName, String firstName, List<Question> questions) {
        int correctAnswers = 0;

        // Задаем все вопросы по очереди и считаем правильные ответы
        for (int i = 0; i < questions.size(); i++) {
            if (askAndCheckQuestion(questions.get(i), i)) {
                correctAnswers++;
            }
        }

        // Создаем результат теста: ФИО + количество правильных/всего вопросов
        return new TestResult(lastName, firstName, correctAnswers, questions.size());
    }

    private boolean askAndCheckQuestion(Question question, int idx) {
        // Показываем вопрос и варианты ответов
        printQuestion(question, idx);

        int answerIndex = -1;
        boolean validInput = false;

        // Просим ввести ответ пока не получим правильное число
        while (!validInput) {
            ioService.print("Your answer: ");
            String answer = ioService.readLine();
            try {
                answerIndex = Integer.parseInt(answer.trim()) - 1; // Преобразуем в индекс (начинается с 0)
                if (answerIndex < 0) {
                    ioService.println("Please enter a positive number!");
                } else {
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                ioService.println("Please enter a valid number!");
            }
        }

        // Проверяем правильность ответа:
        // - ответ должен быть в пределах списка вариантов
        // - выбранный вариант должен быть помечен как правильный
        return answerIndex < question.getAnswerOptions().size()
                && question.getAnswerOptions().get(answerIndex).isCorrect();
    }

    private void printQuestion(Question question, int idx) {
        // Выводим номер вопроса и его текст
        ioService.println((idx + 1) + ". " + question.getText());

        // Выводим все варианты ответов с номерами
        List<AnswerOption> opts = question.getAnswerOptions();
        for (int j = 0; j < opts.size(); j++) {
            ioService.println("  " + (j + 1) + ". " + opts.get(j).getText());
        }
    }
}