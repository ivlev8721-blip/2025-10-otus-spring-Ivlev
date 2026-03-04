package ru.otus.vivlev.library.kafka;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.vivlev.library.dto.AuthorDto;
import ru.otus.vivlev.library.kafka.event.AuthorEvent;
import ru.otus.vivlev.library.kafka.producer.LibraryEventProducer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9093", "port=9093"})
@DisplayName("Kafka Integration Test")
class KafkaIntegrationTest {

    @Autowired
    private LibraryEventProducer eventProducer;

    @Test
    @DisplayName("should send author event to Kafka")
    void shouldSendAuthorEvent() {
        AuthorDto authorDto = new AuthorDto(1L, "Тестовый автор");
        AuthorEvent event = new AuthorEvent(AuthorEvent.EventType.CREATED, authorDto);

        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getEventType()).isEqualTo(AuthorEvent.EventType.CREATED);
        assertThat(event.getAuthor().getFullName()).isEqualTo("Тестовый автор");
        assertThat(event.getTimestamp()).isNotNull();
    }
}
