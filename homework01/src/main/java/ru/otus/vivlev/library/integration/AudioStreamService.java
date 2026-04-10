package ru.otus.vivlev.library.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.otus.vivlev.library.domain.Track;
import ru.otus.vivlev.library.repository.TrackRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AudioStreamService {

    private final TrackRepository trackRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @ServiceActivator(inputChannel = "listenRequestChannel", outputChannel = "processingChannel")
    public ListenRequest transformRequest(ListenRequest request) {
        log.info("Received listen request for album: {}, track: {}, user: {}", 
                request.getAlbumId(), request.getTrackId(), request.getUsername());
        return request;
    }

    @ServiceActivator(inputChannel = "processingChannel", outputChannel = "notificationChannel")
    public StreamReadyNotification prepareStream(ListenRequest request) {
        log.info("Preparing audio stream for track: {}", request.getTrackId());
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Stream preparation interrupted", e);
        }

        Track track = trackRepository.findById(request.getTrackId())
                .orElseThrow(() -> new RuntimeException("Track not found"));

        String streamUrl = "/stream/" + request.getTrackId() + "?token=" + System.currentTimeMillis();
        
        log.info("Stream ready for track: {} at URL: {}", request.getTrackId(), streamUrl);
        
        return new StreamReadyNotification(
                request.getAlbumId(),
                request.getTrackId(),
                request.getUsername(),
                streamUrl,
                "Track '" + track.getTitle() + "' is ready to play!"
        );
    }

    @ServiceActivator(inputChannel = "notificationChannel")
    public void sendNotification(StreamReadyNotification notification) {
        log.info("Sending WebSocket notification to user: {}", notification.getUsername());
        
        messagingTemplate.convertAndSendToUser(
                notification.getUsername(),
                "/topic/stream-ready",
                notification
        );
    }

    @ServiceActivator(inputChannel = "notificationChannel")
    public void logStreamEvent(StreamReadyNotification notification) {
        log.info("Stream event logged - Album: {}, Track: {}, User: {}, URL: {}", 
                notification.getAlbumId(), 
                notification.getTrackId(), 
                notification.getUsername(), 
                notification.getStreamUrl());
    }
}
