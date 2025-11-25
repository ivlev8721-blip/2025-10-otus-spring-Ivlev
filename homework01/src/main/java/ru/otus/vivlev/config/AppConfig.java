package ru.otus.vivlev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import ru.otus.vivlev.dao.QuestionDao;
import ru.otus.vivlev.dao.QuestionDaoCsv;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan("ru.otus.vivlev")
public class AppConfig {
    @Bean
    public QuestionDao questionDao(AppProperties appProperties) {
        return new QuestionDaoCsv(appProperties.getQuestionsFile());
    }
    // Этот бин необходим для поддержки аннотаций @Value
    // Позволяет Spring подставлять значения из application.properties
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}