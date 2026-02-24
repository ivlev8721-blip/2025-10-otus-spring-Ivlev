package ru.otus.vivlev.library.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.vivlev.library.dto.CommentDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent {
    
    private String eventId;
    private EventType eventType;
    private CommentDto comment;
    private LocalDateTime timestamp;
    
    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
    
    public CommentEvent(EventType eventType, CommentDto comment) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.eventType = eventType;
        this.comment = comment;
        this.timestamp = LocalDateTime.now();
    }
}
