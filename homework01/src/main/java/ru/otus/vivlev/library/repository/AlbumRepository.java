package ru.otus.vivlev.library.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.vivlev.library.domain.Album;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findByArtistContainingIgnoreCase(String artist);

    List<Album> findByGenreId(Long genreId);

    @Query("SELECT a FROM Album a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(a.artist) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Album> searchAlbums(@Param("query") String query, Pageable pageable);

    Page<Album> findAll(Pageable pageable);
}
