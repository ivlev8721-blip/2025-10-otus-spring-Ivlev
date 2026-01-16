package ru.otus.vivlev.library.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USERS")
public class DomainUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long userId;

    @Column(name = "LOGIN")
    private String login;

    @Column(name = "PASSWORD_HASH")
    private String passwordHash;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive;

    @JsonIgnore
    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(targetEntity = Authority.class, fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_AUTHORITY",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_NAME", referencedColumnName = "NAME")})
    private Set<Authority> authorities = new HashSet<>();
}
