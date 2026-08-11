package com.uber.uber_backend.kafka;

import com.uber.uber_backend.dto.DriverLocationUpdate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DriverLocationConsumer {

    @KafkaListener(
            topics = "driver-location-updates",
            groupId = "driver-location-group"
    )
    public void consume(DriverLocationUpdate update) {

        System.out.println(update.getDriverId());
        System.out.println(update.getLatitude());
        System.out.println(update.getLongitude());

        // Save location
    }
}