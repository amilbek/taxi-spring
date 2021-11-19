package com.example.taxi.controller;

import com.example.taxi.constants.Constants;
import com.example.taxi.entity.User;
import com.example.taxi.models.UserRequest;
import com.example.taxi.security.jwt.JwtTokenProvider;
import com.example.taxi.services.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@Log
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                              JwtTokenProvider jwtTokenProvider,
                              UserService userService,
                              PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/register")
    public String register(@RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String phoneNumber,
                           @RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        UserRequest userRequest = new UserRequest(firstName, lastName, phoneNumber, username, password);
        userRequest.setPassword(passwordEncoder.encode(password));
        boolean result = userService.saveUser(userRequest);
        if (!result) {
            model.addAttribute("failed", Constants.REGISTRATION_FAILED);
            return "auth/user-register";    
        }
        model.addAttribute("succeed", Constants.REGISTRATION_SUCCEED);
        return "auth/user-login";
    }

    @PostMapping("/auth/login")
    public String authenticate(@RequestParam String username, @RequestParam String password,
                               Model model) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            User user = userService.getUserByUsername(username);
            String token = jwtTokenProvider.createToken(username, user.getRole());
            Map<Object, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("token", token);

            log.info("username: " + response.get("username") + "\n" +
                     "token: " + response.get("token"));
            String greeting = Constants.LOGIN_SUCCEED + " " + username;
            model.addAttribute("greeting", greeting);
            model.addAttribute("user", user);
            if (username.equals("admin")) {
                return "redirect:/admin/" + username;
            }
            return "redirect:/users/" + username;
        } catch (AuthenticationException e) {
            model.addAttribute("failed", Constants.LOGIN_FAILED);
            return "auth/user-login";
        }
    }

    @GetMapping("/auth/logout")
    public String logout(Model model) {
        model.addAttribute("logout", Constants.LOGOUT_SUCCEED);
        return "redirect:/auth/sign-in";
    }


    @GetMapping("/auth/sign-in")
    public String login() {
        return "auth/user-login";
    }

    @GetMapping("/auth/signup")
    public String register() {
        return "auth/user-register";
    }


}
