package com.uber.uber_backend.kafka;

import com.uber.uber_backend.dto.DriverLocationUpdate;
import com.uber.uber_backend.model.DriverLocation;
import com.uber.uber_backend.repository.DriverLocationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DriverLocationConsumer {

    private final DriverLocationRepository repository;

    public DriverLocationConsumer(DriverLocationRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "driver-location-updates",
            groupId = "driver-location-group"
    )
    public void consume(DriverLocationUpdate update) {

        DriverLocation location = repository
                .findByDriverId(update.getDriverId())
                .orElse(new DriverLocation());

        location.setDriverId(update.getDriverId());
        location.setLatitude(update.getLatitude());
        location.setLongitude(update.getLongitude());
        location.setUpdatedAt(LocalDateTime.now());

        repository.save(location);

        System.out.println("Location saved:");
        System.out.println("Driver: " + update.getDriverId());
        System.out.println("Latitude: " + update.getLatitude());
        System.out.println("Longitude: " + update.getLongitude());
    }
}