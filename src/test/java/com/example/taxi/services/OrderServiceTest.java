package com.example.taxi.services;

import com.example.taxi.constants.Constants;
import com.example.taxi.entity.Order;
import com.example.taxi.entity.User;
import com.example.taxi.models.OrderRequest;
import com.example.taxi.repository.OrderRepository;
import com.example.taxi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService sut;

    @Mock
    OrderRepository orderRepository;

    @Mock
    UserRepository userRepository;

    @Test
    void saveOrder() {

        User user = new User("firstName", "lastName",
                "87775536956", "username", "password");
        User driver = new User("default", "driver",
                "87775536956", "driver-name", "password");
        List<Order> orders = new ArrayList<>();

        OrderRequest orderRequest = new OrderRequest("addressFrom", "addressTo",
                "tariff");

        when(orderRepository.findAll()).thenReturn(orders);

        when(userRepository.findByUsername("username")).thenReturn(user);
        when(userRepository.findByUsername(Constants.DEFAULT_DRIVER)).thenReturn(driver);

        boolean result = sut.saveOrder(orderRequest, "username");
        assertTrue(result);
    }

    @Test
    void getOrder() {
        Order expected = new Order("addressFrom", "addressTo",
                100.0, "tariff", 10.0);

        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(expected));

        Order actual = sut.getOrder(1);

        assertEquals(expected, actual);
    }

    @Test
    void getCurrentOrderByUser() {
        List<Order> orders = new ArrayList<>();
        Order expected = new Order("addressFrom", "addressTo",
                100.0, "tariff", 10.0);
        expected.setOrderStatus(Constants.WAITED_STATUS);

        User user = new User("firstName", "lastName",
                "87775536956", "username", "password");
        expected.setUser(user);

        orders.add(expected);

        when(orderRepository.findAll()).thenReturn(orders);

        Order actual = sut.getCurrentOrderByUser(user);

        assertEquals(expected, actual);
    }
}