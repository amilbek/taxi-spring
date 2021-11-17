package com.example.taxi.controller;

import com.example.taxi.constants.Constants;
import com.example.taxi.entity.Order;
import com.example.taxi.entity.User;
import com.example.taxi.models.UserRequest;
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

import java.sql.Date;

@Log
@Controller
public class DriverController {

    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public DriverController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/users/{username}/driver")
    public String driver(@PathVariable(value = "username") String username, Model model) {
        User user = userService.getUserByUsername(username);
        if (Boolean.TRUE.equals(user.getIsDriver())) {
            model.addAttribute("driver", user);
            return "drivers/driver-page";
        } else {
            return "redirect:/users/{username}/driver-register";
        }
    }

    @GetMapping("/users/{username}/driver-register")
    public String driverBecome(@PathVariable(value = "username") String username,
                               Model model) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            log.info("Nothing about user");
            model.addAttribute("failed", Constants.USER_NOT_FOUND);
            return "users/user-page";
        }
        model.addAttribute("user", user);
        return "drivers/driver-register";
    }


    @PostMapping("/users/{username}/become-driver")
    public String becomeDriver(@PathVariable(value = "username") String username,
                               @RequestParam String driverIdNumber,
                               @RequestParam String driverLicenseNumber,
                               @RequestParam Date licenseExpDate,
                               Model model) {
        User user = userService.getUserByUsername(username);
        UserRequest userRequest = new UserRequest(Math.toIntExact(user.getId()), true,
                driverIdNumber, driverLicenseNumber, licenseExpDate);
        boolean result = userService.becomeDriver(Math.toIntExact(user.getId()), userRequest);
        if (!result) {
            log.info("User not become driver");
            model.addAttribute("user", user);
            return "redirect:/users/{username}/driver-page";
        }
        log.info("User became driver");
        model.addAttribute("succeed", Constants.REGISTRATION_SUCCEED);
        model.addAttribute("user", user);
        return "redirect:/users/{username}/driver-page";
    }

    @GetMapping("/users/{username}/driver-page")
    public String getDriverPage(@PathVariable(value = "username") String username, Model model) {
        User user= userService.getUserByUsername(username);
        model.addAttribute("driver", user);
        return "drivers/driver-page";
    }

    @GetMapping("/users/{username}/driver-edit")
    public String getDriverEdit(@PathVariable(value = "username") String username, Model model) {
        User user= userService.getUserByUsername(username);
        model.addAttribute("driver", user);
        return "drivers/driver-edit";
    }

    @PostMapping("/users/{username}/edit-driver")
    public String driverEdit(@PathVariable(value = "username") String username,
                             @RequestParam String driverIdNumber,
                             @RequestParam String driverLicenseNumber,
                             @RequestParam Date licenseExpDate,
                             Model model) {
        User user = userService.getUserByUsername(username);
        UserRequest userRequest = new UserRequest(Math.toIntExact(user.getId()), true,
                driverIdNumber, driverLicenseNumber, licenseExpDate);
        boolean result = userService.becomeDriver(Math.toIntExact(user.getId()), userRequest);
        if (!result) {
            model.addAttribute("failed", Constants.EDITING_FAILED);
            model.addAttribute("user", user);
            return "redirect:/users/{username}/driver-edit";
        }
        log.info(user.toString());
        log.info("User Updated");
        model.addAttribute("succeed", Constants.EDITING_SUCCEED);
        model.addAttribute("driver", user);
        return "redirect:/users/{username}/driver-page";
    }

    @GetMapping("/users/{username}/driver-history")
    public String getDriverHistoryPage(@PathVariable(value = "username") String username, Model model) {
        User driver = userService.getUserByUsername(username);
        Iterable<Order> orders = orderService.getOrdersByDriver(driver);
        model.addAttribute("user", driver);
        model.addAttribute("orders", orders);
        return "drivers/driver-history";
    }
}
