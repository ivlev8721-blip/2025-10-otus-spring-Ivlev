package ru.otus.vivlev.library.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.vivlev.library.dto.BookDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookEvent {
    
    private String eventId;
    private EventType eventType;
    private BookDto book;
    private LocalDateTime timestamp;
    
    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
    
    public BookEvent(EventType eventType, BookDto book) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.eventType = eventType;
        this.book = book;
        this.timestamp = LocalDateTime.now();
    }
}
