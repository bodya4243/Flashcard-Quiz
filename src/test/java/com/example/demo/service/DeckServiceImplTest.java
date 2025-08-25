package com.example.demo.service;

import com.example.demo.dto.DeckDto;
import com.example.demo.io.JsonUtil;
import com.example.demo.maper.DeckMapper;
import com.example.demo.model.Card;
import com.example.demo.model.Deck;
import com.example.demo.model.Menu;
import com.example.demo.model.SessionContext;
import com.example.demo.model.User;
import com.example.demo.repository.DeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DeckServiceImplTest {

    private DeckRepository deckRepository;
    private CardService cardService;
    private SessionContext context;
    private JsonUtil jsonUtil;
    private DeckServiceImpl deckService;

    @BeforeEach
    void setUp() {
        deckRepository = mock(DeckRepository.class);
        cardService = mock(CardService.class);
        context = mock(SessionContext.class);
        jsonUtil = mock(JsonUtil.class);

        deckService = new DeckServiceImpl(deckRepository, cardService, context, jsonUtil);
    }

    @Test
    void processDeckCreating_ShouldSaveDeckWithCard() {
        Menu menu = new Menu();
        Deck deck = new Deck();
        deck.setId(1L);
        deck.setName("deck1");
        deck.setCards(new ArrayList<>());

        when(deckRepository.save(any(Deck.class))).thenReturn(deck);

        Card card = new Card();
        card.setName("c1");
        when(cardService.createCard(deck)).thenReturn(card);

        deckService.scanner = new Scanner("deck1\n");

        deckService.processDeckCreating(menu);

        verify(deckRepository, times(2)).save(any(Deck.class));
        assertEquals("deck1", deck.getName());
        assertTrue(deck.getCards().contains(card));
    }

    @Test
    void deleteDeck_ShouldDeleteIfExists() {
        Deck deck = new Deck();
        deck.setName("deckToDelete");

        deckService.deleteDeck(deck);

        verify(deckRepository, times(1)).delete(deck);
    }

    @Test
    void deleteDeck_ShouldNotDeleteIfNull() {
        deckService.deleteDeck(null);

        verify(deckRepository, never()).delete(any());
    }

    @Test
    void getDeckUpdateMenu_ShouldSaveDeckToJsonWhenSaveCommand() throws IOException {
        User user = new User();
        Deck deck = new Deck();
        deck.setName("testDeck");
        deck.setCards(Collections.emptyList());

        when(context.getCurrentUser()).thenReturn(user);
        when(deckRepository.findByNameAndMenuUserWithCards("testDeck", user)).thenReturn(Optional.of(deck));

        // Scanner: name -> save -> back
        String input = "testDeck\nsave\nback\nexit\n";
        deckService.scanner = new Scanner(input);

        deckService.getDeckUpdateMenu();

        verify(jsonUtil, times(1)).saveDecksToJsonFile(anyList());
    }
}
