package com.example.demo.service;

import com.example.demo.model.Deck;
import com.example.demo.model.Menu;

import java.util.List;

public interface DeckService {
    void processDeckUpdate(List<Deck> decks);
    void getDeckUpdateMenu();
    void deleteDeck(Deck deck);
    void processDeckCreating(Menu menu);
}
