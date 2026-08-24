package com.uber.uber_backend.service;

import com.uber.uber_backend.dto.NearbyDriverResponse;
import com.uber.uber_backend.model.Driver;
import com.uber.uber_backend.model.DriverLocation;
import com.uber.uber_backend.model.DriverStatus;
import com.uber.uber_backend.repository.DriverLocationRepository;
import com.uber.uber_backend.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NearbyDriverService {

    private final DriverRepository driverRepository;
    private final DriverLocationRepository driverLocationRepository;

    public NearbyDriverService(
            DriverRepository driverRepository,
            DriverLocationRepository driverLocationRepository) {

        this.driverRepository = driverRepository;
        this.driverLocationRepository = driverLocationRepository;
    }

    public List<NearbyDriverResponse> findNearbyDrivers(
            double latitude,
            double longitude,
            double radius) {

        List<Driver> availableDrivers =
                driverRepository.findByStatus(DriverStatus.AVAILABLE);

        List<NearbyDriverResponse> nearbyDrivers = new ArrayList<>();

        for (Driver driver : availableDrivers) {

            DriverLocation location =
                    driverLocationRepository
                            .findByDriverId(driver.getId())
                            .orElse(null);

            if (location == null) {
                continue;
            }

            double distance = calculateDistance(
                    latitude,
                    longitude,
                    location.getLatitude(),
                    location.getLongitude()
            );

            if (distance <= radius) {

                nearbyDrivers.add(
                        new NearbyDriverResponse(
                                driver.getId(),
                                driver.getName(),
                                location.getLatitude(),
                                location.getLongitude(),
                                distance
                        )
                );
            }
        }

        return nearbyDrivers;
    }

    public double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        final double EARTH_RADIUS = 6371.0; // kilometers

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        +
                        Math.cos(Math.toRadians(lat1))
                                * Math.cos(Math.toRadians(lat2))
                                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
        );

        return EARTH_RADIUS * c;
    }
}