package ru.otus.vivlev.library.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.vivlev.library.config.jwt.TokenProvider;
import ru.otus.vivlev.library.controller.vm.JWTToken;
import ru.otus.vivlev.library.controller.vm.LoginVM;

import static ru.otus.vivlev.library.config.jwt.JWTFilter.AUTHORIZATION_HEADER;

@RestController
@RequiredArgsConstructor
public class UserJWTController {

    private final AuthenticationManager authenticationManager;

    private final TokenProvider tokenProvider;

    @PostMapping(value = "/api/v1/authenticate", produces = "application/json;charset=UTF-8")
    public ResponseEntity<JWTToken> authorize(@RequestBody LoginVM loginVM) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginVM.getLogin(), loginVM.getPassword());

        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }
}
