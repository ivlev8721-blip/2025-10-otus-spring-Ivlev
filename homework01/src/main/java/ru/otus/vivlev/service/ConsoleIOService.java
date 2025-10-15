package ru.otus.vivlev.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

@Service
public class ConsoleIOService implements IOService {
    private BufferedReader reader;

    @Override
    public void print(String s) {
        System.out.print(s);
    }

    @Override
    public void println(String s) {
        System.out.println(s);
    }

    @Override
    public String readLine() {
        // Ленивая инициализация BufferedReader после возможной подмены System.in в тестах
        try {
            if (reader == null) {
                reader = new BufferedReader(new InputStreamReader(System.in));
            }
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from input", e);
        }
    }
}
