package com.example.demo.io;

import com.example.demo.dto.DeckDto;
import com.example.demo.model.Deck;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonUtil {

    private final String filePath;
    private final ObjectMapper mapper;

    public JsonUtil(@Value("${deck.json.path}") String filePath) {
        this.filePath = filePath;
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveDecksToJsonFile(List<DeckDto> decks) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        mapper.writeValue(file, decks);
    }

    public List<Deck> loadDecksFromJsonFile() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        return mapper.readValue(file, new TypeReference<>() {});
    }
}
