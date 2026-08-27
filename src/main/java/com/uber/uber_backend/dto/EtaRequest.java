package com.uber.uber_backend.dto;

public class EtaRequest {

    private double distanceKm;
    private int hour;
    private int dayOfWeek;

    public EtaRequest() {}

    public EtaRequest(double distanceKm, int hour, int dayOfWeek) {
        this.distanceKm = distanceKm;
        this.hour = hour;
        this.dayOfWeek = dayOfWeek;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public int getHour() {
        return hour;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }
}