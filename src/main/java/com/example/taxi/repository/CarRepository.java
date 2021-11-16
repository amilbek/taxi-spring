package com.example.taxi.repository;

import com.example.taxi.entity.Car;
import com.example.taxi.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface CarRepository extends CrudRepository<Car, Long> {
    Car findByCarNumber(String carNumber);
    Car findByUser(User user);
}
