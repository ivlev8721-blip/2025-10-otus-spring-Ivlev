package ru.otus.vivlev.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.otus.vivlev.library.domain.Album;
import ru.otus.vivlev.library.domain.Genre;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchServiceImpl implements BatchService {

    private final AlbumService albumService;
    private final GenreService genreService;

    @Transactional(readOnly = true)
    public byte[] exportAlbumsToCsv() {
        List<Album> albums = albumService.getAllAlbums(PageRequest.of(0, 1000)).getContent();
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true, StandardCharsets.UTF_8);
        
        writer.println("ID,Title,Artist,Genre,Release Year,Cover Image URL");
        
        for (Album album : albums) {
            String genreName = album.getGenre() != null ? album.getGenre().getName() : "";
            writer.printf("%d,\"%s\",\"%s\",\"%s\",%d,\"%s\"%n",
                    album.getId(),
                    escapeCsv(album.getTitle()),
                    escapeCsv(album.getArtist()),
                    escapeCsv(genreName),
                    album.getReleaseYear() != null ? album.getReleaseYear() : 0,
                    escapeCsv(album.getCoverImageUrl() != null ? album.getCoverImageUrl() : "")
            );
        }
        
        writer.flush();
        return outputStream.toByteArray();
    }

    @Transactional
    public int importAlbumsFromCsv(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл пуст");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("Файл пуст");
            }

            List<Album> importedAlbums = new ArrayList<>();
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    Album album = parseCsvLine(line);
                    Album savedAlbum = albumService.createAlbum(album);
                    importedAlbums.add(savedAlbum);
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка в строке " + lineNumber + ": " + e.getMessage(), e);
                }
            }

            return importedAlbums.size();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла: " + e.getMessage(), e);
        }
    }

    private Album parseCsvLine(String line) {
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        
        Album album = new Album();
        album.setTitle(unescapeCsv(parts[1]));
        album.setArtist(unescapeCsv(parts[2]));
        
        String genreName = unescapeCsv(parts[3]);
        if (!genreName.isEmpty()) {
            Genre genre = genreService.getAllGenres().stream()
                    .filter(g -> g.getName().equalsIgnoreCase(genreName))
                    .findFirst()
                    .orElse(null);
            album.setGenre(genre);
        }
        
        if (parts.length > 4 && !parts[4].trim().isEmpty()) {
            album.setReleaseYear(Integer.parseInt(parts[4].trim()));
        }
        
        if (parts.length > 5) {
            album.setCoverImageUrl(unescapeCsv(parts[5]));
        }
        
        return album;
    }
    
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"");
    }
    
    private String unescapeCsv(String value) {
        if (value == null) {
            return "";
        }
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value.replace("\"\"", "\"");
    }
}
