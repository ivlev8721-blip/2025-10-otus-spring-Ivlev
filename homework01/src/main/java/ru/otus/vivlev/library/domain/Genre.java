package ru.otus.vivlev.library.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Genre {

    private long id;

    private String genreName;

    public Genre(String genreName) {
        this.genreName = genreName;
    }
}
