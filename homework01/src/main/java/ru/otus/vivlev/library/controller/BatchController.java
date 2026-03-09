package ru.otus.vivlev.library.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.otus.vivlev.library.domain.Album;
import ru.otus.vivlev.library.domain.Genre;
import ru.otus.vivlev.library.service.AlbumService;
import ru.otus.vivlev.library.service.GenreService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {

    private final AlbumService albumService;
    private final GenreService genreService;

    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportAlbumsToCsv() {
        List<Album> albums = albumService.getAllAlbums(PageRequest.of(0, 1000)).getContent();
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true, StandardCharsets.UTF_8);
        
        // CSV заголовок
        writer.println("ID,Title,Artist,Genre,Release Year,Cover Image URL");
        
        // CSV данные
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
        byte[] csvBytes = outputStream.toByteArray();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "albums.csv");
        headers.setContentLength(csvBytes.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }
    
    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> importAlbumsFromCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Файл пуст");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String headerLine = reader.readLine(); // Пропускаем заголовок
            if (headerLine == null) {
                return ResponseEntity.badRequest().body("Файл пуст");
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
                    return ResponseEntity.badRequest()
                            .body("Ошибка в строке " + lineNumber + ": " + e.getMessage());
                }
            }

            return ResponseEntity.ok("Импортировано альбомов: " + importedAlbums.size());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Ошибка чтения файла: " + e.getMessage());
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
