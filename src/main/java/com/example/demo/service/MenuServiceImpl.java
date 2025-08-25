package com.example.demo.service;

import com.example.demo.io.JsonUtil;
import com.example.demo.model.Card;
import com.example.demo.model.Deck;
import com.example.demo.model.Menu;
import com.example.demo.model.SessionContext;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.DeckRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
@Service
public class MenuServiceImpl implements MenuService {
    private final DeckService deckService;
    private final DeckRepository deckRepository;
    private final SessionContext context;
    private final CardRepository cardRepository;
    private final JsonUtil jsonUtil;
    private final Scanner scanner;

    public MenuServiceImpl(DeckService deckService,
                           DeckRepository deckRepository,
                           SessionContext context,
                           CardRepository cardRepository,
                           JsonUtil jsonUtil) {
        this(deckService, deckRepository, context, cardRepository, jsonUtil, new Scanner(System.in));
    }

    @Override
    public void processState(Menu menu) throws IOException {
        while (true) {
            System.out.println("choose the state of the program");
            System.out.println("update / learn / exit");

            String command = scanner.nextLine().trim();

            if ("update".equalsIgnoreCase(command)) {
                List<Deck> decks = menu.getDecks();

                if (decks.isEmpty()) {
                    System.out.println("there are no decks present");

                    System.out.println("write 'getJson' to get decks from local storage");

                    String jsonLoad = scanner.nextLine().trim();

                    if ("getJson".equalsIgnoreCase(jsonLoad)) {
                        decks = jsonUtil.loadDecksFromJsonFile();

                        if (decks.isEmpty()) {
                            System.out.println("create deck");
                            deckService.processDeckCreating(menu);
                        }
                    }
                }

                System.out.println("updating deck");
                deckService.processDeckUpdate(decks);
            } else if ("learn".equalsIgnoreCase(command)) {
                List<Deck> decks = menu.getDecks();

                if (decks.isEmpty()) {
                    System.out.println("there are no decks present. You can go to update mode");
                    break;
                }

                processLearn(decks);
            } else if ("exit".equalsIgnoreCase(command)) {
                break;
            } else {
                System.out.println("unknown command!");
            }
        }
    }

    @Override
    public void processLearn(List<Deck> decks) {
        int counter = 0;

        System.out.println("choose one of the deck by its name");

        System.out.println("enter deck name: ");
        String name = scanner.nextLine();

        Deck deck = deckRepository.findByNameAndMenuUserWithCards(name ,context.getCurrentUser())
                .orElseThrow(
                        () -> new RuntimeException("the deck: " + name + "not found")
                );

        System.out.println("cards in the deck: " + deck.getName());

        while (true) {
            deck.getCards().forEach(card -> System.out.println(card.getName()
                    + " question " + card.getQuestion()));

            System.out.println("press enter to continue or write (stop) to exit");
            String stopCommand = scanner.nextLine().trim();

            if ("stop".equalsIgnoreCase(stopCommand)) {
                break;
            }

            System.out.println("choose card to answer the question: ");
            String chosenCard = scanner.nextLine();

            Card card = cardRepository.findByNameAndDeck(chosenCard, deck).orElseThrow(
                    () -> new RuntimeException("card is not found")
            );

            counter = answerTheQuestion(card, counter);

            if (counter >= deck.getCards().size()) {
                System.out.println("you have already answered all questions good job.");
                break;
            }
        }

        System.out.println("your mark is: " + counter + "/" + deck.getCards().size());
    }

    @Override
    public String saveMenuToJson(Menu menu) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.writeValue(new File("menu.json"), menu);

        return "menu was saved to json";
    }

    private int answerTheQuestion(Card card, int counter) {
        System.out.println(card.getQuestion());
        String answer = scanner.nextLine();

        if (answer.equals(card.getAnswer())) {
            System.out.println("correct adding +1 to your karma");
            counter ++;
        }

        System.out.println("correct answer is: " + card.getAnswer());

        return counter;
    }
}
