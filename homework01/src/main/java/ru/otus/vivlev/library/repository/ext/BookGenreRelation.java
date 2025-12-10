package ru.otus.vivlev.library.repository.ext;

import lombok.Data;

@Data
public class BookGenreRelation {

    private final long book_id;

    private final long genre_id;
}
