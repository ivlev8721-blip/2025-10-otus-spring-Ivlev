package ru.otus.vivlev.library.batch.dto;

import lombok.Data;

@Data
public class AlbumCsvDto {
    private String title;
    private String artist;
    private String genreName;
    private Integer releaseYear;
    private String coverImageUrl;
}
