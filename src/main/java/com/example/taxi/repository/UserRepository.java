package com.example.taxi.repository;

import com.example.taxi.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
    User findByPhoneNumber(String phoneNumber);
}
