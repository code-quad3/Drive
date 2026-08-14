package com.uber.uber_backend.dto;

import java.util.UUID;

public class NearbyDriverResponse {

    private UUID driverId;
    private String name;
    private double latitude;
    private double longitude;
    private double distance;

    public NearbyDriverResponse(
            UUID driverId,
            String name,
            double latitude,
            double longitude,
            double distance) {

        this.driverId = driverId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public UUID getDriverId() {
        return driverId;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistance() {
        return distance;
    }
}