package com.example.taxi.repository;

import com.example.taxi.entity.Order;
import com.example.taxi.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
    Order findAllByUser(User user);
    void deleteOrderByUser(User user);
    Order findByUser(User user);
}
