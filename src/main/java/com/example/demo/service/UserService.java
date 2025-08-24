package com.example.demo.service;

import com.example.demo.model.User;

import java.util.Optional;

public interface UserService {
    User addUser(User user);

    User login(String name, String password);
}
