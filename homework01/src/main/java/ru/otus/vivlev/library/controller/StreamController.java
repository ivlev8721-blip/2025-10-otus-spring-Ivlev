package ru.otus.vivlev.library.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.otus.vivlev.library.integration.ListenRequest;

@RestController
@RequestMapping("/api/stream")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class StreamController {

    private final MessageChannel listenRequestChannel;

    @PostMapping("/listen")
    public ResponseEntity<String> requestStream(
            @RequestParam Long albumId,
            @RequestParam Long trackId,
            Authentication authentication) {
        
        ListenRequest request = new ListenRequest(albumId, authentication.getName(), trackId);
        
        listenRequestChannel.send(MessageBuilder.withPayload(request).build());
        
        return ResponseEntity.ok("Stream preparation started. You will be notified when ready.");
    }
}
