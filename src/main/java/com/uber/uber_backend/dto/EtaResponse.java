package com.uber.uber_backend.dto;

public class EtaResponse {

    private double predicted_eta_minutes;

    public double getPredicted_eta_minutes() {
        return predicted_eta_minutes;
    }

    public void setPredicted_eta_minutes(double predicted_eta_minutes) {
        this.predicted_eta_minutes = predicted_eta_minutes;
    }
}