package ru.otus.vivlev.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml"})
public class QuestionServiceIntegrationTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream testOut = new PrintStream(outContent);
    private final java.io.InputStream originalIn = System.in;

    @Autowired
    private QuestionService questionService;

    @Before
    public void setUpStreams() {
        System.setOut(testOut);
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    public void integrationTest_shouldPassTestFlow() {
        // Подготовка ввода пользователя (фамилия, имя, ответы для всех 5 вопросов)
        // Вопросы и правильные варианты из resources/questions.csv: правильный ответ всегда первый (индекс 1)
        String userInput = "Ivanov\nIvan\n1\n1\n1\n1\n1\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);

        // Запуск теста через XML-контекстный бин
        questionService.printQuestions();

        String output = outContent.toString();
        // Проверяем, что первый вопрос и вариант ответа присутствуют
        assertTrue(output.contains("1. What does Spring manage?"));
        assertTrue(output.contains("  1. Beans"));
        // Проверяем, что итог пройден
        assertTrue(output.contains("Test passed"));
    }
}
