package com.example.taxi.services;

import com.example.taxi.constants.Constants;
import com.example.taxi.entity.Car;
import com.example.taxi.entity.Order;
import com.example.taxi.entity.User;
import com.example.taxi.enums.Status;
import com.example.taxi.models.OrderRequest;
import com.example.taxi.repository.CarRepository;
import com.example.taxi.repository.OrderRepository;
import com.example.taxi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.sqrt;

@Slf4j
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
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        for (Order o : orders) {
            if (o.getUser().equals(user) && !o.getOrderStatus().equals(Constants.COMPLETED_STATUS)) {
                return false;
            }
        }
        User driver = userRepository.findByUsername(Constants.DEFAULT_DRIVER);
        order.setUser(user);
        order.setDriver(driver);
        orderRepository.save(order);
        return true;
    }

    public Order getOrder(Integer id) {
        return orderRepository.findById(id.longValue()).orElse(null);
    }

    public boolean acceptOrder(String username, Integer id) {
        User driver = userRepository.findByUsername(username);
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        for (Order o : orders) {
            if (o.getDriver().equals(driver) && !o.getOrderStatus().equals(Constants.COMPLETED_STATUS)) {
                return false;
            }
        }
        Order order = orderRepository.findById(id.longValue()).orElse(null);
        Car car = carRepository.findByUser(driver);
        assert driver != null;
        assert order != null;
        if (!order.getOrderStatus().equals(Constants.ORDERED_STATUS) ||
                Boolean.TRUE.equals(!driver.getIsAvailable()) ||
                !order.getTariff().equals(car.getTariff())) {
            return false;
        }
        order.setDriver(driver);
        order.setOrderStatus(Constants.ACCEPTED_STATUS);
        orderRepository.save(order);
        return true;
    }

    public boolean waitClient(Integer orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId.longValue());
        Order order = orderOptional.orElse(null);
        assert order != null;
        if (order.getOrderStatus().equals(Constants.ACCEPTED_STATUS)) {
            order.setOrderStatus(Constants.WAITED_STATUS);
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    public boolean startOrder(Integer id) {
        Optional<Order> orderOptional = orderRepository.findById(id.longValue());
        Order order = orderOptional.orElse(null);
        assert order != null;
        if (order.getOrderStatus().equals(Constants.ACCEPTED_STATUS) ||
                order.getOrderStatus().equals(Constants.WAITED_STATUS)) {
            order.setOrderStatus(Constants.STARTED_STATUS);
            order.setOrderStartTime(dateTimeFormatter());
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    public boolean completeOrder(Integer id) {
        Optional<Order> orderOptional = orderRepository.findById(id.longValue());
        Order order = orderOptional.orElse(null);
        assert order != null;
        if (order.getOrderStatus().equals(Constants.STARTED_STATUS)) {
            order.setOrderStatus(Constants.COMPLETED_STATUS);
            order.setOrderEndTime(dateTimeFormatter());
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    public boolean cancelOrder(Integer id) {
        Optional<Order> orderOptional = orderRepository.findById(id.longValue());
        Order order = orderOptional.orElse(null);
        assert order != null;
        if (order.getOrderStatus().equals(Constants.ORDERED_STATUS)) {
            order.setOrderStatus(Constants.CANCELED_STATUS);
            order.setOrderEndTime(dateTimeFormatter());
            orderRepository.save(order);
            return true;
        }
        return false;
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
            case Constants.ECONOMY_TARIFF:
                tariffPrice = 80.0;
                break;
            case Constants.COMFORT_TARIFF:
                tariffPrice = 120.0;
                break;
            case Constants.BUSINESS_TARIFF:
                tariffPrice = 140.0;
                break;
            case Constants.MINIVAN_TARIFF:
                tariffPrice = 150.0;
                break;
            case Constants.CARGO_TARIFF:
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
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        for (Order order : orders) {
            if (order.getUser().equals(user) &&
                            (!order.getOrderStatus().equals(Constants.COMPLETED_STATUS) &&
                                    !order.getOrderStatus().equals(Constants.CANCELED_STATUS))) {
                return order;
            }
        }
        return null;
    }

    public Order getCurrentOrderByDriver(User driver) {
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        for (Order order : orders) {
            if (order.getDriver().equals(driver) &&
                    (order.getOrderStatus().equals(Constants.ACCEPTED_STATUS) ||
                            order.getOrderStatus().equals(Constants.WAITED_STATUS) ||
                            order.getOrderStatus().equals(Constants.STARTED_STATUS))) {
                return order;
            }
        }
        return null;
    }

    public List<Order> getOrdersByUser(User user) {
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(order -> {
            if (order.getUser().equals(user)) {
                orders.add(order);
            }
        });
        return orders;
    }

    public List<Order> getOrdersByDriver(User driver) {
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(order -> {
            if (order.getDriver().equals(driver)) {
                orders.add(order);
            }
        });
        return orders;
    }

    public List<Order> getAvailableOrders(User user) {
        List<Order> orders = new ArrayList<>();
        Car car = carRepository.findByUser(user);
        if (car == null) {
            return Collections.emptyList();
        }
        orderRepository.findAll().forEach(order -> {
            if (order.getOrderStatus().equals(Constants.ORDERED_STATUS) &&
                    order.getTariff().equals(car.getTariff()) &&
                    order.getUser().getStatus().equals(Status.ACTIVE)) {
                orders.add(order);
            }
        });
        return orders;
    }

    public Double rateOrder(User driver, Double rating) {
        Double averageRate = driver.getRating();
        List<Order> orders = getOrdersByDriver(driver);
        int orderNumber = 0;
        if (!orders.isEmpty()) {
            orderNumber = orders.size();
        }
        Double newAverageRating = (averageRate + rating) / orderNumber + 1;
        log.info("newAverageRating: {}", newAverageRating);
        DecimalFormat df = new DecimalFormat("#,##");
        driver.setRating(Double.valueOf(df.format(newAverageRating)));
        userRepository.save(driver);
        return newAverageRating;
    }
}
