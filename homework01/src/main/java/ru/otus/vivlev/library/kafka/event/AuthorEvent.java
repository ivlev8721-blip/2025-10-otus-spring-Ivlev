package ru.otus.vivlev.library.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.vivlev.library.dto.AuthorDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorEvent {
    
    private String eventId;
    private EventType eventType;
    private AuthorDto author;
    private LocalDateTime timestamp;
    
    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
    
    public AuthorEvent(EventType eventType, AuthorDto author) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.eventType = eventType;
        this.author = author;
        this.timestamp = LocalDateTime.now();
    }
}
