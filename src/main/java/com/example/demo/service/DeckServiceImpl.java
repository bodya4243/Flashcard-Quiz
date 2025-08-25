package com.example.demo.service;

import com.example.demo.dto.DeckDto;
import com.example.demo.io.JsonUtil;
import com.example.demo.maper.DeckMapper;
import com.example.demo.model.Card;
import com.example.demo.model.Deck;
import com.example.demo.model.Menu;
import com.example.demo.model.SessionContext;
import com.example.demo.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
@Service
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;
    private final CardService cardService;
    private final SessionContext context;
    private final JsonUtil jsonUtil;
    Scanner scanner = new Scanner(System.in);

    public void processDeckUpdate(List<Deck> decks) {

        while (true) {
            System.out.println("All present decks: ");
            decks.forEach(deck -> System.out.println(deck.getName()));

            System.out.println("Options:");
            System.out.println(" - get deck by name to update: write \"get\"");
            System.out.println(" - go to main menu: write \"main\"");

            String command = scanner.nextLine().trim();

            if ("get".equalsIgnoreCase(command)) {
                getDeckUpdateMenu();
            } else if ("main".equalsIgnoreCase(command)) {
                break;
            } else {
                System.out.println("Unknown command, try again.");
            }
        }
    }

    public void getDeckUpdateMenu() {
        while (true) {
            try {
                System.out.println("enter deck 'name' or 'exit' to quit: ");

                String name = scanner.nextLine();

                if ("exit".equalsIgnoreCase(name)) {
                    break;
                }

                Deck deck;
                try {
                    deck = deckRepository.findByNameAndMenuUserWithCards(name, context.getCurrentUser())
                            .orElseThrow(() -> new RuntimeException("Deck not found!"));
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                    continue;
                }

                System.out.println("you can save this deck to local json file -> write 'save'");
                String saveCommand = scanner.nextLine();

                if ("save".equalsIgnoreCase(saveCommand)) {
                    DeckDto dto = DeckMapper.toDto(deck);
                    jsonUtil.saveDecksToJsonFile(List.of(dto));
                }

                while (true) {
                    System.out.println("deck cards: ");
                    deck.getCards().forEach(card -> System.out.println(card.toString()));

                    System.out.println("you can delete this deck or add/delete/update card in this deck");
                    System.out.println("write for this: delete_deck / add_card / delete_card / update_card / back");

                    String command = scanner.nextLine().trim();

                    if ("delete_deck".equalsIgnoreCase(command)) {
                        deleteDeck(deck);
                        System.out.println("Deck deleted.");
                        break;
                    } else if ("add_card".equalsIgnoreCase(command)) {
                        cardService.createCard(deck);
                    } else if ("delete_card".equalsIgnoreCase(command)) {
                        System.out.println("all the present cards: ");
                        deck.getCards().forEach(System.out::println);
                        cardService.deleteCard(deck);
                    } else if ("update_card".equalsIgnoreCase(command)) {
                        System.out.println("all the present cards: ");
                        deck.getCards().forEach(System.out::println);
                        System.out.println(cardService.updateCard(deck));
                    } else if ("back".equalsIgnoreCase(command)) {
                        System.out.println("Returning to deck selection...");
                        break;
                    } else {
                        System.out.println("Unknown command, try again.");
                    }
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Do you want to try again? (yes/no)");

                String retry = scanner.nextLine().trim();
                if (!retry.equalsIgnoreCase("yes")) {
                    break;
                }
            }
        }
    }

    public void processDeckCreating(Menu menu) {
        Deck deck = new Deck();
        System.out.println("deck creating: ");
        System.out.println("enter deck name: ");
        String name = scanner.nextLine();

        deck.setName(name);
        deck.setMenu(menu);

        deck = deckRepository.save(deck);

        System.out.println("you should create a card to save the deck");

        Card card = cardService.createCard(deck);
        deck.getCards().add(card);

        deckRepository.save(deck);
    }

    public void deleteDeck(Deck deck) {
        if (deck != null) {
            deckRepository.delete(deck);
            System.out.println("Deck \"" + deck.getName() + "\" deleted.");
        } else {
            System.out.println("Deck not found.");
        }
    }
}
