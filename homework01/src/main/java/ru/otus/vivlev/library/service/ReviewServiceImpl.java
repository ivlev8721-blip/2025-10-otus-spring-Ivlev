package ru.otus.vivlev.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.vivlev.library.domain.Album;
import ru.otus.vivlev.library.domain.DomainUser;
import ru.otus.vivlev.library.domain.Review;
import ru.otus.vivlev.library.repository.AlbumRepository;
import ru.otus.vivlev.library.repository.ReviewRepository;
import ru.otus.vivlev.library.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;

    @Transactional(readOnly = true)
    public List<Review> getReviewsByAlbum(Long albumId) {
        return reviewRepository.findByAlbumId(albumId);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserUserId(userId);
    }

    @Transactional(readOnly = true)
    public long getTotalReviewsCount() {
        return reviewRepository.count();
    }

    @Transactional
    public Review createReview(Long userId, Long albumId, Integer rating, String comment) {
        DomainUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        Review review = new Review();
        review.setUser(user);
        review.setAlbum(album);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
