package com.uber.uber_backend.controller;

import com.uber.uber_backend.dto.DriverLocationUpdate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
public class DriverLocationController {

    @PostMapping
    public String updateLocation(
            @RequestBody DriverLocationUpdate request
    ) {

        System.out.println(
                "Driver " + request.getDriverId()
                        + " lat=" + request.getLatitude()
                        + " lng=" + request.getLongitude()
        );

        return "Location received";
    }
}