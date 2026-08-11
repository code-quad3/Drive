package com.uber.uber_backend.kafka;

import com.uber.uber_backend.dto.DriverLocationUpdate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DriverLocationProducer {

    private final KafkaTemplate<String, DriverLocationUpdate> kafkaTemplate;

    public DriverLocationProducer(KafkaTemplate<String, DriverLocationUpdate> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishLocation(DriverLocationUpdate location) {
        kafkaTemplate.send(
                "driver-location-updates",
                location.getDriverId().toString(),
                location
        );
    }
}