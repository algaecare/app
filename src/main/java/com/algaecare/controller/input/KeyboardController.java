package com.algaecare.controller.input;

import javafx.scene.Scene;
import java.util.logging.Logger;

import com.algaecare.model.InputAction;

import java.util.function.Consumer;
import java.util.logging.Level;

public class KeyboardController implements InputController {
    private static final Logger LOGGER = Logger.getLogger(KeyboardController.class.getName());
    private final Scene scene;
    private Consumer<InputAction> callback;
    private boolean isInitialized;

    public KeyboardController(Scene scene) {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }
        this.scene = scene;
    }

    @Override
    public void initialize() {
        try {
            scene.setOnKeyPressed(event -> {
                InputAction action = InputAction.fromKeyCode(event.getCode());
                if (action != null) {
                    handleInput(action);
                }
            });
            isInitialized = true;
            LOGGER.info("Keyboard controller initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize keyboard controller", e);
            throw new RuntimeException("Keyboard initialization failed", e);
        }
    }

    private void handleInput(InputAction action) {
        if (callback != null) {
            try {
                callback.accept(action);
                LOGGER.info("Keyboard input processed: " + action.getDescription());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error processing keyboard input: " + action, e);
            }
        } else {
            LOGGER.warning("Keyboard input received but no callback set: " + action);
        }
    }

    @Override
    public void setInputCallback(Consumer<InputAction> callback) {
        this.callback = callback;
        LOGGER.info("Keyboard callback registered");
    }

    @Override
    public void cleanup() {
        if (isInitialized) {
            scene.setOnKeyPressed(null);
            isInitialized = false;
            LOGGER.info("Keyboard controller cleaned up");
        }
    }
}