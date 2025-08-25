package com.example.demo.console;

import com.example.demo.model.Menu;
import com.example.demo.model.SessionContext;
import com.example.demo.model.User;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.DeckRepository;
import com.example.demo.repository.MenuRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.DeckService;
import com.example.demo.service.MenuService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class MyConsoleTest {

    @Mock
    private UserService userService;
    @Mock
    private DeckRepository deckRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private SessionContext context;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DeckService deckService;
    @Mock
    private MenuService menuService;

    @InjectMocks
    private MyConsole myConsole;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processRegister_ShouldSaveUserAndSetContext() throws Exception {
        User user = new User();
        user.setName("TestUser");
        user.setEmail("test@mail.com");
        user.setPassword("pass");
        user.setMenu(new Menu());

        when(userService.addUser(any(User.class))).thenReturn(user);

        myConsole.scanner = new Scanner("TestUser\ntest@mail.com\npass\n");

        Method method = MyConsole.class.getDeclaredMethod("processRegister");
        method.setAccessible(true);
        method.invoke(myConsole);

        verify(userService, times(1)).addUser(any(User.class));
        verify(context, times(1)).setCurrentUser(user);
        verify(context, times(1)).setCurrentMenu(user.getMenu());
    }

    @Test
    void processLogin_ShouldLoginAndSetContext() throws Exception {
        User user = new User();
        user.setName("LoggedUser");
        Menu menu = new Menu();
        user.setMenu(menu);

        when(userService.login("mail@mail.com", "123")).thenReturn(user);
        when(menuRepository.findByIdWithDecks(any())).thenReturn(menu);

        myConsole.scanner = new Scanner("mail@mail.com\n123\n");

        Method method = MyConsole.class.getDeclaredMethod("processLogin");
        method.setAccessible(true);
        method.invoke(myConsole);

        verify(userService, times(1)).login("mail@mail.com", "123");
        verify(context, times(1)).setCurrentUser(user);
        verify(context, times(1)).setCurrentMenu(menu);
    }
}
