package ru.otus.vivlev.library.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.otus.vivlev.library.domain.Album;
import ru.otus.vivlev.library.service.AlbumService;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping
    public ResponseEntity<Page<Album>> getAllAlbums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(albumService.getAllAlbums(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbumById(@PathVariable Long id) {
        return albumService.getAlbumById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Album>> searchAlbums(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(albumService.searchAlbums(query, pageable));
    }

    @GetMapping("/artist/{artist}")
    public ResponseEntity<List<Album>> getAlbumsByArtist(@PathVariable String artist) {
        return ResponseEntity.ok(albumService.getAlbumsByArtist(artist));
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<List<Album>> getAlbumsByGenre(@PathVariable Long genreId) {
        return ResponseEntity.ok(albumService.getAlbumsByGenre(genreId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Album> createAlbum(@RequestBody Album album) {
        return ResponseEntity.ok(albumService.createAlbum(album));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Album> updateAlbum(@PathVariable Long id, @RequestBody Album album) {
        return ResponseEntity.ok(albumService.updateAlbum(id, album));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }
}
