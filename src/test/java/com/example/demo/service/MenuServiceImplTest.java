package com.example.demo.service;

import com.example.demo.io.JsonUtil;
import com.example.demo.model.Card;
import com.example.demo.model.Deck;
import com.example.demo.model.Menu;
import com.example.demo.model.SessionContext;
import com.example.demo.model.User;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.DeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class MenuServiceImplTest {

    private DeckService deckService;
    private DeckRepository deckRepository;
    private SessionContext context;
    private CardRepository cardRepository;
    private JsonUtil jsonUtil;
    private MenuServiceImpl menuService;

    @BeforeEach
    void setUp() {
        deckService = mock(DeckService.class);
        deckRepository = mock(DeckRepository.class);
        context = mock(SessionContext.class);
        cardRepository = mock(CardRepository.class);
        jsonUtil = mock(JsonUtil.class);
    }

    @Test
    void testProcessState_exitImmediately() throws IOException {
        String input = "exit\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        menuService = new MenuServiceImpl(deckService, deckRepository, context, cardRepository, jsonUtil, scanner);

        Menu menu = new Menu();
        menuService.processState(menu);

        verifyNoInteractions(deckService, deckRepository, cardRepository, jsonUtil);
    }

    @Test
    void testProcessState_updateWithEmptyDecks_createsDeck() throws IOException {
        String input = "update\ngetJson\nexit\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        menuService = new MenuServiceImpl(deckService, deckRepository, context, cardRepository, jsonUtil, scanner);

        Menu menu = new Menu();
        menu.setDecks(List.of());

        when(jsonUtil.loadDecksFromJsonFile()).thenReturn(List.of());

        menuService.processState(menu);

        verify(deckService).processDeckCreating(menu);
        verify(deckService).processDeckUpdate(anyList());
    }

    @Test
    void testProcessLearn_answerCorrectly() {
        String input = "deck1\n\ncard1\ncorrectAnswer\nstop\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        menuService = new MenuServiceImpl(deckService, deckRepository, context, cardRepository, jsonUtil, scanner);

        User user = new User();
        when(context.getCurrentUser()).thenReturn(user);

        Card card = new Card();
        card.setName("card1");
        card.setQuestion("Q?");
        card.setAnswer("correctAnswer");

        Deck deck = new Deck();
        deck.setName("deck1");
        deck.setCards(List.of(card));

        when(deckRepository.findByNameAndMenuUserWithCards("deck1", user))
                .thenReturn(Optional.of(deck));
        when(cardRepository.findByNameAndDeck("card1", deck))
                .thenReturn(Optional.of(card));

        menuService.processLearn(List.of(deck));

        verify(deckRepository).findByNameAndMenuUserWithCards("deck1", user);
        verify(cardRepository).findByNameAndDeck("card1", deck);
    }

    @Test
    void testSaveMenuToJson_createsFile() throws IOException {
        Scanner scanner = new Scanner(new ByteArrayInputStream("exit\n".getBytes()));
        menuService = new MenuServiceImpl(deckService, deckRepository, context, cardRepository, jsonUtil, scanner);

        Menu menu = new Menu();
        File file = new File("menu.json");
        if (file.exists()) {
            file.delete();
        }

        menuService.saveMenuToJson(menu);

        assert file.exists();
        file.delete();
    }
}
