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
import org.springframework.web.bind.annotation.*;

@Log
@Controller
@RequestMapping("/admin")
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

    @GetMapping("/{username}")
    public String getAdminPage(@PathVariable(value = "username") String username, Model model) {
        User admin = userService.getUserByUsername(username);
        if (admin == null) {
            log.info("Nothing about admin");
            return "auth/user-login";
        }
        log.info(admin.toString());
        model.addAttribute("user", admin);
        return "admin/admin";
    }

    @PostMapping("/driver-status/{id}")
    public String changeDriverStatus(@PathVariable(value = "id") Integer driverId,
                                     Model model) {
        DriverStatusRequest driverStatusRequest = new DriverStatusRequest(driverId, true);
        boolean result = userService.changeStatus(driverStatusRequest);
        User user = userService.getUser(driverId);
        if (!result) {
            model.addAttribute("fail", Constants.SOMETHING_WRONG);
            model.addAttribute("driver", user);
            return "admin/driver-details";
        }
        model.addAttribute("driver", user);
        return "admin/driver-details";
    }

    @PostMapping("/car-tariff/{id}")
        public String changeTariff(@PathVariable(value = "id") Integer carId,
                                          @RequestParam String carTariff, Model model) {
        CarTariffRequest carTariffRequest = new CarTariffRequest(carId, carTariff);
        boolean result = carService.changeTariff(carTariffRequest);
        if (!result) {
            Car car = carService.getCarById(carId);
            model.addAttribute("fail", Constants.SOMETHING_WRONG);
            model.addAttribute("car", car);
            return "redirect:/admin/all-cars";
        }
        Car car = carService.getCarById(carId);
        model.addAttribute("car", car);
        return "redirect:/admin/all-cars";
    }

    @GetMapping("/all-cars/{id}/change-tariff")
    public String changeCarTariff(@PathVariable(value = "id") Integer id, Model model) {
        Car car = carService.getCarById(id);
        if (car == null) {
            return "redirect:/admin/all-car";
        }
        model.addAttribute("car", car);
        return "admin/change-tariff";
    }

    @PostMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable(value = "id") Integer id, Model model) {
        boolean result1 = orderService.deleteOrderByUser(id);
        boolean result2 = userService.deleteUser(id);
        if (!result1 || !result2) {
            User user = userService.getUser(id);
            model.addAttribute("user", user);
            return "/main/resources/templates/users/user-page.html";
        }
        Iterable<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/all-users";
    }

    @PostMapping("/delete-driver/{id}")
    public String deleteDriver(@PathVariable(value = "id") Integer id, Model model) {
        boolean result1 = carService.deleteCarByDriver(id);
        boolean result2 = orderService.deleteOrderByDriver(id);
        boolean result3 = userService.deleteUser(id);
        if (!result1 || !result2 || !result3) {
            User user = userService.getUser(id);
            model.addAttribute("driver", user);
            return "admin/driver-details";
        }
        Iterable<User> drivers = userService.getAllDrivers();
        model.addAttribute("drivers", drivers);
        return "redirect:/admin/all-drivers";
    }

    @GetMapping("/all-users")
    public String getAllUsers(Model model) {
        Iterable<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/all-users";
    }

    @GetMapping("/all-drivers")
    public String getAllDrivers(Model model) {
        Iterable<User> users = userService.getAllDrivers();
        model.addAttribute("drivers", users);
        return "admin/all-drivers";
    }

    @GetMapping("/all-cars")
    public String getAllCars(Model model) {
        Iterable<Car> cars = carService.getAllCars();
        model.addAttribute("cars", cars);
        return "admin/all-cars";
    }

    @GetMapping("/all-orders")
    public String getAllOrders(Model model) {
        Iterable<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/all-orders";
    }

    @GetMapping("/all-users/{id}")
    public String getUser(@PathVariable(value ="id") Integer id, Model model) {
        User user = userService.getUser(id);
        if (user == null) {
            return "redirect:/admin/all-users";
        }
        model.addAttribute("user", user);
        return "admin/user-details";
    }

    @GetMapping("/all-drivers/{id}")
    public String getDriver(@PathVariable(value ="id") Integer id, Model model) {
        User user = userService.getUser(id);
        if (user == null) {
            return "redirect:/admin/all-drivers";
        }
        model.addAttribute("user", user);
        return "admin/user-details";
    }

    @GetMapping("/all-cars/{id}")
    public String getCar(@PathVariable(value ="id") Integer id, Model model) {
        Car car = carService.getCarById(id);
        if (car == null) {
            return "redirect:/admin/all-car";
        }
        model.addAttribute("car", car);
        return "admin/car-details";
    }

    @GetMapping("/all-orders/{id}")
    public String getOrder(@PathVariable(value ="id") Integer id, Model model) {
        Order order = orderService.getOrder(id);
        if (order == null) {
            return "redirect:/admin/all-users";
        }
        model.addAttribute("order", order);
        return "orders/order-page";
    }
}