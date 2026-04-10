package ru.otus.vivlev.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.vivlev.library.domain.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByAlbumId(Long albumId);

    List<Review> findByUserUserId(Long userId);
}
