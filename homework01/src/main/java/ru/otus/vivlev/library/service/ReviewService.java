package ru.otus.vivlev.library.service;

import ru.otus.vivlev.library.domain.Review;

import java.util.List;

public interface ReviewService {

    List<Review> getReviewsByAlbum(Long albumId);

    List<Review> getReviewsByUser(Long userId);

    long getTotalReviewsCount();

    Review createReview(Long userId, Long albumId, Integer rating, String comment);

    void deleteReview(Long reviewId);
}
