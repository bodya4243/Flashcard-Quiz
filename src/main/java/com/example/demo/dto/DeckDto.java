package com.example.demo.dto;

import java.util.List;

public record DeckDto(Long id, String name, List<CardDto> cards) {}