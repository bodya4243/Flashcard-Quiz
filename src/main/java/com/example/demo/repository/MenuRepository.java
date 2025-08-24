package com.example.demo.repository;

import com.example.demo.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Optional<Menu> findMenuByUserId(Long id);

    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.decks WHERE m.id = :id")
    Menu findByIdWithDecks(@Param("id") Long id);
}
