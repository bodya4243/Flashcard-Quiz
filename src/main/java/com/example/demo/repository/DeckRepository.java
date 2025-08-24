package com.example.demo.repository;

import com.example.demo.model.Deck;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {

    @Query("SELECT d FROM Deck d LEFT JOIN FETCH d.cards WHERE d.name = :name AND d.menu.user = :user")
    Optional<Deck> findByNameAndMenuUserWithCards(@Param("name") String name, @Param("user") User user);
}
