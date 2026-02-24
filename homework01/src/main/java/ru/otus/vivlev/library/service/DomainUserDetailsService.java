package ru.otus.vivlev.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.vivlev.library.domain.DomainUser;
import ru.otus.vivlev.library.exeption.UserNotActivatedException;
import ru.otus.vivlev.library.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("userDetailsService")
@RequiredArgsConstructor
public class DomainUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<DomainUser> userByLoginFromDatabase = userRepository.findOneWithAuthoritiesByLogin(login);
        return userByLoginFromDatabase.map(domainUser -> createSpringSecurityUser(login, domainUser))
                .orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found in the database"));
    }

    private User createSpringSecurityUser(String lowercaseLogin, DomainUser domainUser) {
        if (!domainUser.getIsActive()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }
        List<GrantedAuthority> grantedAuthorities = domainUser.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
        return new User(domainUser.getLogin(),
                domainUser.getPasswordHash(),
                grantedAuthorities);
    }
}
