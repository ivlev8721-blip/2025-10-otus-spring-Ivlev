package ru.otus.vivlev.library.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.vivlev.library.domain.DomainUser;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<DomainUser, Long> {

    @EntityGraph(attributePaths = "authorities")
    Optional<DomainUser> findOneWithAuthoritiesByLogin(String login);
    
    Optional<DomainUser> findByLogin(String login);
}
