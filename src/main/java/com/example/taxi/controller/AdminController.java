package com.example.taxi.controller;

import com.example.taxi.constants.Constants;
import com.example.taxi.entity.Car;
import com.example.taxi.entity.Order;
import com.example.taxi.entity.User;
import com.example.taxi.models.CarTariffRequest;
import com.example.taxi.models.DriverStatusRequest;
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
public class AdminController {
    private final UserService userService;
    private final CarService carService;
    private final OrderService orderService;

    @Autowired
    public AdminController(UserService userService,
                           CarService carService,
                           OrderService orderService) {
        this.userService = userService;
        this.carService = carService;
        this.orderService = orderService;
    }

    @GetMapping("/admin/{username}")
    public String getAdminPage(@PathVariable(value = "username") String username, Model model) {
        User admin = userService.getUserByUsername(username);
        if (admin == null) {
            log.info("Nothing about admin");
            return "auth/user-login";
        }
        log.info(admin.toString());
        model.addAttribute("admin", admin);
        return "admin/admin";
    }

    @PostMapping("/admin/driver-status/{id}")
    public String changeDriverStatus(@PathVariable(value = "id") Integer driverId,
                                     Model model) {
        DriverStatusRequest driverStatusRequest = new DriverStatusRequest(driverId, true);
        boolean result = userService.changeStatus(driverStatusRequest);
        User user = userService.getUser(driverId);
        if (!result) {
            model.addAttribute("failed", Constants.SOMETHING_WRONG);
            model.addAttribute("driver", user);
            return "redirect:/admin/all-drivers/{id}";
        }
        model.addAttribute("driver", user);
        return "redirect:/admin/all-drivers/{id}";
    }

    @PostMapping("/admin/{id}/car-tariff/{carId}")
    public String changeTariff(@PathVariable(value = "id") Integer id,
                               @PathVariable(value = "carId") Integer carId,
                               @RequestParam String carTariff, Model model) {
        CarTariffRequest carTariffRequest = new CarTariffRequest(carId, carTariff);
        boolean result = carService.changeTariff(carTariffRequest);
        if (!result) {
            Car car = carService.getCarById(carId);
            model.addAttribute("failed", Constants.SOMETHING_WRONG);
            model.addAttribute("car", car);
            return "redirect:/admin/all-cars/{id}";
        }
        User user = userService.getUser(id);
        Car car = carService.getCarById(carId);
        model.addAttribute("user", user);
        model.addAttribute("car", car);
        return "redirect:/admin/all-drivers/{id}";
    }

    @GetMapping("/admin/{username}/{id}/change-tariff")
    public String changeCarTariff(@PathVariable(value = "username") String username,
                                  @PathVariable(value = "id") Integer id, Model model) {
        Car car = carService.getCarById(id);
        User admin = userService.getUserByUsername("admin");
        model.addAttribute("admin", admin);
        if (car == null) {
            return "redirect:/admin/all-drivers/{id}";
        }
        User user = userService.getUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("car", car);
        return "admin/change-tariff";
    }

    @PostMapping("/admin/delete-user/{id}")
    public String deleteUser(@PathVariable(value = "id") Integer id, Model model) {
        User user = userService.getUser(id);
        boolean result1 = carService.deleteCarByUser(user);
        boolean result2 = orderService.deleteOrdersByUser(user);
        boolean result3 = userService.deleteUser(user);
        if (!result1 || !result2 || !result3) {
            model.addAttribute("failed", Constants.SOMETHING_WRONG);
            return "redirect:/admin/all-users/{id}";
        }
        Iterable<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "redirect:/admin/all-users";
    }

    @PostMapping("/admin/delete-driver/{id}")
    public String deleteDriver(@PathVariable(value = "id") Integer id, Model model) {
        boolean result1 = carService.deleteCarByDriver(id);
        DriverStatusRequest driverStatusRequest = new DriverStatusRequest(id, false);
        boolean result2 = userService.changeStatus(driverStatusRequest);
        if (!result1 || !result2) {
            model.addAttribute("failed", Constants.SOMETHING_WRONG);
            return "redirect:/admin/all-drivers/{id}";
        }
        Iterable<User> drivers = userService.getAllDrivers();
        model.addAttribute("drivers", drivers);
        return "redirect:/admin/all-drivers";
    }

    @GetMapping("/admin/all-users")
    public String getAllUsers(Model model) {
        Iterable<User> users = userService.getAllUsers();
        User admin = userService.getUserByUsername("admin");
        model.addAttribute("admin", admin);
        model.addAttribute("users", users);
        return "admin/all-users";
    }

    @GetMapping("/admin/all-drivers")
    public String getAllDrivers(Model model) {
        Iterable<User> users = userService.getAllDrivers();
        User admin = userService.getUserByUsername("admin");
        model.addAttribute("admin", admin);
        model.addAttribute("drivers", users);
        return "admin/all-drivers";
    }

    @GetMapping("/admin/all-orders")
    public String getAllOrders(Model model) {
        Iterable<Order> orders = orderService.getAllOrders();
        User admin = userService.getUserByUsername("admin");
        model.addAttribute("admin", admin);
        model.addAttribute("orders", orders);
        return "admin/all-orders";
    }

    @GetMapping("/admin/all-users/{id}")
    public String getUser(@PathVariable(value ="id") Integer id, Model model) {
        User user = userService.getUser(id);
        User admin = userService.getUserByUsername("admin");
        model.addAttribute("admin", admin);
        if (user == null) {
            Iterable<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "admin/all-users";
        }
        model.addAttribute("user", user);
        return "admin/user-details";
    }

    @GetMapping("/admin/all-drivers/{id}")
    public String getDriver(@PathVariable(value ="id") Integer id, Model model) {
        User user = userService.getUser(id);
        Car car = carService.getCarByUser(id);
        User admin = userService.getUserByUsername("admin");
        model.addAttribute("admin", admin);
        if (user == null) {
            Iterable<User> users = userService.getAllDrivers();
            model.addAttribute("drivers", users);
            return "admin/all-drivers";
        }
        model.addAttribute("user", user);
        model.addAttribute("car", car);
        return "admin/driver-details";
    }

    @GetMapping("/admin/all-orders/{id}")
    public String getOrder(@PathVariable(value ="id") Integer id, Model model) {
        Order order = orderService.getOrder(id);
        User admin = userService.getUserByUsername("admin");
        model.addAttribute("admin", admin);
        if (order == null) {
            Iterable<Order> orders = orderService.getAllOrders();
            model.addAttribute("orders", orders);
            return "admin/all-orders";
        }
        model.addAttribute("order", order);
        return "admin/order-details";
    }
}