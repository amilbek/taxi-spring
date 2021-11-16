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

    @PostMapping("/users/{username}/accept-order/{id}")
    public String acceptOrder(@PathVariable(value = "username") String username,
                              @PathVariable(value = "id") Integer id, Model model) {
        boolean result = orderService.acceptOrder(username, id);
        if (!result) {
            Iterable<Order> orders = orderService.getAvailableOrders();
            model.addAttribute("orders", orders);
            model.addAttribute("failed", Constants.NOT_ACCEPTED);
            return "redirect:/users/{username}/avail-orders";
        }
        User user = userService.getUserByUsername(username);
        Order order = orderService.getOrder(id);
        log.info(order.toString());
        model.addAttribute("user", user);
        model.addAttribute("order", order);
        model.addAttribute("succeed", Constants.ACCEPTED_SUCCESSFULLY);
        return "redirect:/users/{username}/driver-order/{id}";
    }

    @GetMapping("/users/{username}/driver-order/{id}")
    public String getCurrentOrder(@PathVariable(value = "username") String username,
                                  @PathVariable(value = "id") Integer id, Model model) {
        User user = userService.getUserByUsername(username);
        Order order = orderService.getOrder(id);
        model.addAttribute("user", user);
        model.addAttribute("order", order);
        return "drivers/driver-order";
    }

    @PostMapping("/users/{username}/wait-client/{id}")
    public String waitClient(@PathVariable(value = "username") String username,
                              @PathVariable(value = "id") Integer id, Model model) {
        boolean result = orderService.waitClient(id);
        if (!result) {
            Iterable<Order> orders = orderService.getAvailableOrders();
            model.addAttribute("orders", orders);
            model.addAttribute("failed", Constants.SOMETHING_WRONG);
            return "redirect:/users/{username}/avail-orders";
        }
        User user = userService.getUserByUsername(username);
        Order order = orderService.getOrder(id);
        log.info(order.toString());
        model.addAttribute("user", user);
        model.addAttribute("order", order);
        return "redirect:/users/{username}/driver-order/{id}";
    }

    @PostMapping("/users/{username}/start-order/{id}")
    public String startOrder(@PathVariable(value = "username") String username,
                             @PathVariable(value = "id") Integer id, Model model) {
        boolean result = orderService.startOrder(id);
        if (!result) {
            Iterable<Order> orders = orderService.getAvailableOrders();
            model.addAttribute("orders", orders);
            model.addAttribute("failed", Constants.SOMETHING_WRONG);
            return "redirect:/users/{username}/avail-orders";
        }
        User user = userService.getUserByUsername(username);
        Order order = orderService.getOrder(id);
        log.info(order.toString());
        model.addAttribute("user", user);
        model.addAttribute("order", order);
        return "redirect:/users/{username}/driver-order/{id}";
    }

    @PostMapping("/users/{username}/complete-order/{id}")
    public String completeOrder(@PathVariable(value = "username") String username,
                                @PathVariable(value = "id") Integer id, Model model) {
        boolean result = orderService.completeOrder(id);
        if (!result) {
            Iterable<Order> orders = orderService.getAvailableOrders();
            model.addAttribute("orders", orders);
            model.addAttribute("fail", Constants.SOMETHING_WRONG);
            return "redirect:/users/{username}/avail-orders";
        }
        User user = userService.getUserByUsername(username);
        Order order = orderService.getOrder(id);
        log.info(order.toString());
        model.addAttribute("user", user);
        model.addAttribute("order", order);
        return "redirect:/users/{username}/driver-order/{id}";
    }
}
