package ru.otus.vivlev.dao;

import ru.otus.vivlev.domain.AnswerOption;
import ru.otus.vivlev.domain.Question;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionDaoCsv implements QuestionDao {
    private final String resourceName;

    public QuestionDaoCsv(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public List<Question> findAll() {
        // Открываем CSV файл из папки resources
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            // Читаем файл построчно и преобразуем в список вопросов
            return reader.lines()
                    .map(line -> line.split(",")) // Разделяем строку на части по запятым
                    .filter(tokens -> tokens.length > 0) // Пропускаем пустые строки
                    .map(tokens -> {
                        // Первая часть - текст вопроса, остальные - варианты ответов
                        String questionText = tokens[0];
                        List<AnswerOption> options =
                                Arrays.stream(Arrays.copyOfRange(tokens, 1, tokens.length))
                                        .map(optionToken -> {
                                            // Вариант ответа в формате "текст:true" или "текст:false"
                                            String[] parts = optionToken.split(":");
                                            String text = parts[0];
                                            // Если есть двоеточие, берем признак правильности ответа
                                            boolean correct = parts.length > 1 && Boolean.parseBoolean(parts[1]);
                                            return new AnswerOption(text, correct);
                                        })
                                        .collect(Collectors.toList());
                        return new Question(questionText, options);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Если файл не найден или ошибка чтения - бросаем исключение
            throw new RuntimeException("Failed to read questions from resource: " + resourceName, e);
        }
    }
}