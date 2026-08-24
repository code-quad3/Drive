package com.uber.uber_backend.service;

import com.uber.uber_backend.dto.FareCalculation;
import org.springframework.stereotype.Service;

@Service
public class FareService {

    private final NearbyDriverService nearbyDriverService;

    public FareService(NearbyDriverService nearbyDriverService) {
        this.nearbyDriverService = nearbyDriverService;
    }

    public FareCalculation calculateFare(
            double pickupLatitude,
            double pickupLongitude,
            double destinationLatitude,
            double destinationLongitude) {

        // 1. Calculate distance
        double distanceKm = nearbyDriverService.calculateDistance(
                pickupLatitude,
                pickupLongitude,
                destinationLatitude,
                destinationLongitude
        );

        // 2. Calculate fare
        double baseFare = 50.0;
        double perKmRate = 15.0;

        double fare = baseFare + (distanceKm * perKmRate);

        // 3. Return both values
        return new FareCalculation(distanceKm, fare);
    }
}