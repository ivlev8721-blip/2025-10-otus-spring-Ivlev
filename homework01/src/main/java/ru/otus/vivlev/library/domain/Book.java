package ru.otus.vivlev.library.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    private long id;
    private String title;
    private Author author;
    private List<Genre> genres;

    public Book(long id, String title, Author author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genres = new ArrayList<>();
    }

    public Book(String title, Author author, List<Genre> genres) {
        this.title = title;
        this.author = author;
        this.genres = genres;
    }
}
