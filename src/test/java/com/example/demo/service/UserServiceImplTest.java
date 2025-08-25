package com.example.demo.service;

import com.example.demo.model.Menu;
import com.example.demo.model.SessionContext;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private SessionContext context;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        context = mock(SessionContext.class);
        userService = new UserServiceImpl(userRepository, context);
    }

    @Test
    void addUser_success() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("1234");

        Menu menu = new Menu();
        user.setMenu(menu);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User savedUser = userService.addUser(user);

        assertEquals("test@mail.com", savedUser.getEmail());
        assertEquals(menu, savedUser.getMenu());
        assertEquals(user, menu.getUser());

        verify(userRepository).save(user);
    }

    @Test
    void addUser_alreadyExists_throwsException() {
        User user = new User();
        user.setEmail("duplicate@mail.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.addUser(user));

        assertEquals("user already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_success() {
        User user = new User();
        user.setEmail("login@mail.com");
        user.setPassword("secret");

        when(userRepository.findByEmail("login@mail.com")).thenReturn(Optional.of(user));

        User loggedUser = userService.login("login@mail.com", "secret");

        assertEquals("login@mail.com", loggedUser.getEmail());
    }

    @Test
    void login_userNotFound_throwsException() {
        when(userRepository.findByEmail("notfound@mail.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.login("notfound@mail.com", "123"));

        assertEquals("no user has been found", ex.getMessage());
    }

    @Test
    void login_wrongPassword_throwsException() {
        User user = new User();
        user.setEmail("login@mail.com");
        user.setPassword("correct");

        when(userRepository.findByEmail("login@mail.com")).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.login("login@mail.com", "wrong"));

        assertEquals("password is not match", ex.getMessage());
    }
}
