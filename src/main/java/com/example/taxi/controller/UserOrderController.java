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
        orderRequest.setUser(user);
        boolean result = orderService.saveOrder(orderRequest, username);
        if (!result) {
            model.addAttribute("fail", Constants.SOMETHING_WRONG);
            return "orders/add-order";
        }
        model.addAttribute("user", user);
        return "redirect:/users/user" + username;
    }


    @GetMapping("/users/{username}/add-order")
    public String addOrder(@PathVariable(value = "username") String username, Model model) {
        model.addAttribute("username", username);
        return "orders/add-order";
    }

    @GetMapping("/users/{username}/cancel-order")
    public String cancelOrder(@PathVariable(value = "username") String username, Model model) {
        User user = userService.getUserByUsername(username);
        boolean result = orderService.cancelOrder(user.getId().intValue());
        Iterable<Order> orders = orderService.getOrdersByUser(Math.toIntExact(user.getId()));
        model.addAttribute("orders", orders);
        model.addAttribute("user", user);
        if (!result) {
            model.addAttribute("fail", Constants.SOMETHING_WRONG);
            return "users/user-page";
        }
        model.addAttribute("success", Constants.CANCELED_SUCCESSFULLY);
        return "users/user-page";
    }
}
