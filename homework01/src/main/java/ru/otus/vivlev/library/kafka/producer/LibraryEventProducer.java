package ru.otus.vivlev.library.kafka.producer;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
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

    @HystrixCommand(
        fallbackMethod = "sendBookEventFallback",
        commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000")
        }
    )
    public void sendBookEvent(BookEvent event) {
        log.info("Отправка события книги: {} - {}", event.getEventType(), event.getBook().getTitle());
        sendEvent(bookEventsTopic, event.getEventId(), event);
    }

    @HystrixCommand(
        fallbackMethod = "sendAuthorEventFallback",
        commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000")
        }
    )
    public void sendAuthorEvent(AuthorEvent event) {
        log.info("Отправка события автора: {} - {}", event.getEventType(), event.getAuthor().getFullName());
        sendEvent(authorEventsTopic, event.getEventId(), event);
    }

    @HystrixCommand(
        fallbackMethod = "sendGenreEventFallback",
        commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000")
        }
    )
    public void sendGenreEvent(GenreEvent event) {
        log.info("Отправка события жанра: {} - {}", event.getEventType(), event.getGenre().getGenreName());
        sendEvent(genreEventsTopic, event.getEventId(), event);
    }

    @HystrixCommand(
        fallbackMethod = "sendCommentEventFallback",
        commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000")
        }
    )
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

    public void sendBookEventFallback(BookEvent event, Throwable throwable) {
        log.error("Circuit breaker активирован для события книги: {} - {}. Причина: {}", 
            event.getEventType(), event.getBook().getTitle(), throwable.getMessage());
    }

    public void sendAuthorEventFallback(AuthorEvent event, Throwable throwable) {
        log.error("Circuit breaker активирован для события автора: {} - {}. Причина: {}", 
            event.getEventType(), event.getAuthor().getFullName(), throwable.getMessage());
    }

    public void sendGenreEventFallback(GenreEvent event, Throwable throwable) {
        log.error("Circuit breaker активирован для события жанра: {} - {}. Причина: {}", 
            event.getEventType(), event.getGenre().getGenreName(), throwable.getMessage());
    }

    public void sendCommentEventFallback(CommentEvent event, Throwable throwable) {
        log.error("Circuit breaker активирован для события комментария: {} - {}. Причина: {}", 
            event.getEventType(), event.getComment().getId(), throwable.getMessage());
    }
}
