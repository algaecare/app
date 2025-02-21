package com.algaecare.controller;

import com.algaecare.model.GameState;

public class MainController {
    private final GameState gameState;

    public MainController() {
        this.gameState = new GameState();
    }

    public void startGame() {
        gameState.setGameRunning(true);
        // Add game initialization logic here
    }

    public boolean isGameRunning() {
        return gameState.isGameRunning();
    }
}