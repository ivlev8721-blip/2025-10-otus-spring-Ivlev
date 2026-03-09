package ru.otus.vivlev.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.vivlev.library.domain.Album;
import ru.otus.vivlev.library.domain.DomainUser;
import ru.otus.vivlev.library.domain.UserCollection;
import ru.otus.vivlev.library.repository.AlbumRepository;
import ru.otus.vivlev.library.repository.UserCollectionRepository;
import ru.otus.vivlev.library.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCollectionService {

    private final UserCollectionRepository userCollectionRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;

    @Transactional(readOnly = true)
    public List<UserCollection> getUserCollection(Long userId) {
        return userCollectionRepository.findByUserUserIdAndIsWishlist(userId, false);
    }

    @Transactional(readOnly = true)
    public List<UserCollection> getUserWishlist(Long userId) {
        return userCollectionRepository.findByUserUserIdAndIsWishlist(userId, true);
    }

    @Transactional
    public UserCollection addToCollection(Long userId, Long albumId, boolean isWishlist) {
        if (userCollectionRepository.existsByUserUserIdAndAlbumId(userId, albumId)) {
            throw new RuntimeException("Album already in collection");
        }

        DomainUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        UserCollection collection = new UserCollection();
        collection.setUser(user);
        collection.setAlbum(album);
        collection.setAddedDate(LocalDateTime.now());
        collection.setIsWishlist(isWishlist);

        return userCollectionRepository.save(collection);
    }

    @Transactional
    public void removeFromCollection(Long userId, Long albumId) {
        UserCollection collection = userCollectionRepository.findByUserUserIdAndAlbumId(userId, albumId)
                .orElseThrow(() -> new RuntimeException("Album not in collection"));
        userCollectionRepository.delete(collection);
    }

    @Transactional
    public UserCollection moveToCollection(Long userId, Long albumId) {
        UserCollection collection = userCollectionRepository.findByUserUserIdAndAlbumId(userId, albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));
        collection.setIsWishlist(false);
        return userCollectionRepository.save(collection);
    }
}
