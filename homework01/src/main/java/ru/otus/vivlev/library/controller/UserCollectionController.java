package ru.otus.vivlev.library.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.otus.vivlev.library.domain.UserCollection;
import ru.otus.vivlev.library.service.UserCollectionService;
import ru.otus.vivlev.library.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/collection")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserCollectionController {

    private final UserCollectionService userCollectionService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserCollection>> getMyCollection(Authentication authentication) {
        Long userId = userService.findByLogin(authentication.getName()).getUserId();
        return ResponseEntity.ok(userCollectionService.getUserCollection(userId));
    }

    @GetMapping("/wishlist")
    public ResponseEntity<List<UserCollection>> getMyWishlist(Authentication authentication) {
        Long userId = userService.findByLogin(authentication.getName()).getUserId();
        return ResponseEntity.ok(userCollectionService.getUserWishlist(userId));
    }

    @PostMapping("/add/{albumId}")
    public ResponseEntity<UserCollection> addToCollection(
            @PathVariable Long albumId,
            @RequestParam(defaultValue = "false") boolean wishlist,
            Authentication authentication) {
        Long userId = userService.findByLogin(authentication.getName()).getUserId();
        return ResponseEntity.ok(userCollectionService.addToCollection(userId, albumId, wishlist));
    }

    @DeleteMapping("/remove/{albumId}")
    public ResponseEntity<Void> removeFromCollection(
            @PathVariable Long albumId,
            Authentication authentication) {
        Long userId = userService.findByLogin(authentication.getName()).getUserId();
        userCollectionService.removeFromCollection(userId, albumId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/move/{albumId}")
    public ResponseEntity<UserCollection> moveToCollection(
            @PathVariable Long albumId,
            Authentication authentication) {
        Long userId = userService.findByLogin(authentication.getName()).getUserId();
        return ResponseEntity.ok(userCollectionService.moveToCollection(userId, albumId));
    }
}
