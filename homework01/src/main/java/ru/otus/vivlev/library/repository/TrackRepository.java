package ru.otus.vivlev.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.vivlev.library.domain.Track;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {

    List<Track> findByAlbumId(Long albumId);
}
