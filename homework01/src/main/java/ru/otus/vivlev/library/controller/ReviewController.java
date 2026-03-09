package ru.otus.vivlev.library.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.otus.vivlev.library.domain.Review;
import ru.otus.vivlev.library.service.ReviewService;
import ru.otus.vivlev.library.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<Review>> getReviewsByAlbum(@PathVariable Long albumId) {
        return ResponseEntity.ok(reviewService.getReviewsByAlbum(albumId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Review>> getMyReviews(Authentication authentication) {
        Long userId = userService.findByLogin(authentication.getName()).getUserId();
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Review> createReview(
            @RequestBody ReviewRequest request,
            Authentication authentication) {
        Long userId = userService.findByLogin(authentication.getName()).getUserId();
        return ResponseEntity.ok(reviewService.createReview(
                userId, request.getAlbumId(), request.getRating(), request.getComment()));
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class ReviewRequest {
        private Long albumId;
        private Integer rating;
        private String comment;
    }
}
