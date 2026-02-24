package ru.otus.vivlev.library.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.otus.vivlev.library.kafka.event.AuthorEvent;
import ru.otus.vivlev.library.kafka.event.BookEvent;
import ru.otus.vivlev.library.kafka.event.CommentEvent;
import ru.otus.vivlev.library.kafka.event.GenreEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.book-events}")
    private String bookEventsTopic;

    @Value("${kafka.topics.author-events}")
    private String authorEventsTopic;

    @Value("${kafka.topics.genre-events}")
    private String genreEventsTopic;

    @Value("${kafka.topics.comment-events}")
    private String commentEventsTopic;

    public void sendBookEvent(BookEvent event) {
        log.info("Отправка события книги: {} - {}", event.getEventType(), event.getBook().getTitle());
        sendEvent(bookEventsTopic, event.getEventId(), event);
    }

    public void sendAuthorEvent(AuthorEvent event) {
        log.info("Отправка события автора: {} - {}", event.getEventType(), event.getAuthor().getFullName());
        sendEvent(authorEventsTopic, event.getEventId(), event);
    }

    public void sendGenreEvent(GenreEvent event) {
        log.info("Отправка события жанра: {} - {}", event.getEventType(), event.getGenre().getGenreName());
        sendEvent(genreEventsTopic, event.getEventId(), event);
    }

    public void sendCommentEvent(CommentEvent event) {
        log.info("Отправка события комментария: {} - {}", event.getEventType(), event.getComment().getId());
        sendEvent(commentEventsTopic, event.getEventId(), event);
    }

    private void sendEvent(String topic, String key, Object event) {
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);
        
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("Событие успешно отправлено в топик: {} с ключом: {}", topic, key);
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Ошибка отправки события в топик: {} с ключом: {}", topic, key, ex);
            }
        });
    }
}
