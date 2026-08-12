package com.uber.uber_backend.kafka;

import com.uber.uber_backend.dto.RideEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RideEventConsumer {

    @KafkaListener(
            topics = "ride-events",
            groupId = "ride-events-group"
    )
    public void consume(RideEvent event) {

        System.out.println("Ride Event Received");

        System.out.println("Ride ID: " + event.getRideId());
        System.out.println("Rider ID: " + event.getRiderId());
        System.out.println("Driver ID: " + event.getDriverId());
        System.out.println("Event: " + event.getEvent());
    }
}