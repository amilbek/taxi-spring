package com.example.taxi.services;

import com.example.taxi.constants.Constants;
import com.example.taxi.entity.Role;
import com.example.taxi.enums.Status;
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
    private final ValidateHelper validateHelper;
    private final PasswordValidationHelper passwordValidationHelper;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       ValidateHelper validateHelper,
                       PasswordValidationHelper passwordValidationHelper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.validateHelper = validateHelper;
        this.passwordValidationHelper = passwordValidationHelper;
    }

    public boolean saveUser(UserRequest userRequest) {
        if (!validateHelper.validatePhoneNumber(userRequest.getPhoneNumber())) {
            return false;
        }

        if (!passwordValidationHelper.passwordValidation(userRequest.getPassword())) {
            return false;
        }

        User byPhoneNumber = userRepository.findByPhoneNumber(userRequest.getPhoneNumber());
        User byUsername = userRepository.findByUsername(userRequest.getUsername());
        if (byUsername != null && byUsername.getUsername().equals(userRequest.getUsername()) &&
                byUsername.getPhoneNumber().equals(userRequest.getPhoneNumber()) &&
                byUsername.getStatus().equals(Status.BANNED)) {
            byUsername.setStatus(Status.ACTIVE);
            userRepository.save(byUsername);
            return true;
        }

        if (byPhoneNumber != null && byPhoneNumber.getUsername().equals(userRequest.getUsername()) &&
                byPhoneNumber.getPhoneNumber().equals(userRequest.getPhoneNumber()) &&
                byPhoneNumber.getStatus().equals(Status.BANNED)) {
            byPhoneNumber.setStatus(Status.ACTIVE);
            userRepository.save(byPhoneNumber);
            return true;
        }

        if (byUsername != null || byPhoneNumber != null) {
            return false;
        }

        User user = new User(userRequest.getFirstName(), userRequest.getLastName(),
                userRequest.getPhoneNumber(), userRequest.getUsername(), userRequest.getPassword());
        Role roleUser = roleRepository.findByName(Constants.ROLE_USER);
        user.setRole(roleUser);
        user.setStatus(Status.ACTIVE);
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
        if (!validateHelper.validatePhoneNumber(userRequest.getPhoneNumber())) {
            return false;
        }
        User user = userRepository.findById(userRequest.getId().longValue()).orElse(null);
        assert user != null;
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setUsername(userRequest.getUsername());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        userRepository.save(user);
        return true;
    }

    public boolean deleteUser(User user) {
        user.setStatus(Status.BANNED);
        userRepository.save(user);
        return true;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            if (user.getStatus().equals(Status.ACTIVE)) {
                users.add(user);
            }
        });
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

    public boolean activeDriver(DriverStatusRequest driverStatusRequest) {
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

    public boolean nonActiveDriver(DriverStatusRequest driverStatusRequest) {
        Optional<User> userOptional =
                userRepository.findById(driverStatusRequest.getDriverId().longValue());
        User user = userOptional.orElse(null);
        if (user == null) {
            return false;
        }
        user.setIsAvailable(driverStatusRequest.getDriverStatus());
        user.setIsDriver(false);
        user.setDriverIdNumber(null);
        user.setDriverLicenseNumber(null);
        user.setLicenseExpDate(null);
        userRepository.save(user);
        return true;
    }

    public List<User> getAllDrivers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            if (Boolean.TRUE.equals(user.getIsDriver()) && user.getStatus().equals(Status.ACTIVE)) {
                users.add(user);
            }
        });
        return users;
    }
}