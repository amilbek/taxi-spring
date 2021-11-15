package com.example.taxi.models;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class UserRequest {
    private Integer id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String username;
    private String password;
    private String role;
    private Boolean isDriver;
    private String driverIdNumber;
    private String driverLicenseNumber;
    private Date licenseExpDate;

    public UserRequest(String firstName, String lastName, String phoneNumber,
                       String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
    }

    public UserRequest(Integer id, String firstName, String lastName, String phoneNumber,
                       String username) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.username = username;
    }

    public UserRequest(Integer id, Boolean isDriver, String driverIdNumber, String driverLicenseNumber,
                       Date licenseExpDate) {
        this.id = id;
        this.isDriver = isDriver;
        this.driverIdNumber = driverIdNumber;
        this.driverLicenseNumber = driverLicenseNumber;
        this.licenseExpDate = licenseExpDate;
    }
}
