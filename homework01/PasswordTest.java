package ru.otus.vivlev.library;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Проверяем хеши из data.sql
        String adminHash = "$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC";
        String userHash = "$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K";
        
        System.out.println("admin password matches: " + encoder.matches("admin", adminHash));
        System.out.println("user password matches: " + encoder.matches("user", userHash));
        
        // Пробуем другие варианты
        System.out.println("admin/admin123: " + encoder.matches("admin123", adminHash));
        System.out.println("user/user123: " + encoder.matches("user123", userHash));
        System.out.println("admin/password: " + encoder.matches("password", adminHash));
        System.out.println("user/password: " + encoder.matches("password", userHash));
    }
}
