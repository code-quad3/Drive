package com.uber.uber_backend.service;

import com.uber.uber_backend.dto.CreateRideRequest;
import com.uber.uber_backend.dto.RideEvent;
import com.uber.uber_backend.kafka.RideEventProducer;
import com.uber.uber_backend.model.Ride;
import com.uber.uber_backend.model.RideStatus;
import com.uber.uber_backend.repository.RideRepository;
import org.springframework.stereotype.Service;

import com.uber.uber_backend.model.Driver;
import com.uber.uber_backend.model.DriverLocation;
import com.uber.uber_backend.model.DriverStatus;
import com.uber.uber_backend.repository.DriverRepository;
import com.uber.uber_backend.repository.DriverLocationRepository;

import com.uber.uber_backend.dto.FareCalculation;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final RideEventProducer rideEventProducer;
    private final DriverRepository driverRepository;
    private final DriverLocationRepository driverLocationRepository;
    private final FareService fareService;

    public RideService(
            RideRepository rideRepository,
            RideEventProducer rideEventProducer,
            DriverRepository driverRepository,
            DriverLocationRepository driverLocationRepository,
            FareService fareService

    ) {
        this.rideRepository = rideRepository;
        this.rideEventProducer = rideEventProducer;
        this.driverRepository = driverRepository;
        this.driverLocationRepository = driverLocationRepository;
        this.fareService = fareService;
    }

    public Ride createRide(CreateRideRequest request) {

        Ride ride = new Ride();

        ride.setRiderId(request.getRiderId());

        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());

        ride.setDestinationLatitude(request.getDestinationLatitude());
        ride.setDestinationLongitude(request.getDestinationLongitude());

        ride.setStatus(RideStatus.REQUESTED);

        // Save to PostgreSQL
        Ride savedRide = rideRepository.save(ride);

        // Create Kafka event
        RideEvent event = new RideEvent();

        event.setRideId(savedRide.getId());
        event.setRiderId(savedRide.getRiderId());
        event.setDriverId(savedRide.getDriverId());
        event.setEvent("RIDE_CREATED");

        // Publish to Kafka
        rideEventProducer.publishRideEvent(event);

        return savedRide;
    }


    public Ride assignDriver(UUID rideId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new RuntimeException(
                    "Ride is not in REQUESTED state"
            );
        }

        Driver nearestDriver = findNearestAvailableDriver(ride, null);

        if (nearestDriver == null) {
            throw new RuntimeException(
                    "No available driver"
            );
        }

        ride.setDriverId(nearestDriver.getId());
        ride.setStatus(RideStatus.DRIVER_ASSIGNED);

        Ride savedRide = rideRepository.save(ride);

        RideEvent event = new RideEvent();

        event.setRideId(savedRide.getId());
        event.setRiderId(savedRide.getRiderId());
        event.setDriverId(savedRide.getDriverId());
        event.setEvent("DRIVER_ASSIGNED");

        rideEventProducer.publishRideEvent(event);

        return savedRide;
    }

    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2
    ) {

        final double EARTH_RADIUS_KM = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2)
                * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    public Ride acceptRide(UUID rideId, UUID driverId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Ride must be assigned first
        if (ride.getStatus() != RideStatus.DRIVER_ASSIGNED) {
            throw new RuntimeException(
                    "Ride is not waiting for driver acceptance"
            );
        }

        // Make sure the accepting driver is the assigned driver
        if (!driverId.equals(ride.getDriverId())) {
            throw new RuntimeException(
                    "Driver is not assigned to this ride"
            );
        }

        // Change ride status
        ride.setStatus(RideStatus.ACCEPTED);

        Ride savedRide = rideRepository.save(ride);

        // Create Kafka event
        RideEvent event = new RideEvent();

        event.setRideId(savedRide.getId());
        event.setRiderId(savedRide.getRiderId());
        event.setDriverId(savedRide.getDriverId());
        event.setEvent("RIDE_ACCEPTED");

        // Publish event
        rideEventProducer.publishRideEvent(event);

        return savedRide;
    }

    public Ride startRide(UUID rideId, UUID driverId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Ride must be ACCEPTED
        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new RuntimeException(
                    "Ride cannot be started"
            );
        }

        // Make sure this is the assigned driver
        if (!driverId.equals(ride.getDriverId())) {
            throw new RuntimeException(
                    "Driver is not assigned to this ride"
            );
        }

        // Find driver
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // Driver must be available
        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new RuntimeException(
                    "Driver is not available"
            );
        }

        // Ride starts
        ride.setStatus(RideStatus.STARTED);

        // Driver is now busy
        driver.setStatus(DriverStatus.ON_TRIP);

        // Save both
        driverRepository.save(driver);

        Ride savedRide = rideRepository.save(ride);

        // Kafka event
        RideEvent event = new RideEvent();

        event.setRideId(savedRide.getId());
        event.setRiderId(savedRide.getRiderId());
        event.setDriverId(savedRide.getDriverId());
        event.setEvent("RIDE_STARTED");

        rideEventProducer.publishRideEvent(event);

        return savedRide;
    }

    public Ride completeRide(UUID rideId, UUID driverId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Ride must be STARTED
        if (ride.getStatus() != RideStatus.STARTED) {
            throw new RuntimeException(
                    "Ride cannot be completed"
            );
        }

        // Only assigned driver can complete the ride
        if (!driverId.equals(ride.getDriverId())) {
            throw new RuntimeException(
                    "Driver is not assigned to this ride"
            );
        }

        // Find driver
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // Driver should currently be on trip
        if (driver.getStatus() != DriverStatus.ON_TRIP) {
            throw new RuntimeException(
                    "Driver is not currently on a trip"
            );
        }

        // Calculate distance and fare
        FareCalculation calculation = fareService.calculateFare(
                ride.getPickupLatitude(),
                ride.getPickupLongitude(),
                ride.getDestinationLatitude(),
                ride.getDestinationLongitude()
        );

        // Store distance and fare
        ride.setDistanceKm(calculation.getDistanceKm());
        ride.setFare(calculation.getFare());

        // Complete ride
        ride.setStatus(RideStatus.COMPLETED);

        // Driver becomes available again
        driver.setStatus(DriverStatus.AVAILABLE);

        // Save changes
        driverRepository.save(driver);

        Ride savedRide = rideRepository.save(ride);

        // Create Kafka event
        RideEvent event = new RideEvent();

        event.setRideId(savedRide.getId());
        event.setRiderId(savedRide.getRiderId());
        event.setDriverId(savedRide.getDriverId());
        event.setEvent("RIDE_COMPLETED");

        // Publish event
        rideEventProducer.publishRideEvent(event);

        return savedRide;
    }

    public Ride cancelRide(UUID rideId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Only these states can be cancelled
        if (ride.getStatus() != RideStatus.REQUESTED &&
                ride.getStatus() != RideStatus.DRIVER_ASSIGNED &&
                ride.getStatus() != RideStatus.ACCEPTED) {

            throw new RuntimeException(
                    "Ride cannot be cancelled"
            );
        }

        UUID driverId = ride.getDriverId();

        // Cancel ride
        ride.setStatus(RideStatus.CANCELLED);

        // If a driver was assigned, make them available again
        if (driverId != null) {

            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            driver.setStatus(DriverStatus.AVAILABLE);

            driverRepository.save(driver);
        }

        Ride savedRide = rideRepository.save(ride);

        // Create Kafka event
        RideEvent event = new RideEvent();

        event.setRideId(savedRide.getId());
        event.setRiderId(savedRide.getRiderId());
        event.setDriverId(savedRide.getDriverId());
        event.setEvent("RIDE_CANCELLED");

        rideEventProducer.publishRideEvent(event);

        return savedRide;
    }

    public Ride getRideById(UUID rideId) {

        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
    }

    public List<Ride> getRidesByDriver(UUID driverId) {

        return rideRepository.findByDriverId(driverId);
    }

    public List<Ride> getRidesByRider(UUID riderId) {

        return rideRepository.findByRiderId(riderId);
    }

    public Ride rejectRide(UUID rideId, UUID driverId) {

        // 1. Find ride
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // 2. Ride must be DRIVER_ASSIGNED
        if (ride.getStatus() != RideStatus.DRIVER_ASSIGNED) {
            throw new RuntimeException(
                    "Ride is not waiting for driver acceptance"
            );
        }

        // 3. Only assigned driver can reject
        if (!driverId.equals(ride.getDriverId())) {
            throw new RuntimeException(
                    "Driver is not assigned to this ride"
            );
        }

        // 4. Find rejecting driver
        Driver rejectingDriver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // 5. Release rejecting driver
        rejectingDriver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(rejectingDriver);

        // 6. Find another available driver
        Driver newDriver = findNearestAvailableDriver(
                ride,
                driverId
        );

        // 7. No replacement driver
        if (newDriver == null) {

            // No replacement driver
            ride.setDriverId(null);
            ride.setStatus(RideStatus.REQUESTED);

            return rideRepository.save(ride);
        }

        // 8. Assign new driver
        ride.setDriverId(newDriver.getId());
        ride.setStatus(RideStatus.DRIVER_ASSIGNED);

        // 9. Save ride
        Ride savedRide = rideRepository.save(ride);

        // 10. Publish Kafka event
        RideEvent event = new RideEvent();

        event.setRideId(savedRide.getId());
        event.setRiderId(savedRide.getRiderId());
        event.setDriverId(newDriver.getId());
        event.setEvent("DRIVER_REASSIGNED");

        rideEventProducer.publishRideEvent(event);

        return savedRide;
    }

    private Driver findNearestAvailableDriver(Ride ride, UUID excludedDriverId) {

        List<Driver> availableDrivers =
                driverRepository.findByStatus(DriverStatus.AVAILABLE);

        Driver nearestDriver = null;
        double shortestDistance = Double.MAX_VALUE;

        for (Driver driver : availableDrivers) {

            // Don't select the driver who rejected
            if (excludedDriverId != null &&
                    driver.getId().equals(excludedDriverId)) {
                continue;
            }

            Optional<DriverLocation> location =
                    driverLocationRepository.findByDriverId(driver.getId());

            if (location.isEmpty()) {
                continue;
            }

            double distance = calculateDistance(
                    ride.getPickupLatitude(),
                    ride.getPickupLongitude(),
                    location.get().getLatitude(),
                    location.get().getLongitude()
            );

            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearestDriver = driver;
            }
        }

        return nearestDriver;
    }

}