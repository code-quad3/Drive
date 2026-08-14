package com.uber.uber_backend.controller;

import com.uber.uber_backend.dto.NearbyDriverResponse;
import com.uber.uber_backend.service.NearbyDriverService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class NearbyDriverController {

    private final NearbyDriverService nearbyDriverService;

    public NearbyDriverController(NearbyDriverService nearbyDriverService) {
        this.nearbyDriverService = nearbyDriverService;
    }

    @GetMapping("/nearby")
    public List<NearbyDriverResponse> findNearbyDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius) {

        return nearbyDriverService.findNearbyDrivers(
                latitude,
                longitude,
                radius
        );
    }
}