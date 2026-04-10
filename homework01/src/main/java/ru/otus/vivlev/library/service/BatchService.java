package ru.otus.vivlev.library.service;

import org.springframework.web.multipart.MultipartFile;

public interface BatchService {

    byte[] exportAlbumsToCsv();

    int importAlbumsFromCsv(MultipartFile file) throws Exception;
}
