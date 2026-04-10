package ru.otus.vivlev.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.vivlev.library.domain.UserCollection;

import java.util.List;
import java.util.Optional;

public interface UserCollectionRepository extends JpaRepository<UserCollection, Long> {

    List<UserCollection> findByUserUserIdAndIsWishlist(Long userId, Boolean isWishlist);

    List<UserCollection> findByUserUserId(Long userId);

    Optional<UserCollection> findByUserUserIdAndAlbumId(Long userId, Long albumId);

    boolean existsByUserUserIdAndAlbumId(Long userId, Long albumId);
}
