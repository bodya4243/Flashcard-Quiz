package com.example.demo.maper;

import com.example.demo.dto.CardDto;
import com.example.demo.dto.DeckDto;
import com.example.demo.model.Card;
import com.example.demo.model.Deck;

import java.util.List;
import java.util.stream.Collectors;

public class DeckMapper {

    public static DeckDto toDto(Deck deck) {
        List<CardDto> cardDTOs = deck.getCards().stream()
                .map(DeckMapper::toCardDto)
                .collect(Collectors.toList());

        return new DeckDto(deck.getId(), deck.getName(), cardDTOs);
    }

    private static CardDto toCardDto(Card card) {
        return new CardDto(
                card.getId(),
                card.getName(),
                card.getQuestion(),
                card.getAnswer()
        );
    }
}