package com.algaecare.controller;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import com.algaecare.model.Environment;
import com.algaecare.model.GameState;

public class MainController {
    private final ScreenController screenController;
    private final KeyboardInputController keyboardInputController;
    private final GameState initialGameState = GameState.TITLE;
    private final Environment environment;
    private GameState gameState;
    private final List<GameStateChangeListener> listeners = new ArrayList<>();

    // Constructor
    public MainController(Stage stage) {
        this.gameState = initialGameState;

        this.screenController = new ScreenController(this, stage);
        addGameStateChangeListener(this.screenController);

        this.keyboardInputController = new KeyboardInputController(stage);
        initializeKeyboardBindings();

        this.environment = new Environment(90, 16, 70, 85);
    }

    // GameState management
    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState newState) {
        GameState oldState = this.gameState;
        this.gameState = newState;
        notifyGameStateChanged(oldState, newState);
    }

    public void addGameStateChangeListener(GameStateChangeListener listener) {
        listeners.add(listener);
    }

    public void removeGameStateChangeListener(GameStateChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyGameStateChanged(GameState oldState, GameState newState) {
        listeners.forEach(listener -> listener.onGameStateChanged(oldState, newState));
    }

    // Keyboard Input
    private void initializeKeyboardBindings() {
        keyboardInputController.bindKey(KeyCode.SPACE, event -> {
            if (gameState == GameState.TITLE) {
                setGameState(GameState.OPENING);
            }
        });
        keyboardInputController.bindKey(KeyCode.ESCAPE, event -> {
            setGameState(GameState.ENDING);
        });
        keyboardInputController.bindKey(KeyCode.DIGIT1, event -> {
            if (gameState == GameState.GAMEPLAY) {

            }
        });
    }
}