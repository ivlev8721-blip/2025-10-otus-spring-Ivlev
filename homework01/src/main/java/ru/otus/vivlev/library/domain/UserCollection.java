package ru.otus.vivlev.library.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_collection")
public class UserCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(targetEntity = DomainUser.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private DomainUser user;

    @ManyToOne(targetEntity = Album.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @Column(name = "added_date")
    private LocalDateTime addedDate;

    @Column(name = "is_wishlist")
    private Boolean isWishlist = false;
}
