package com.uber.uber_backend.controller;

import com.uber.uber_backend.dto.DriverLocationUpdate;
import com.uber.uber_backend.kafka.DriverLocationProducer;
import com.uber.uber_backend.model.DriverLocation;
import com.uber.uber_backend.repository.DriverLocationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
public class DriverLocationController {

    private final DriverLocationProducer producer;
    private final DriverLocationRepository repository;

    public DriverLocationController(
            DriverLocationProducer producer,
            DriverLocationRepository repository
    ) {
        this.producer = producer;
        this.repository = repository;
    }

    @PostMapping
    public String updateLocation(@RequestBody DriverLocationUpdate request) {

        producer.publishLocation(request);

        return "Location sent to Kafka";
    }

    @GetMapping("/{driverId}")
    public DriverLocation getDriverLocation(
            @PathVariable UUID driverId
    ) {
        return repository
                .findByDriverId(driverId)
                .orElseThrow(
                        () -> new RuntimeException("Driver location not found")
                );
    }
}