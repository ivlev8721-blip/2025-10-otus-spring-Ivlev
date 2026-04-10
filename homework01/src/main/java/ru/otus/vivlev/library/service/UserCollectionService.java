package ru.otus.vivlev.library.service;

import ru.otus.vivlev.library.domain.UserCollection;

import java.util.List;

public interface UserCollectionService {

    List<UserCollection> getUserCollection(Long userId);

    List<UserCollection> getUserWishlist(Long userId);

    UserCollection addToCollection(Long userId, Long albumId, boolean isWishlist);

    void removeFromCollection(Long userId, Long albumId);

    UserCollection moveToCollection(Long userId, Long albumId);
}
