package com.uber.uber_backend.repository;

import com.uber.uber_backend.model.Driver;
import com.uber.uber_backend.model.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DriverRepository
        extends JpaRepository<Driver, UUID> {

    List<Driver> findByStatus(DriverStatus status);
}