package ru.otus.vivlev.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.vivlev.library.domain.Album;
import ru.otus.vivlev.library.repository.AlbumRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;

    @Transactional(readOnly = true)
    public Page<Album> getAllAlbums(Pageable pageable) {
        return albumRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Album> getAlbumById(Long id) {
        return albumRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Album> searchAlbums(String query, Pageable pageable) {
        return albumRepository.searchAlbums(query, pageable);
    }

    @Transactional(readOnly = true)
    public List<Album> getAlbumsByArtist(String artist) {
        return albumRepository.findByArtistContainingIgnoreCase(artist);
    }

    @Transactional(readOnly = true)
    public List<Album> getAlbumsByGenre(Long genreId) {
        return albumRepository.findByGenreId(genreId);
    }

    @Transactional
    public Album createAlbum(Album album) {
        return albumRepository.save(album);
    }

    @Transactional
    public Album updateAlbum(Long id, Album albumDetails) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found with id: " + id));
        
        album.setTitle(albumDetails.getTitle());
        album.setArtist(albumDetails.getArtist());
        album.setGenre(albumDetails.getGenre());
        album.setReleaseYear(albumDetails.getReleaseYear());
        album.setCoverImageUrl(albumDetails.getCoverImageUrl());
        
        return albumRepository.save(album);
    }

    @Transactional
    public void deleteAlbum(Long id) {
        albumRepository.deleteById(id);
    }
}
