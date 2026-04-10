package ru.otus.vivlev.library.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import ru.otus.vivlev.library.batch.dto.AlbumCsvDto;
import ru.otus.vivlev.library.domain.Album;
import ru.otus.vivlev.library.domain.Genre;
import ru.otus.vivlev.library.repository.AlbumRepository;
import ru.otus.vivlev.library.repository.GenreRepository;

@Slf4j
@RequiredArgsConstructor
public class AlbumItemProcessor implements ItemProcessor<AlbumCsvDto, Album> {

    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;

    @Override
    public Album process(AlbumCsvDto dto) throws Exception {
        log.info("Processing album: {} by {}", dto.getTitle(), dto.getArtist());

        if (albumRepository.findByArtistContainingIgnoreCase(dto.getArtist()).stream()
                .anyMatch(a -> a.getTitle().equalsIgnoreCase(dto.getTitle()))) {
            log.warn("Album already exists: {} by {}", dto.getTitle(), dto.getArtist());
            return null;
        }

        Genre genre = genreRepository.findByName(dto.getGenreName())
                .orElseGet(() -> {
                    Genre newGenre = new Genre();
                    newGenre.setName(dto.getGenreName());
                    return genreRepository.save(newGenre);
                });

        Album album = new Album();
        album.setTitle(dto.getTitle());
        album.setArtist(dto.getArtist());
        album.setGenre(genre);
        album.setReleaseYear(dto.getReleaseYear());
        album.setCoverImageUrl(dto.getCoverImageUrl());

        log.info("Album processed successfully: {}", album.getTitle());
        return album;
    }
}
