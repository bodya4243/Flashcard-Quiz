package com.example.demo.service;

import com.example.demo.model.Card;
import com.example.demo.model.Deck;

public interface CardService {
    Card createCard(Deck deck);

    void deleteCard(Deck deck);

    String updateCard(Deck deck);
}
