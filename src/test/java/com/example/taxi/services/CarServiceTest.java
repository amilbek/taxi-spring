package com.example.taxi.services;

import com.example.taxi.entity.Car;
import com.example.taxi.entity.User;
import com.example.taxi.models.CarRequest;
import com.example.taxi.repository.CarRepository;
import com.example.taxi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @InjectMocks
    CarService sut;

    @Mock
    UserRepository userRepository;

    @Mock
    CarRepository carRepository;

    @Test
    void saveCar() {
        CarRequest carRequest = new CarRequest("carNumber", "carModel", "carColor");
        User user = new User("firstName", "lastName",
                "87775536956", "username", "password");

        when(carRepository.findByCarNumber("carNumber")).thenReturn(null);
        when(userRepository.findByUsername("username")).thenReturn(user);

        boolean result = sut.saveCar(carRequest, "username");

        assertTrue(result);
    }

    @Test
    void getCarById() {
        Car expected = new Car("carNumber", "carModel", "carColor");

        when(carRepository.findById(1L)).thenReturn(java.util.Optional.of(expected));

        Car actual = sut.getCarById(1);

        assertEquals(expected, actual);
    }

    @Test
    void getCarByCarNumber() {
        Car expected = new Car("carNumber", "carModel", "carColor");

        when(carRepository.findByCarNumber("carNumber")).thenReturn(expected);

        Car actual = sut.getCarByCarNumber("carNumber");

        assertEquals(expected, actual);
    }
}