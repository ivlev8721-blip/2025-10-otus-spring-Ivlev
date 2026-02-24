package ru.otus.vivlev.library.controller;

import org.springframework.test.web.servlet.ResultMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataForAllControllerTest {
    private static final Map<String, Map<String, ResultMatcher>> urlsForUser = new HashMap<>();
    private static final Map<String, Map<String, ResultMatcher>> urlsForAdmin = new HashMap<>();
    private static final Map<String, Map<String, ResultMatcher>> urlsNoAuth = new HashMap<>();

    static {
        urlsForUser.put("/api/v1/book", Map.of("get", status().isOk(), "post", status().isOk()));
        urlsForAdmin.put("/api/v1/book", Map.of("get", status().isOk(), "post", status().isOk()));
        urlsNoAuth.put("/api/v1/book", Map.of("get", status().isForbidden(), "post", status().isForbidden()));
        urlsForUser.put("/api/v1/book/2", Map.of("get", status().isOk(), "put", status().isOk()));
        urlsForAdmin.put("/api/v1/book/2", Map.of("get", status().isOk(), "put", status().isOk()));
        urlsForUser.put("/api/v1/book/3", Map.of("delete", status().isNoContent()));
        urlsForAdmin.put("/api/v1/book/3", Map.of("delete", status().isNoContent()));
        urlsNoAuth.put("/api/v1/book/2", Map.of("get", status().isForbidden(), "put", status().isForbidden(), "delete", status().isForbidden()));

        urlsForUser.put("/api/v1/author", Map.of("get", status().isOk(), "post", status().isForbidden()));
        urlsForAdmin.put("/api/v1/author", Map.of("get", status().isOk(), "post", status().isOk()));
        urlsNoAuth.put("/api/v1/author", Map.of("get", status().isForbidden(), "post", status().isForbidden()));
        urlsForUser.put("/api/v1/author/3", Map.of("delete", status().isForbidden()));
        urlsForAdmin.put("/api/v1/author/3", Map.of("delete", status().isNoContent()));
        urlsForUser.put("/api/v1/author/1", Map.of("get", status().isOk()));
        urlsForAdmin.put("/api/v1/author/1", Map.of("get", status().isOk()));
        urlsNoAuth.put("/api/v1/author/2", Map.of("get", status().isForbidden(), "delete", status().isForbidden()));

        urlsForUser.put("/api/v1/genre", Map.of("get", status().isOk(), "post", status().isForbidden()));
        urlsForAdmin.put("/api/v1/genre", Map.of("get", status().isOk(), "post", status().isOk()));
        urlsNoAuth.put("/api/v1/genre", Map.of("get", status().isForbidden(), "post", status().isForbidden()));
        urlsForUser.put("/api/v1/genre/3", Map.of("delete", status().isForbidden()));
        urlsForAdmin.put("/api/v1/genre/3", Map.of("delete", status().isNoContent()));
        urlsForUser.put("/api/v1/genre/1", Map.of("get", status().isOk()));
        urlsForAdmin.put("/api/v1/genre/1", Map.of("get", status().isOk()));
        urlsNoAuth.put("/api/v1/genre/2", Map.of("get", status().isForbidden(), "delete", status().isForbidden()));

        urlsForUser.put("/api/v1/comment", Map.of("post", status().isOk()));
        urlsForAdmin.put("/api/v1/comment", Map.of("post", status().isOk()));
        urlsNoAuth.put("/api/v1/comment", Map.of("post", status().isForbidden()));
        urlsForUser.put("/api/v1/comment/book/1", Map.of("get", status().isOk()));
        urlsForAdmin.put("/api/v1/comment/book/1", Map.of("get", status().isOk()));
        urlsNoAuth.put("/api/v1/comment/book/1", Map.of("get", status().isForbidden()));
        urlsForUser.put("/api/v1/comment/1", Map.of("delete", status().isNoContent()));
        urlsForAdmin.put("/api/v1/comment/1", Map.of("delete", status().isNoContent()));
        urlsForUser.put("/api/v1/comment/1", Map.of("get", status().isOk()));
        urlsForAdmin.put("/api/v1/comment/1", Map.of("get", status().isOk()));
        urlsNoAuth.put("/api/v1/comment/2", Map.of("get", status().isForbidden(), "delete", status().isForbidden()));
    }

    public static Stream<Map<String, Map<String, ResultMatcher>>> getUrlsForUser() {
        return Stream.of(urlsForUser);
    }

    public static Stream<Map<String, Map<String, ResultMatcher>>> getUrlsForAdmin() {
        return Stream.of(urlsForAdmin);
    }

    public static Stream<Map<String, Map<String, ResultMatcher>>> getUrlsNoAuth() {
        return Stream.of(urlsNoAuth);
    }
}
