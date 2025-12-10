package ru.otus.vivlev.library.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Author {

    private long id;

    private String fullName;

    public Author(String fullName) {
        this.fullName = fullName;
    }
}
