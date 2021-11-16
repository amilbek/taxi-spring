package com.example.taxi.services;

import com.example.taxi.entity.Car;
import com.example.taxi.entity.User;
import com.example.taxi.models.CarRequest;
import com.example.taxi.repository.CarRepository;
import com.example.taxi.models.CarTariffRequest;
import com.example.taxi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CarService {
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Autowired
    public CarService(CarRepository carRepository, UserRepository userRepository) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    public boolean saveCar(CarRequest carRequest, String username) {
        Car byCarNumber = carRepository.findByCarNumber(carRequest.getCarNumber());
        User user = userRepository.findByUsername(username);
        if (byCarNumber != null || user == null) {
            return false;
        }
        Car car = new Car(carRequest.getCarNumber(), carRequest.getCarModel(),
                carRequest.getCarColor());
        car.setUser(user);
        carRepository.save(car);
        return true;
    }

    public Car getCarById(Integer id) {
        return carRepository.findById(id.longValue()).orElse(null);
    }

    public Car getCarByCarNumber(String carNumber) {
        return carRepository.findByCarNumber(carNumber);
    }

    public boolean updateCar(CarRequest carRequest) {
        Optional<Car> carOptional = carRepository.findById(carRequest.getId().longValue());
        Car car = carOptional.orElse(null);
        assert car != null;
        car.setCarNumber(carRequest.getCarNumber());
        car.setCarModel(carRequest.getCarModel());
        carRepository.save(car);
        return true;
    }

    public boolean deleteCarById(Integer id) {
        Optional<Car> carOptional = carRepository.findById(id.longValue());
        Car car = carOptional.orElse(null);
        assert car != null;
        carRepository.delete(car);
        return true;
    }

    public void deleteCarByCarNumber(String carNumber) {
        Car car = carRepository.findByCarNumber(carNumber);
        assert car != null;
        carRepository.delete(car);
    }

    public boolean changeTariff(CarTariffRequest carTariffRequest) {
        Optional<Car> carOptional = carRepository.findById(carTariffRequest.getId().longValue());
        Car car = carOptional.orElse(null);
        if (car == null) {
            return false;
        }
        car.setTariff(carTariffRequest.getTariff());
        carRepository.save(car);
        return true;
    }

    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        carRepository.findAll().forEach(cars::add);
        return cars;
    }

    public Car getCarByUser(Integer id) {
        Optional<User> userOptional = userRepository.findById(id.longValue());
        User user = userOptional.orElse(null);
        return carRepository.findByUser(user);
    }

    public boolean deleteCarByDriver(Integer id) {
        Optional<User> userOptional = userRepository.findById(id.longValue());
        User user = userOptional.orElse(null);
        Car car = carRepository.findByUser(user);
        if (user == null) {
            return false;
        }
        log.info(user.toString());
        log.info(car.toString());
        carRepository.deleteById(car.getId());
        return true;
    }
}
