package com.example.taxi.entity;

import com.example.taxi.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="cars")
@Getter
@Setter
@NoArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String carNumber;
    private String carModel;
    private String carColor;
    private String tariff;

    @OneToOne(targetEntity=User.class,cascade=CascadeType.ALL)
    private User user;

    public Car(String carNumber, String carModel, String carColor) {
        this.carNumber = carNumber;
        this.carModel = carModel;
        this.carColor = carColor;
        tariff = "Not Chosen";
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", carNumber='" + carNumber + '\'' +
                ", carModel='" + carModel + '\'' +
                ", carColor='" + carColor + '\'' +
                ", tariff='" + tariff + '\'' +
                ", user=" + user +
                '}';
    }
}
