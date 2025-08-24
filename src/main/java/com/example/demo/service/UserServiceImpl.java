package com.example.demo.service;

import com.example.demo.model.Menu;
import com.example.demo.model.SessionContext;
import com.example.demo.model.User;
import com.example.demo.repository.MenuRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SessionContext context;

    @Override
    public User addUser(User user) {
        boolean isPresent = userRepository.findByEmail(user.getEmail()).isPresent();

        if (isPresent) {
            throw new RuntimeException("user already exists");
        }

        Menu menu = user.getMenu();
        if (menu != null) {
            menu.setUser(user);
            user.setMenu(menu);
        }

        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("no user has been found")
        );

        if (!Objects.equals(user.getPassword(), password)) {
            throw new RuntimeException("password is not match");
        }

        return user;
    }
}
