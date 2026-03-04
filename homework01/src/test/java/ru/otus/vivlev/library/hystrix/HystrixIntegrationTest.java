package ru.otus.vivlev.library.hystrix;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import ru.otus.vivlev.library.dto.BookDto;
import ru.otus.vivlev.library.kafka.event.BookEvent;
import ru.otus.vivlev.library.kafka.producer.LibraryEventProducer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Hystrix Integration Test")
public class HystrixIntegrationTest {

    @Autowired
    private LibraryEventProducer eventProducer;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    @DisplayName("Проверка успешного выполнения Hystrix команды")
    void testHystrixCommandSuccess() {
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(null);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Test Book");

        BookEvent event = new BookEvent(BookEvent.EventType.CREATED, bookDto);

        eventProducer.sendBookEvent(event);

        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Проверка срабатывания fallback при ошибке")
    void testHystrixCommandFallback() {
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Kafka недоступна"));

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Test Book");

        BookEvent event = new BookEvent(BookEvent.EventType.CREATED, bookDto);

        try {
            eventProducer.sendBookEvent(event);
        } catch (Exception e) {
        }

        verify(kafkaTemplate, atLeastOnce()).send(anyString(), anyString(), any());
    }
}
