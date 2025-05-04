package com.algaecare.controller;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.algaecare.model.GameState;

import java.util.logging.Level;

public class KeyboardInputController implements GameStateEventManager {
    private static final Logger LOGGER = Logger.getLogger(KeyboardInputController.class.getName());
    private final Stage stage;
    private final Map<KeyCode, Consumer<KeyEvent>> keyHandlers;
    private final GameStateEventManager.EventEmitter stateEmitter;
    private GameState currentState;

    public KeyboardInputController(Stage stage, GameStateEventManager.EventEmitter stateEmitter) {
        this.stage = stage;
        this.stateEmitter = stateEmitter;
        this.keyHandlers = new EnumMap<>(KeyCode.class);
        initializeKeyboardHandling();
        initializeGameBindings();
    }

    @Override
    public GameState getCurrentState() {
        throw new UnsupportedOperationException("KeyboardController doesn't manage game state");
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        LOGGER.info(String.format("Keyboard state updated from %s to %s", oldState, newState));
        this.currentState = newState;
    }

    private void initializeGameBindings() {
        // Title screen control
        bindKey(KeyCode.SPACE, event -> {
            if (currentState == GameState.TITLE) {
                stateEmitter.emitGameStateChange(GameState.OPENING);
            }
        });

        // Global controls
        bindKey(KeyCode.ESCAPE, event -> {
            stateEmitter.emitGameStateChange(GameState.TITLE);
        });

        // Gameplay controls
        GameState[] gameplayStates = {
                GameState.TRASH, GameState.CAR, GameState.AIRPLANE,
                GameState.SHOPPING_BAG_WORLD, GameState.RECYCLING, GameState.TRAIN,
                GameState.SHOPPING_BAG_LOCAL, GameState.BICYCLE, GameState.TRASH_GRABBER
        };

        KeyCode[] digitKeys = {
                KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3,
                KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6,
                KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9
        };

        for (int i = 0; i < Math.min(digitKeys.length, gameplayStates.length); i++) {
            final int index = i;
            final GameState targetState = gameplayStates[index];

            bindKey(digitKeys[index], event -> {
                LOGGER.info("Key " + digitKeys[index] + " pressed in state " + currentState);
                if (currentState == GameState.GAMEPLAY || currentState == GameState.OPENING) {
                    stateEmitter.emitGameStateChange(targetState);
                } else {
                    LOGGER.warning(String.format("Key %s pressed but current state is %s, expected GAMEPLAY or OPENING",
                            digitKeys[index], currentState));
                }
            });
        }

        // Development shortcuts
        bindKey(KeyCode.ENTER, event -> {
            if (currentState == GameState.OPENING) {
                stateEmitter.emitGameStateChange(GameState.GAMEPLAY);
            }
        });
    }

    private void initializeKeyboardHandling() {
        if (stage.getScene() != null) {
            attachKeyHandlers(stage.getScene());
        }

        stage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene != null) {
                detachKeyHandlers(oldScene);
            }
            if (newScene != null) {
                attachKeyHandlers(newScene);
            }
        });
    }

    private void attachKeyHandlers(Scene scene) {
        scene.setOnKeyPressed(this::handleKeyEvent);
    }

    private void detachKeyHandlers(Scene scene) {
        scene.setOnKeyPressed(null);
    }

    private void handleKeyEvent(KeyEvent event) {
        try {
            Consumer<KeyEvent> handler = keyHandlers.get(event.getCode());
            if (handler != null) {
                handler.accept(event);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error handling key event: " + event.getCode(), e);
        }
    }

    public void bindKey(KeyCode key, Consumer<KeyEvent> action) {
        keyHandlers.put(key, action);
    }
}