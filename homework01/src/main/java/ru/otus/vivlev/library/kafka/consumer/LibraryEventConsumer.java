package ru.otus.vivlev.library.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.otus.vivlev.library.kafka.event.AuthorEvent;
import ru.otus.vivlev.library.kafka.event.BookEvent;
import ru.otus.vivlev.library.kafka.event.CommentEvent;
import ru.otus.vivlev.library.kafka.event.GenreEvent;

@Slf4j
@Service
public class LibraryEventConsumer {

    @KafkaListener(topics = "${kafka.topics.book-events}", groupId = "${kafka.consumer.group-id}")
    public void consumeBookEvent(BookEvent event) {
        log.info("Получено событие книги: {} - ID: {}, Название: {}", 
            event.getEventType(), 
            event.getBook().getId(), 
            event.getBook().getTitle());
        
        switch (event.getEventType()) {
            case CREATED:
                log.info("Книга создана: {}", event.getBook().getTitle());
                break;
            case UPDATED:
                log.info("Книга обновлена: {}", event.getBook().getTitle());
                break;
            case DELETED:
                log.info("Книга удалена: ID {}", event.getBook().getId());
                break;
        }
    }

    @KafkaListener(topics = "${kafka.topics.author-events}", groupId = "${kafka.consumer.group-id}")
    public void consumeAuthorEvent(AuthorEvent event) {
        log.info("Получено событие автора: {} - ID: {}, Имя: {}", 
            event.getEventType(), 
            event.getAuthor().getId(), 
            event.getAuthor().getFullName());
        
        switch (event.getEventType()) {
            case CREATED:
                log.info("Автор создан: {}", event.getAuthor().getFullName());
                break;
            case UPDATED:
                log.info("Автор обновлен: {}", event.getAuthor().getFullName());
                break;
            case DELETED:
                log.info("Автор удален: ID {}", event.getAuthor().getId());
                break;
        }
    }

    @KafkaListener(topics = "${kafka.topics.genre-events}", groupId = "${kafka.consumer.group-id}")
    public void consumeGenreEvent(GenreEvent event) {
        log.info("Получено событие жанра: {} - ID: {}, Название: {}", 
            event.getEventType(), 
            event.getGenre().getId(), 
            event.getGenre().getGenreName());
        
        switch (event.getEventType()) {
            case CREATED:
                log.info("Жанр создан: {}", event.getGenre().getGenreName());
                break;
            case UPDATED:
                log.info("Жанр обновлен: {}", event.getGenre().getGenreName());
                break;
            case DELETED:
                log.info("Жанр удален: ID {}", event.getGenre().getId());
                break;
        }
    }

    @KafkaListener(topics = "${kafka.topics.comment-events}", groupId = "${kafka.consumer.group-id}")
    public void consumeCommentEvent(CommentEvent event) {
        log.info("Получено событие комментария: {} - ID: {}", 
            event.getEventType(), 
            event.getComment().getId());
        
        switch (event.getEventType()) {
            case CREATED:
                log.info("Комментарий создан от пользователя: {}", event.getComment().getUserName());
                break;
            case UPDATED:
                log.info("Комментарий обновлен: ID {}", event.getComment().getId());
                break;
            case DELETED:
                log.info("Комментарий удален: ID {}", event.getComment().getId());
                break;
        }
    }
}
