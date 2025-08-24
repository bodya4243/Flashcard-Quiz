package com.example.demo.service;

import com.example.demo.model.Card;
import com.example.demo.model.Deck;
import com.example.demo.model.SessionContext;
import com.example.demo.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Scanner;

@RequiredArgsConstructor
@Service
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final SessionContext context;
    Scanner scanner = new Scanner(System.in);

    public Card createCard(Deck deck) {
        Card card = new Card();

        System.out.println("enter card name: ");
        String name = scanner.nextLine();

        System.out.println("enter question: ");
        String question = scanner.nextLine();

        System.out.println("enter answer: ");
        String answer = scanner.nextLine();

        card.setName(name);
        card.setQuestion(question);
        card.setAnswer(answer);
        card.setDeck(deck);

        cardRepository.save(card);

        return card;
    }

    public void deleteCard(Deck deck) {
        System.out.println("Enter the card name to delete it:");
        String name = scanner.nextLine();

        Optional<Card> card = cardRepository.findByNameAndDeck(name, deck);

        if (card.isPresent()) {
            cardRepository.delete(card.get());
            System.out.println("Card \"" + name + "\" has been deleted");
        } else {
            System.out.println("Card \"" + name + "\" not found");
        }
    }

    @Override
    public String updateCard(Deck deck) {
        System.out.println("Enter the card name to update it:");
        String name = scanner.nextLine();

        Card cardToUpdate = cardRepository.findByNameAndDeck(name, deck).orElseThrow(
                () -> new RuntimeException("card: " + name + "doesn't exist")
        );

        System.out.println("Enter a new card name to update it:");
        String newName = scanner.nextLine();

        cardToUpdate.setId(cardToUpdate.getId());
        cardToUpdate.setName(newName);

        System.out.println("Enter a new question: ");
        String newQuestion = scanner.nextLine();
        cardToUpdate.setQuestion(newQuestion);

        System.out.println("Enter a new answer: ");
        String newAnswer = scanner.nextLine();
        cardToUpdate.setAnswer(newAnswer);

        cardRepository.save(cardToUpdate);

        return "card with the name: " + newName + " has been changed";
    }
}
