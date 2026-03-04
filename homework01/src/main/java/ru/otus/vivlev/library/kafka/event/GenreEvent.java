package ru.otus.vivlev.library.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.vivlev.library.dto.GenreDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreEvent {
    
    private String eventId;
    private EventType eventType;
    private GenreDto genre;
    private LocalDateTime timestamp;
    
    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
    
    public GenreEvent(EventType eventType, GenreDto genre) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.eventType = eventType;
        this.genre = genre;
        this.timestamp = LocalDateTime.now();
    }
}
