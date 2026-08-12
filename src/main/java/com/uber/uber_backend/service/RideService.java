package com.uber.uber_backend.service;

import com.uber.uber_backend.dto.CreateRideRequest;
import com.uber.uber_backend.dto.RideEvent;
import com.uber.uber_backend.kafka.RideEventProducer;
import com.uber.uber_backend.model.Ride;
import com.uber.uber_backend.model.RideStatus;
import com.uber.uber_backend.repository.RideRepository;
import org.springframework.stereotype.Service;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final RideEventProducer rideEventProducer;

    public RideService(
            RideRepository rideRepository,
            RideEventProducer rideEventProducer
    ) {
        this.rideRepository = rideRepository;
        this.rideEventProducer = rideEventProducer;
    }

    public Ride createRide(CreateRideRequest request) {

        Ride ride = new Ride();

        ride.setRiderId(request.getRiderId());

        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());

        ride.setDestinationLatitude(request.getDestinationLatitude());
        ride.setDestinationLongitude(request.getDestinationLongitude());

        ride.setStatus(RideStatus.REQUESTED);

        // Save to PostgreSQL
        Ride savedRide = rideRepository.save(ride);

        // Create Kafka event
        RideEvent event = new RideEvent();

        event.setRideId(savedRide.getId());
        event.setRiderId(savedRide.getRiderId());
        event.setDriverId(savedRide.getDriverId());
        event.setEvent("RIDE_CREATED");

        // Publish to Kafka
        rideEventProducer.publishRideEvent(event);

        return savedRide;
    }
}