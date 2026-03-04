package ru.otus.vivlev.library.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.otus.vivlev.library.config.jwt.JWTFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    private final JWTFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/h2-console/**")
                .antMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf()
                .disable()
                .httpBasic()
                .disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/authenticate").permitAll()
                .antMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/author/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/author/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/v1/author/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/v1/author/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/genre/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/genre/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/v1/genre/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/v1/genre/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/book/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/book/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/v1/book/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/v1/book/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/comment/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/comment/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/v1/comment/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/v1/comment/**").permitAll()
                .anyRequest().authenticated();

    }
}
