package com.uber.uber_backend.kafka;

import com.uber.uber_backend.dto.RideEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RideEventProducer {

    private final KafkaTemplate<String, RideEvent> kafkaTemplate;

    public RideEventProducer(KafkaTemplate<String, RideEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishRideEvent(RideEvent event) {

        kafkaTemplate.send(
                "ride-events",
                event.getRideId().toString(),
                event
        );
    }
}