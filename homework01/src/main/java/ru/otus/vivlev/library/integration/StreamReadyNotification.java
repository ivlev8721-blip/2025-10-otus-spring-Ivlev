package ru.otus.vivlev.library.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamReadyNotification {
    private Long albumId;
    private Long trackId;
    private String username;
    private String streamUrl;
    private String message;
}
