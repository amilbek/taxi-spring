package com.example.taxi.models;

import lombok.Data;

@Data
public class CarTariffRequest {
    private Integer id;
    private String tariff;

    public CarTariffRequest(Integer id, String tariff) {
        this.id = id;
        this.tariff = tariff;
    }
}
