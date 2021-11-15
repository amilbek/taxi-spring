package com.example.taxi.models;

import lombok.Data;

@Data
public class DriverStatusRequest {
    private Integer driverId;
    private Boolean driverStatus;

    public DriverStatusRequest(Integer driverId, Boolean driverStatus) {
        this.driverId = driverId;
        this.driverStatus = driverStatus;
    }
}
