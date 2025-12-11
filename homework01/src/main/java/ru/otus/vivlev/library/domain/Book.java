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
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToOne(targetEntity = Author.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private Author author;

    @EqualsAndHashCode.Exclude
    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(targetEntity = Genre.class, fetch = FetchType.EAGER)
    @JoinTable(
            name = "book_genre",
            joinColumns = {@JoinColumn(name = "book_id")},
            inverseJoinColumns = {@JoinColumn(name = "genre_id")}
    )
    private List<Genre> genres = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(targetEntity = Comment.class, mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}
