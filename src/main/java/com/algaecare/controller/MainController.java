package com.algaecare.controller;

import javafx.stage.Stage;
import com.algaecare.model.Environment;
import com.algaecare.model.GameState;

public class MainController extends GameStateEventManager.Notifier {
    private final Environment environment;

    public MainController(Stage stage) {
        // Initialize model
        this.environment = new Environment(90, 16, 70, 85);

        // Initialize controllers
        KeyboardInputController keyboardInputController = new KeyboardInputController(stage);
        ScreenController screenController = new ScreenController(stage);
        NFCChipController nfcController = new NFCChipController();

        // Wire up event chain:
        // Keyboard -> MainController -> ScreenController
        keyboardInputController.addStateEmitter(this); // Keyboard emits to Main
        addGameStateChangeListener(screenController); // Main notifies Screen

        // Set initial state
        currentState = GameState.TITLE;
        notifyGameStateChanged(null, currentState);
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        if (newState == GameState.TITLE) {
            environment.reset();
        }
    }
}