package com.example.taxi.controller;

import com.example.taxi.constants.Constants;
import com.example.taxi.entity.Order;
import com.example.taxi.entity.User;
import com.example.taxi.models.UserRequest;
import com.example.taxi.services.CarService;
import com.example.taxi.services.OrderService;
import com.example.taxi.services.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Log
@Controller
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final CarService carService;

    @Autowired
    public UserController(UserService userService, OrderService orderService, CarService carService) {
        this.userService = userService;
        this.orderService = orderService;
        this.carService = carService;
    }

    @GetMapping("/users/{username}")
    public String getUserByUsername(@PathVariable(value ="username") String username, Model model) {
        log.info(username);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            log.info("Nothing about user");
            model.addAttribute("failed", Constants.USER_NOT_FOUND);
            return "auth/user-login";
        }
        log.info(user.toString());
        model.addAttribute("user", user);
        return "users/user-page";
    }

    @GetMapping("/users/{username}/edit-page")
    public String getEditPage(@PathVariable(value = "username") String username, Model model) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            log.info("Nothing about user");
            model.addAttribute("failed", Constants.USER_NOT_FOUND);
            return "auth/user-login";
        }
        model.addAttribute("user", user);
        return "users/user-edit";
    }

    @PostMapping("/users/{username}/edit")
    public String updateUser(@RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String phoneNumber,
                             @PathVariable(value = "username")
                                 @RequestParam String username,
                             Model model) {
        User user = userService.getUserByUsername(username);
        UserRequest userRequest = new UserRequest(Math.toIntExact(user.getId()), firstName, lastName,
                phoneNumber, username);
        boolean result = userService.updateUser(userRequest);
        if (!result) {
            model.addAttribute("failed", Constants.EDITING_FAILED);
            model.addAttribute("user", user);
            return "/users/user-page";
        }
        log.info(user.toString());
        log.info("User Updated");
        model.addAttribute("succeed", Constants.EDITING_SUCCEED);
        model.addAttribute("user", user);
        return "redirect:/users/" + username;
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable(value = "id") Integer id, Model model) {
        User user = userService.getUser(id);
        boolean result = userService.deleteUser(user);
        if (!result) {
            log.info(user.toString());
            model.addAttribute("user", user);
            model.addAttribute("failed", Constants.DELETING_FAILED);
            return "users/user-page";
        }
        log.info("User deleted");
        return "redirect:/auth/sign-in";
    }

    @GetMapping("/users/{username}/history")
    public String getMyOrders(@PathVariable(value = "username") String username, Model model) {
        User user = userService.getUserByUsername(username);
        Iterable<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        return "users/user-history";
    }

    @GetMapping("/users/{username}/history/{id}")
    public String getMyOrder(@PathVariable(value = "username") String username,
                             @PathVariable(value = "id") Integer id, Model model) {
        User user = userService.getUserByUsername(username);
        Order order = orderService.getOrder(id);
        model.addAttribute("user", user);
        model.addAttribute("order", order);
        return "users/order-detail";
    }
}
