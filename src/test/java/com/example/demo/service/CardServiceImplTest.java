package com.example.demo.service;

import com.example.demo.model.Card;
import com.example.demo.model.Deck;
import com.example.demo.model.SessionContext;
import com.example.demo.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    private CardRepository cardRepository;
    private SessionContext context;
    private CardServiceImpl cardService;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        context = mock(SessionContext.class);
        cardService = new CardServiceImpl(cardRepository, context);
    }

    @Test
    void createCard_ShouldSaveAndReturnCard() {
        Deck deck = new Deck();

        String input = "Card1\nQuestion1\nAnswer1\n";
        cardService.scanner = new Scanner(input);

        Card result = cardService.createCard(deck);

        assertEquals("Card1", result.getName());
        assertEquals("Question1", result.getQuestion());
        assertEquals("Answer1", result.getAnswer());
        assertEquals(deck, result.getDeck());

        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void deleteCard_ShouldDeleteIfExists() {
        Deck deck = new Deck();
        Card card = new Card();
        card.setName("ToDelete");

        when(cardRepository.findByNameAndDeck("ToDelete", deck)).thenReturn(Optional.of(card));

        cardService.scanner = new Scanner("ToDelete\n");
        cardService.deleteCard(deck);

        verify(cardRepository, times(1)).delete(card);
    }

    @Test
    void deleteCard_ShouldPrintNotFoundIfMissing() {
        Deck deck = new Deck();
        when(cardRepository.findByNameAndDeck("Missing", deck)).thenReturn(Optional.empty());

        cardService.scanner = new Scanner("Missing\n");
        cardService.deleteCard(deck);

        verify(cardRepository, never()).delete(any(Card.class));
    }

    @Test
    void updateCard_ShouldUpdateAndSave() {
        Deck deck = new Deck();
        Card existing = new Card();
        existing.setId(1L);
        existing.setName("OldName");
        existing.setQuestion("OldQ");
        existing.setAnswer("OldA");

        when(cardRepository.findByNameAndDeck("OldName", deck)).thenReturn(Optional.of(existing));

        String input = "OldName\nNewName\nNewQ\nNewA\n";
        cardService.scanner = new Scanner(input);

        String result = cardService.updateCard(deck);

        assertEquals("card with the name: NewName has been changed", result);
        assertEquals("NewName", existing.getName());
        assertEquals("NewQ", existing.getQuestion());
        assertEquals("NewA", existing.getAnswer());

        verify(cardRepository, times(1)).save(existing);
    }

    @Test
    void updateCard_ShouldThrowIfCardNotFound() {
        Deck deck = new Deck();
        when(cardRepository.findByNameAndDeck("Unknown", deck)).thenReturn(Optional.empty());

        cardService.scanner = new Scanner("Unknown\n");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cardService.updateCard(deck));
        assertTrue(ex.getMessage().contains("doesn't exist"));

        verify(cardRepository, never()).save(any(Card.class));
    }
}
