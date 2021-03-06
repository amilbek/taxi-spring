package com.example.taxi.controller;

import com.example.taxi.constants.Constants;
import com.example.taxi.entity.Car;
import com.example.taxi.entity.User;
import com.example.taxi.models.CarRequest;
import com.example.taxi.services.CarService;
import com.example.taxi.services.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@Log
@Controller
public class CarController {

    private final UserService userService;
    private final CarService carService;

    @Autowired
    public CarController(UserService userService, CarService carService) {
        this.userService = userService;
        this.carService = carService;
    }

    @GetMapping("/users/{username}/car")
    public String getCar(@PathVariable(value = "username") String username, Model model) {
        User user = userService.getUserByUsername(username);
        Car car = carService.getCarByUser(user.getId().intValue());
        if (car != null) {
            model.addAttribute("failed", Constants.CAR_NOT_FOUND);
            model.addAttribute("car", car);
            return "redirect:/users/{username}/car-page";
        } else {
            model.addAttribute("driver", user);
            return "redirect:/users/{username}/add";
        }
    }

    @GetMapping("/users/{username}/car-page")
    public String getCarPage(@PathVariable(value = "username") String username, Model model) {
        User user = userService.getUserByUsername(username);
        Car car = carService.getCarByUser(user.getId().intValue());
        model.addAttribute("driver", user);
        model.addAttribute("car", car);
        return "cars/car-page";
    }

    @GetMapping("/users/{username}/add")
    public String getCarAdding(@PathVariable(value = "username") String username, Model model) {
        User user = userService.getUserByUsername(username);
        model.addAttribute("driver", user);
        return "cars/add-car";
    }

    @PostMapping("/users/{username}/car-add")
    public String createCar(@PathVariable(value = "username") String username,
                            @RequestParam String carNumber, @RequestParam String carModel,
                            @RequestParam String carColor, Model model) {
        User user = userService.getUserByUsername(username);
        CarRequest carRequest = new CarRequest(carNumber, carModel, carColor);
        boolean result = carService.saveCar(carRequest, username);
        if (!result) {
            carService.deleteCarByCarNumber(carNumber);
            model.addAttribute("failed", Constants.ADDING_CAR_FAILED);
            return "cars/add-car";
        }
        log.info("Car added");
        Car car = carService.getCarByCarNumber(carNumber);
        log.info(car.toString());
        model.addAttribute("driver", user);
        model.addAttribute("car", car);
        return "redirect:/users/{username}/car-page";
    }

    @GetMapping("/users/{username}/{id}/edit-car")
    public String editCar(@PathVariable(value = "username") String username,
                          @PathVariable(value = "id") Integer id, Model model) {
        User user = userService.getUserByUsername(username);
        Car car = carService.getCarById(id);
        model.addAttribute("driver", user);
        model.addAttribute("car", car);
        return "cars/edit-car";
    }

    @PostMapping("/users/{username}/{id}/car-edit")
    public String updateCar(@PathVariable(value = "username") String username,
                            @PathVariable(value = "id") Integer id,
                            @RequestParam String carNumber,
                            @RequestParam String carModel,
                            @RequestParam String carColor,
                            Model model) {
        CarRequest carRequest = new CarRequest(id, carNumber, carModel, carColor);
        log.info("ID: " + id);
        boolean result = carService.updateCar(carRequest);
        if (!result) {
            model.addAttribute("failed", Constants.SOMETHING_WRONG);
            return "redirect:/users/{username}/{id}/edit-car";
        }
        Car car = carService.getCarById(id);
        User user = userService.getUserByUsername(username);
        model.addAttribute("driver", user);
        model.addAttribute("car", car);
        return "redirect:/users/{username}/car-page";
    }

    @PostMapping("/users/{username}/{id}/delete")
    public String deleteCarById(@PathVariable(value = "username") String username,
                                @PathVariable(value = "id") Integer id,
                                Model model) {
        boolean result = carService.deleteCarById(id);
        if (!result) {
            Car car = carService.getCarById(id);
            model.addAttribute("failed", Constants.SOMETHING_WRONG);
            model.addAttribute("car", car);
            return "cars/car-page";
        }
        User user = userService.getUserByUsername(username);
        model.addAttribute("driver", user);
        return "redirect:/users/{username}/driver-page";
    }
}
