package com.algaecare.controller;

import com.algaecare.model.GameState;

public interface GameStateEventManager {
    void onGameStateChanged(GameState oldState, GameState newState);

    GameState getCurrentState();

    interface EventEmitter {
        void emitGameStateChange(GameState newState);
    }
}