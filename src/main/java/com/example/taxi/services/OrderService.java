package com.example.taxi.services;

import com.example.taxi.entity.Car;
import com.example.taxi.entity.Order;
import com.example.taxi.entity.User;
import com.example.taxi.models.OrderRequest;
import com.example.taxi.repository.CarRepository;
import com.example.taxi.repository.OrderRepository;
import com.example.taxi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.sqrt;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        CarRepository carRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    public boolean saveOrder(OrderRequest orderRequest, String username) {
        Order order = new Order(orderRequest.getAddressFrom(), orderRequest.getAddressTo(),
                getDistance(), orderRequest.getTariff(), getPrice(orderRequest.getTariff()));
        User user = userRepository.findByUsername(username);
        order.setUser(user);
        orderRepository.save(order);
        return true;
    }

    public Order getOrder(Integer id) {
        return orderRepository.findById(id.longValue()).orElse(null);
    }

    public boolean acceptOrder(String username, Integer id) {
        Optional<Order> orderOptional = orderRepository.findById(id.longValue());
        Order order = orderOptional.orElse(null);
        User driver = userRepository.findByUsername(username);
        Car car = carRepository.findByUser(driver);
        assert driver != null;
        assert order != null;
        if (!order.getOrderStatus().equals("ordered") || Boolean.TRUE.equals(!driver.getIsAvailable())
        || !order.getTariff().equals(car.getTariff())) {
            return false;
        }
        order.setDriver(driver);
        order.setOrderStatus("accepted");
        orderRepository.save(order);
        return true;
    }

    public boolean waitClient(Integer orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId.longValue());
        Order order = orderOptional.orElse(null);
        assert order != null;
        if (!order.getOrderStatus().equals("accepted")) {
            return false;
        }
        order.setOrderStatus("waited");
        orderRepository.save(order);
        return true;
    }

    public boolean startOrder(Integer id) {
        Optional<Order> orderOptional = orderRepository.findById(id.longValue());
        Order order = orderOptional.orElse(null);
        assert order != null;
        if (!order.getOrderStatus().equals("accepted")) {
            return false;
        }
        order.setOrderStatus("started");
        order.setOrderStartTime(dateTimeFormatter());
        orderRepository.save(order);
        return true;
    }

    public boolean cancelOrder(Integer id) {
        Optional<Order> orderOptional = orderRepository.findById(id.longValue());
        Order order = orderOptional.orElse(null);
        assert order != null;
        if (order.getOrderStatus().equals("completed")) {
            return false;
        }
        order.setOrderStatus("canceled");
        order.setOrderEndTime(dateTimeFormatter());

        orderRepository.save(order);
        return true;
    }

    public boolean completeOrder(Integer id) {
        Optional<Order> orderOptional = orderRepository.findById(id.longValue());
        Order order = orderOptional.orElse(null);
        assert order != null;
        if (!order.getOrderStatus().equals("started")) {
            return false;
        }
        order.setOrderStatus("completed");
        order.setOrderEndTime(dateTimeFormatter());
        orderRepository.save(order);
        return true;
    }

    public double getDistance() {
        double min = 0.0;
        double max = 10.0;
        double addressFromX = (Math.random() * ((max - min) + 1)) + min;
        double addressToX = (Math.random() * ((max - min) + 1)) + min;
        double addressFromY = (Math.random() * ((max - min) + 1)) + min;
        double addressToY = (Math.random() * ((max - min) + 1)) + min;
        double x = addressToX - addressFromX;
        double y = addressToY - addressFromY;
        return Math.ceil(sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
    }

    public double getPrice(String chosenTariff) {
        double tariffPrice = 0.0;
        switch (chosenTariff) {
            case "economy":
                tariffPrice = 80.0;
                break;
            case "comfort":
                tariffPrice = 120.0;
                break;
            case "business":
                tariffPrice = 140.0;
                break;
            case "minivan":
                tariffPrice = 150.0;
                break;
            case "cargo":
                tariffPrice = 180.0;
                break;
            default:
                break;
        }
        return getDistance() * tariffPrice;
    }


    private String dateTimeFormatter() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return currentTime.format(myFormatObj);
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        return orders;
    }

    public Order getCurrentOrderByUser(User user) {
        Order order = orderRepository.findByUser(user);
        if (order.getOrderStatus().equals("ordered")) {
            return order;
        }
        return null;
    }

    public List<Order> getOrdersByUser(User user) {
        List<Order> orders = new ArrayList<>();
        orders.add(orderRepository.findAllByUser(user));
        return orders;
    }

    public List<Order> getOrdersByDriver(User driver) {
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(order -> {
            if (order.getOrderStatus().equals("completed") && Boolean.TRUE.equals(driver.getIsDriver())) {
                orders.add(order);
            }
        });
        return orders;
    }

    public List<Order> getAvailableOrders(User user) {
        List<Order> orders = new ArrayList<>();
        Car car = carRepository.findByUser(user);
        orderRepository.findAll().forEach(order -> {
            if (order.getOrderStatus().equals("ordered") && order.getTariff().equals(car.getTariff())) {
                orders.add(order);
            }
        });
        return orders;
    }

    public boolean deleteOrderByUser(Integer id) {
        Optional<User> userOptional = userRepository.findById(id.longValue());
        User user = userOptional.orElse(null);
        if (user == null) {
            return false;
        }
        orderRepository.deleteOrderByUser(user);
        return true;
    }
}
