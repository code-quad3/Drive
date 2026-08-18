package com.uber.uber_backend.controller;

import com.uber.uber_backend.dto.CreateRideRequest;
import com.uber.uber_backend.model.Ride;
import com.uber.uber_backend.service.RideService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    public Ride createRide(@RequestBody CreateRideRequest request) {

        return rideService.createRide(request);
    }

    @PostMapping("/{rideId}/assign")
    public Ride assignDriver(@PathVariable UUID rideId) {

        return rideService.assignDriver(rideId);
    }

    @PostMapping("/{rideId}/accept")
    public Ride acceptRide(
            @PathVariable UUID rideId,
            @RequestParam UUID driverId
    ) {

        return rideService.acceptRide(rideId, driverId);
    }

    @PostMapping("/{rideId}/start")
    public Ride startRide(
            @PathVariable UUID rideId,
            @RequestParam UUID driverId
    ) {

        return rideService.startRide(rideId, driverId);
    }

    @PostMapping("/{rideId}/complete")
    public Ride completeRide(
            @PathVariable UUID rideId,
            @RequestParam UUID driverId
    ) {

        return rideService.completeRide(rideId, driverId);
    }

    @PostMapping("/{rideId}/cancel")
    public Ride cancelRide(@PathVariable UUID rideId) {

        return rideService.cancelRide(rideId);
    }

}