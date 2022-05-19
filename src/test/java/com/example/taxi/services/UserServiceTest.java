package com.example.taxi.services;

import com.example.taxi.constants.Constants;
import com.example.taxi.entity.Role;
import com.example.taxi.entity.User;
import com.example.taxi.enums.Status;
import com.example.taxi.helpers.PasswordValidationHelper;
import com.example.taxi.helpers.ValidateHelper;
import com.example.taxi.models.UserRequest;
import com.example.taxi.repository.RoleRepository;
import com.example.taxi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService sut;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    ValidateHelper validateHelper;

    @Mock
    PasswordValidationHelper passwordValidationHelper;

    @Test
    void saveUser() {
        UserRequest userRequest = new UserRequest("firstName", "lastName",
                "87775536956", "username", "password");

        Role role = new Role();

        when(validateHelper.validatePhoneNumber(userRequest.getPhoneNumber())).thenReturn(true);
        when(passwordValidationHelper.passwordValidation(userRequest.getPassword())).thenReturn(true);

        when(userRepository.findByPhoneNumber(userRequest.getPhoneNumber())).thenReturn(null);
        when(userRepository.findByUsername(userRequest.getUsername())).thenReturn(null);

        when(roleRepository.findByName(Constants.ROLE_USER)).thenReturn(role);

        boolean result = sut.saveUser(userRequest);

        assertTrue(result);
    }

    @Test
    void getUser() {
        User expected = new User("firstName", "lastName",
                "87775536956", "username", "password");

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(expected));

        User actual = sut.getUser(1);

        assertEquals(expected, actual);
    }

    @Test
    void getUserByUsername() {
        User expected = new User("firstName", "lastName",
                "87775536956", "username", "password");

        when(userRepository.findByUsername("username")).thenReturn(expected);

        User actual = sut.getUserByUsername("username");

        assertEquals(expected, actual);
    }

    @Test
    void deleteUser() {
        User user = new User("firstName", "lastName",
                "87775536956", "username", "password");
        user.setStatus(Status.ACTIVE);

        boolean result = sut.deleteUser(user);

        assertTrue(result);
    }

    @Test
    void getAllUsers() {
        List<User> expected = new ArrayList<>();

        User user1 = new User("firstName", "lastName",
                "87775536956", "username", "password");
        user1.setStatus(Status.ACTIVE);


        expected.add(user1);


        when(userRepository.findAll()).thenReturn(expected);

        List<User> actual = sut.getAllUsers();

        assertEquals(expected, actual);
    }
}