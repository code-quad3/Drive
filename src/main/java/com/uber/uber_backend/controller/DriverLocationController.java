package com.uber.uber_backend.controller;

import com.uber.uber_backend.dto.DriverLocationUpdate;
import com.uber.uber_backend.kafka.DriverLocationProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
public class DriverLocationController {

    private final DriverLocationProducer producer;

    public DriverLocationController(DriverLocationProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public String updateLocation(@RequestBody DriverLocationUpdate request) {

        producer.publishLocation(request);

        return "Location sent to Kafka";
    }
}