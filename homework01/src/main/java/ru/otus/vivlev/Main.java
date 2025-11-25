package ru.otus.vivlev;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.otus.vivlev.config.AppConfig;
import ru.otus.vivlev.service.QuestionService;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        QuestionService questionService = context.getBean(QuestionService.class);
        questionService.printQuestions();
    }
}
