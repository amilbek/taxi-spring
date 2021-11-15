package com.example.taxi.controller;

import com.example.taxi.constants.Constants;
import com.example.taxi.entity.Order;
import com.example.taxi.entity.User;
import com.example.taxi.models.OrderRequest;
import com.example.taxi.services.OrderService;
import com.example.taxi.services.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log
@Controller
public class UserOrderController {

    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public UserOrderController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping("/users/{username}/order")
    public String addOrder(@PathVariable(value = "username") String username,
                           @RequestParam String addressFrom,
                           @RequestParam String addressTo,
                           @RequestParam String tariff,
                           Model model) {
        User user = userService.getUserByUsername(username);
        OrderRequest orderRequest = new OrderRequest(addressFrom, addressTo, tariff);
        boolean result = orderService.saveOrder(orderRequest, username);
        if (!result) {
            model.addAttribute("failed", Constants.SOMETHING_WRONG);
            return "orders/add-order";
        }
        Order order = orderService.getCurrentOrderByUser(user);
        log.info(order.toString());
        model.addAttribute("succeed", Constants.ORDERED_SUCCESSFULLY);
        model.addAttribute("user", user);
        model.addAttribute("order", order);
        return "redirect:/users/{username}/order-page";
    }

    @GetMapping("/users/{username}/add-order")
    public String addOrder(@PathVariable(value = "username") String username, Model model) {
        User user = userService.getUserByUsername(username);
        model.addAttribute("user", user);
        return "orders/add-order";
    }

    @GetMapping("/users/{username}/order-page")
    public String getUserOrderPage(@PathVariable(value = "username") String username, Model model) {
        User user = userService.getUserByUsername(username);
        Order order = orderService.getCurrentOrderByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("order", order);
        return "orders/user-order";
    }

    @PostMapping("/users/{username}/{id}/cancel-order")
    public String cancelOrder(@PathVariable(value = "username") String username,
                              @PathVariable(value = "id") Integer id, Model model) {
        User user = userService.getUserByUsername(username);
        Order order = orderService.getOrder(id);
        boolean result = orderService.cancelOrder(user.getId().intValue());
        Iterable<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        model.addAttribute("user", user);
        if (!result) {
            model.addAttribute("order", order);
            model.addAttribute("failed", Constants.SOMETHING_WRONG);
            return "redirect:/users/{username}/order-page";
        }
        model.addAttribute("success", Constants.CANCELED_SUCCESSFULLY);
        return "users/user-orders";
    }
}
