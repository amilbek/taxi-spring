package com.example.taxi.controller;

import com.example.taxi.constants.Constants;
import com.example.taxi.entity.Order;
import com.example.taxi.entity.User;
import com.example.taxi.services.OrderService;
import com.example.taxi.services.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Log
@Controller
public class DriverOrderController {

    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public DriverOrderController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/users/{username}/avail-orders")
    public String getOrders(@PathVariable(value = "username") String username, Model model) {
        User user = userService.getUserByUsername(username);
        Iterable<Order> orders = orderService.getAvailableOrders();
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        return "drivers/driver-orders";
    }

    @PostMapping("/users/{username}/{id}/accept-order")
    public String acceptOrder(@PathVariable(value = "username") String username,
                              @PathVariable(value = "id") Integer orderId, Model model) {
        boolean result = orderService.acceptOrder(username, orderId);
        Iterable<Order> orders = orderService.getAvailableOrders();
        model.addAttribute("orders", orders);
        if (!result) {
            model.addAttribute("fail", Constants.SOMETHING_WRONG);
            return "drivers/driver-orders";
        }
        model.addAttribute("succeed", Constants.ACCEPTED_SUCCESSFULLY);
        return "drivers/driver-orders";
    }

    @PostMapping("/users/{username}/{id}/wait-client")
    public String waitClient(@PathVariable(value = "username") String username,
                              @PathVariable(value = "id") Integer orderId, Model model) {
        boolean result = orderService.waitClient(orderId);
        Iterable<Order> orders = orderService.getAvailableOrders();
        model.addAttribute("orders", orders);
        if (!result) {
            model.addAttribute("fail", Constants.SOMETHING_WRONG);
            return "drivers/driver-orders";
        }
        model.addAttribute("succeed", Constants.ACCEPTED_SUCCESSFULLY);
        return "drivers/driver-orders";
    }

    @PostMapping("/users/{username}/{id}/start-order")
    public String startOrder(@PathVariable(value = "username") String username,
                             @PathVariable(value = "id") Integer id, Model model) {
        User user = userService.getUserByUsername(username);
        boolean result = orderService.startOrder(id);
        Iterable<Order> orders = orderService.getAvailableOrders();
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        if (!result) {
            model.addAttribute("fail", Constants.SOMETHING_WRONG);
            return "drivers/driver-orders";
        }
        model.addAttribute("succeed", Constants.START_ORDER);
        return "drivers/driver-orders";
    }

    @PostMapping("/users/{username}/{id}/complete-order")
    public String completeOrder(@PathVariable(value = "username") String username,
                                @PathVariable(value = "id") Integer id, Model model) {
        User user = userService.getUserByUsername(username);
        boolean result = orderService.completeOrder(id);
        Iterable<Order> orders = orderService.getAvailableOrders();
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        if (!result) {
            model.addAttribute("fail", Constants.SOMETHING_WRONG);
            return "drivers/driver-orders";
        }
        model.addAttribute("succeed", Constants.COMPLETED_SUCCESSFULLY);
        return "drivers/driver-orders";
    }
}
