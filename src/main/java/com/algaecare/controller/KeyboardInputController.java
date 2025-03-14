package com.algaecare.controller;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.logging.Level;

public class KeyboardInputController {
    private static final Logger LOGGER = Logger.getLogger(KeyboardInputController.class.getName());
    private final Stage stage;
    private final Map<KeyCode, Consumer<KeyEvent>> keyHandlers;

    public KeyboardInputController(Stage stage) {
        this.stage = stage;
        this.keyHandlers = new EnumMap<>(KeyCode.class);
        initializeKeyboardHandling();
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