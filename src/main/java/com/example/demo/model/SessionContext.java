package com.example.demo.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class SessionContext {
    private User currentUser;
    private Menu currentMenu;
}