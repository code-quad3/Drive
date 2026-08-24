package com.uber.uber_backend.repository;

import com.uber.uber_backend.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RideRepository extends JpaRepository<Ride, UUID> {

    List<Ride> findByRiderId(UUID riderId);

    List<Ride> findByDriverId(UUID driverId);
}