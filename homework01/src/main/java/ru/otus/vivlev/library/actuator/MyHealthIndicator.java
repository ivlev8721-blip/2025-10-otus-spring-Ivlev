package ru.otus.vivlev.library.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import ru.otus.vivlev.library.repository.AlbumRepository;

@Component
@RequiredArgsConstructor
public class MyHealthIndicator implements HealthIndicator {

    private final AlbumRepository albumRepository;

    @Override
    public Health health() {
        long count = albumRepository.count();
        if (count == 0) {
            return Health.down()
                    .status(Status.DOWN)
                    .withDetail("message", "Внимание, нет доступных альбомов!")
                    .build();
        } else {
            return Health.up()
                    .withDetail("message", "Все отлично, альбомы присутствуют!")
                    .withDetail("albumCount", count)
                    .build();
        }
    }
}
