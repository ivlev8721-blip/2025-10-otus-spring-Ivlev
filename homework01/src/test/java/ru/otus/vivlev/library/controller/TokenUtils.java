package ru.otus.vivlev.library.controller;

import lombok.SneakyThrows;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TokenUtils {

    @SneakyThrows
    public static String getToken(MockMvc mockMvc, String userName, String userPass) {
        // Получаем токен из заголовка Authorization
        MvcResult result = mockMvc.perform(post("/api/v1/authenticate")
                .contentType("application/json")
                .content("{\"login\":\"" + userName +
                        "\", \"password\":\"" + userPass +
                        "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        
        return result.getResponse().getHeader("Authorization");
    }
}
