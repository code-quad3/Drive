package com.uber.uber_backend.service;

import com.uber.uber_backend.model.Driver;

import java.util.List;

public interface DriverService {

    Driver createDriver(Driver driver);

    List<Driver> getAllDrivers();
}