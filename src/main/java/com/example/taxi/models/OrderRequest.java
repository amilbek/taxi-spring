package com.example.taxi.models;

import com.example.taxi.entity.User;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderRequest {
    private Integer id;
    private String addressFrom;
    private String addressTo;
    private String tariff;
    private User user;

    public OrderRequest(String addressFrom, String addressTo, String tariff) {
        this.addressFrom = addressFrom;
        this.addressTo = addressTo;
        this.tariff = tariff;
    }
}
