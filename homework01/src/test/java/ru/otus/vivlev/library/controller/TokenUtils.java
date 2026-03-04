package ru.otus.vivlev.library.controller;

import lombok.SneakyThrows;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TokenUtils {

    @SneakyThrows
    public static String getToken(Map<String, String> tokenMap, MockMvc mockMvc, String userName, String userPass) {
        if (!tokenMap.containsKey(userName)) {
            MvcResult result = mockMvc.perform(post("/api/v1/authenticate")
                    .contentType("application/json")
                    .content("{\"login\":\"" + userName +
                            "\", \"password\":\"" + userPass +
                            "\"}"))
                    .andExpect(status().isOk())
                    .andReturn();
            tokenMap.put(userName, result.getResponse().getHeaderValue("Authorization").toString());
        }
        return tokenMap.get(userName);
    }
}
