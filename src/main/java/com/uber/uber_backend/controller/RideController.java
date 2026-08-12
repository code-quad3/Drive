package com.uber.uber_backend.controller;

import com.uber.uber_backend.dto.CreateRideRequest;
import com.uber.uber_backend.model.Ride;
import com.uber.uber_backend.service.RideService;
import org.springframework.web.bind.annotation.*;

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
}