package com.example.demo.service;

import com.example.demo.model.Deck;
import com.example.demo.model.Menu;
import com.example.demo.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;


public interface MenuService {
    String saveMenuToJson(Menu menu) throws JsonProcessingException, IOException;
}
