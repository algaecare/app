package com.algaecare.controller;

import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import com.algaecare.model.Environment;
import com.algaecare.model.GameState;

public class MainController implements GameStateEventManager, GameStateEventManager.EventEmitter {
    private final List<GameStateEventManager> listeners = new ArrayList<>();
    private GameState currentState;
    private final Environment environment;

    public MainController(Stage stage) {
        // Initialize model
        this.environment = new Environment(90, 16, 70, 85);

        // Initialize controllers
        KeyboardInputController keyboardInputController = new KeyboardInputController(stage, this);
        ScreenController screenController = new ScreenController(stage, this);
        NFCChipController nfcController = new NFCChipController();

        // Wire up event chain
        addGameStateChangeListener(screenController);
        addGameStateChangeListener(keyboardInputController);

        // Set initial state
        currentState = GameState.TITLE;
        notifyGameStateChanged(null, currentState);
    }

    public void addGameStateChangeListener(GameStateEventManager listener) {
        listeners.add(listener);
    }

    @Override
    public void emitGameStateChange(GameState newState) {
        GameState oldState = currentState;
        currentState = newState;
        notifyGameStateChanged(oldState, newState);
    }

    @Override
    public GameState getCurrentState() {
        return currentState;
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        if (newState == GameState.TITLE) {
            environment.reset();
        }
    }

    protected void notifyGameStateChanged(GameState oldState, GameState newState) {
        listeners.forEach(listener -> listener.onGameStateChanged(oldState, newState));
    }
}