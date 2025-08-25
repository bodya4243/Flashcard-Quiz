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
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Scanner;

@RequiredArgsConstructor
@Component
public class MyConsole implements CommandLineRunner {

    private final UserService userService;
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final SessionContext context;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final DeckService deckService;
    private final MenuService menuService;

    Scanner scanner = new Scanner(System.in);

    @Override
    public void run(String... args) throws IOException {
        while (true) {
            System.out.println("write \"register\" or \"login\"");
            System.out.println("you can always quit by writing \"stop\"");

            String command = scanner.nextLine().trim();

            if ("register".equalsIgnoreCase(command)) {
                processRegister();
            } else if ("login".equalsIgnoreCase(command)) {
                processLogin();
            }

            menuService.processState(context.getCurrentMenu());

            System.out.println("type stop to exit");

            if ("stop".equalsIgnoreCase(command)) {
                System.out.println("Bye!");
                break;
            }
        }
    }

    private void processRegister() {
        System.out.println("write your name:");
        String name = scanner.nextLine();

        System.out.println("write your email:");
        String email = scanner.nextLine();

        System.out.println("write your password:");
        String password = scanner.nextLine();

        User userToSave = new User();
        Menu menuToSave = new Menu();

        userToSave.setMenu(menuToSave);
        userToSave.setName(name);
        userToSave.setEmail(email);
        userToSave.setPassword(password);

        User savedUser = userService.addUser(userToSave);
        context.setCurrentUser(savedUser);
        context.setCurrentMenu(savedUser.getMenu());

        System.out.println("hello: " + savedUser.getName());
    }

    private void processLogin() {
        while (true) {
            System.out.println("write your email:");
            String email = scanner.nextLine();

            System.out.println("write your password:");
            String password = scanner.nextLine();

            try {
                User loggedUser = userService.login(email, password);
                System.out.println("hello: " + loggedUser.getName());

                context.setCurrentUser(loggedUser);
                Menu menu = menuRepository.findByIdWithDecks(loggedUser.getMenu().getId());
                context.setCurrentMenu(menu);

                return;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("try again (or type 'exit' to cancel)");

                String command = scanner.nextLine();
                if ("exit".equalsIgnoreCase(command)) {
                    return;
                }
            }
        }
    }
}
