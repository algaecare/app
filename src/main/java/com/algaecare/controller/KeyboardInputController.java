package com.algaecare.controller;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.algaecare.model.GameState;

import java.util.logging.Level;

public class KeyboardInputController implements GameStateEventManager {
    private static final Logger LOGGER = Logger.getLogger(KeyboardInputController.class.getName());
    private final Stage stage;
    private final Map<KeyCode, Consumer<KeyEvent>> keyHandlers;
    private final List<GameStateEventManager.EventEmitter> stateEmitters;
    private GameState currentGameState;

    public KeyboardInputController(Stage stage) {
        this.stage = stage;
        this.keyHandlers = new EnumMap<>(KeyCode.class);
        this.stateEmitters = new ArrayList<>();
        this.currentGameState = GameState.TITLE;
        initializeKeyboardHandling();
        initializeGameBindings();
    }

    public void addStateEmitter(GameStateEventManager.EventEmitter emitter) {
        stateEmitters.add(emitter);
    }

    private void emitGameStateChange(GameState newState) {
        stateEmitters.forEach(emitter -> emitter.emitGameStateChange(newState));
    }

    private void initializeGameBindings() {
        // Title screen controls
        bindKey(KeyCode.SPACE, event -> {
            if (currentGameState == GameState.TITLE) {
                emitGameStateChange(GameState.OPENING);
            }
        });

        // Global controls
        bindKey(KeyCode.ESCAPE, event -> {
            emitGameStateChange(GameState.TITLE);
        });

        // Gameplay controls
        KeyCode[] digitKeys = { KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4,
                KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8,
                KeyCode.DIGIT9 };
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        this.currentGameState = newState;
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

    public void unbindKey(KeyCode key) {
        keyHandlers.remove(key);
    }

    public void clearBindings() {
        keyHandlers.clear();
    }

    public void dispose() {
        if (stage.getScene() != null) {
            detachKeyHandlers(stage.getScene());
        }
        clearBindings();
    }
}