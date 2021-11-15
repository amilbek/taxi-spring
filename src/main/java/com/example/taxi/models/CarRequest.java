package com.example.taxi.models;

import com.example.taxi.entity.User;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CarRequest {
    private Integer id;
    private String carNumber;
    private String carModel;
    private String carColor;
    private String tariff;
    private User user;

    public CarRequest(String carNumber, String carModel, String carColor) {
        this.carNumber = carNumber;
        this.carModel = carModel;
        this.carColor = carColor;
    }

    public CarRequest(Integer id, String carNumber, String carModel, String carColor) {
        this.id = id;
        this.carNumber = carNumber;
        this.carModel = carModel;
        this.carColor = carColor;
    }
}
