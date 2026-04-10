package ru.otus.vivlev.library.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.otus.vivlev.library.domain.Album;

import java.util.List;
import java.util.Optional;

public interface AlbumService {

    Page<Album> getAllAlbums(Pageable pageable);

    Optional<Album> getAlbumById(Long id);

    Page<Album> searchAlbums(String query, Pageable pageable);

    List<Album> getAlbumsByArtist(String artist);

    List<Album> getAlbumsByGenre(Long genreId);

    Album createAlbum(Album album);

    Album updateAlbum(Long id, Album albumDetails);

    void deleteAlbum(Long id);
}
