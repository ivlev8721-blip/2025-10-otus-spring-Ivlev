package ru.otus.vivlev.library.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.vivlev.library.service.AlbumService;
import ru.otus.vivlev.library.service.GenreService;
import ru.otus.vivlev.library.service.ReviewService;
import ru.otus.vivlev.library.service.UserCollectionService;
import ru.otus.vivlev.library.service.UserService;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final AlbumService albumService;
    private final GenreService genreService;
    private final ReviewService reviewService;
    private final UserCollectionService userCollectionService;
    private final UserService userService;

    @GetMapping("/")
    public String index(Model model, 
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size) {
        model.addAttribute("albums", albumService.getAllAlbums(PageRequest.of(page, size)));
        model.addAttribute("genres", genreService.getAllGenres());
        return "index";
    }

    @GetMapping("/catalog")
    public String catalog(Model model,
                         @RequestParam(required = false) Long genreId,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "20") int size) {
        if (genreId != null) {
            model.addAttribute("albums", new org.springframework.data.domain.PageImpl<>(albumService.getAlbumsByGenre(genreId)));
        } else {
            model.addAttribute("albums", albumService.getAllAlbums(PageRequest.of(page, size)));
        }
        model.addAttribute("genres", genreService.getAllGenres());
        model.addAttribute("selectedGenreId", genreId);
        return "catalog";
    }

    @GetMapping("/album/{id}")
    public String albumDetails(@PathVariable Long id, Model model) {
        albumService.getAlbumById(id).ifPresent(album -> {
            model.addAttribute("album", album);
            model.addAttribute("reviews", reviewService.getReviewsByAlbum(id));
        });
        return "album-details";
    }

    @GetMapping("/my-collection")
    public String myCollection(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Long userId = userService.findByLogin(authentication.getName()).getUserId();
            model.addAttribute("collection", userCollectionService.getUserCollection(userId));
            model.addAttribute("wishlist", userCollectionService.getUserWishlist(userId));
        }
        return "my-collection";
    }

    @GetMapping("/admin")
    public String adminPanel(Model model) {
        model.addAttribute("albums", albumService.getAllAlbums(PageRequest.of(0, 100)));
        model.addAttribute("genres", genreService.getAllGenres());
        model.addAttribute("reviewsCount", reviewService.getTotalReviewsCount());
        return "admin";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
