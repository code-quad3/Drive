package com.uber.uber_backend.service;

import com.uber.uber_backend.dto.EtaRequest;
import com.uber.uber_backend.dto.EtaResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EtaPredictionService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String ML_URL =
            "http://localhost:8000/predict-eta";

    public double predictEta(
            double distanceKm,
            int hour,
            int dayOfWeek
    ) {

        EtaRequest request =
                new EtaRequest(distanceKm, hour, dayOfWeek);

        EtaResponse response =
                restTemplate.postForObject(
                        ML_URL,
                        request,
                        EtaResponse.class
                );

        return response.getPredicted_eta_minutes();
    }
}