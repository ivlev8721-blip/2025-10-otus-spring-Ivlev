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

    /**
     * Создает объект для работы с вопросами из CSV файла
     * Spring автоматически передаст настройки из application.properties
     */
    @Bean
    public QuestionDao questionDao(AppProperties appProperties) {
        return new QuestionDaoCsv(appProperties.getQuestionsFile());
    }

    /**
     * Включает поддержку подстановки значений из application.properties
     * в аннотации @Value (например: @Value("${questions.file}"))
     * static нужен чтобы этот бин создался самым первым
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}