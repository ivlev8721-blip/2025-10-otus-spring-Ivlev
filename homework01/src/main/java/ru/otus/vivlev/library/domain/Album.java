package ru.otus.vivlev.library.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "album")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "artist", nullable = false)
    private String artist;

    @ManyToOne(targetEntity = Genre.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(targetEntity = Track.class, mappedBy = "album", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Track> tracks = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(targetEntity = Review.class, mappedBy = "album", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
}
