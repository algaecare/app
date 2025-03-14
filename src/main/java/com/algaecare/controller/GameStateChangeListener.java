package com.algaecare.controller;

import com.algaecare.model.GameState;

public interface GameStateChangeListener {
    void onGameStateChanged(GameState oldState, GameState newState);
}