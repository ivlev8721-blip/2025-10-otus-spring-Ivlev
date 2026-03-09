package ru.otus.vivlev.library.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListenRequest {
    private Long albumId;
    private String username;
    private Long trackId;
}
