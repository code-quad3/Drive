package com.uber.uber_backend.dto;

public class FareCalculation {

    private final double distanceKm;
    private final double fare;

    public FareCalculation(double distanceKm, double fare) {
        this.distanceKm = distanceKm;
        this.fare = fare;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getFare() {
        return fare;
    }
}