package com.example.taxi.services;

import com.example.taxi.constants.Constants;
import com.example.taxi.entity.Role;
import com.example.taxi.entity.User;
import com.example.taxi.helpers.PasswordValidationHelper;
import com.example.taxi.helpers.ValidateHelper;
import com.example.taxi.models.DriverStatusRequest;
import com.example.taxi.models.UserRequest;
import com.example.taxi.repository.RoleRepository;
import com.example.taxi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public boolean saveUser(UserRequest userRequest) {
        if (!ValidateHelper.validatePhoneNumber(userRequest.getPhoneNumber())) {
            return false;
        }

        User byPhoneNumber = userRepository.findByPhoneNumber(userRequest.getPhoneNumber());
        User byUsername = userRepository.findByUsername(userRequest.getUsername());
        if (byUsername != null || byPhoneNumber != null) {
            return false;
        }

        if (!PasswordValidationHelper.passwordValidation(userRequest.getPassword())) {
            return false;
        }
        User user = new User(userRequest.getFirstName(), userRequest.getLastName(),
                userRequest.getPhoneNumber(), userRequest.getUsername(), userRequest.getPassword());
        Role roleAdmin = roleRepository.findByName(Constants.ROLE_USER);
        user.setRole(roleAdmin);
        user.setStatus(roleAdmin.getStatus());
        userRepository.save(user);
        return true;
    }
    
    public User getUser(Integer id) {
        return userRepository.findById(id.longValue()).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean updateUser(UserRequest userRequest) {
        if (!ValidateHelper.validatePhoneNumber(userRequest.getPhoneNumber())) {
            return false;
        }
        Optional<User> userOptional = userRepository.findById(userRequest.getId().longValue());
        User user = userOptional.orElse(null);
        assert user != null;
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setUsername(userRequest.getUsername());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        userRepository.save(user);
        return true;
    }

    public boolean deleteUser(Integer id) {
        Optional<User> userOptional = userRepository.findById(id.longValue());
        User user = userOptional.orElse(null);
        assert user != null;
        userRepository.delete(user);
        return true;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public boolean becomeDriver(Integer id, UserRequest userRequest) {
        Optional<User> userOptional = userRepository.findById(id.longValue());
        User user = userOptional.orElse(null);
        assert user != null;
        user.setIsDriver(true);
        user.setDriverIdNumber(userRequest.getDriverIdNumber());
        user.setDriverLicenseNumber(userRequest.getDriverLicenseNumber());
        user.setLicenseExpDate(userRequest.getLicenseExpDate());
        userRepository.save(user);
        return true;
    }

    public boolean changeStatus(DriverStatusRequest driverStatusRequest) {
        Optional<User> userOptional =
                userRepository.findById(driverStatusRequest.getDriverId().longValue());
        User user = userOptional.orElse(null);
        if (user == null) {
            return false;
        }
        user.setIsAvailable(driverStatusRequest.getDriverStatus());
        userRepository.save(user);
        return true;
    }

    public List<User> getAllDrivers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            if (Boolean.TRUE.equals(user.getIsDriver())) {
                users.add(user);
            }
        });
        return users;
    }
}