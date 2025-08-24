package com.example.demo.service;

import com.example.demo.model.Deck;
import com.example.demo.model.Menu;
import com.example.demo.model.User;
import com.example.demo.repository.DeckRepository;
import com.example.demo.repository.MenuRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;

    @Override
    public String saveMenuToJson(Menu menu) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.writeValue(new File("menu.json"), menu);

        return "menu was saved to json";
    }
}
