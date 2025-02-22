package com.algaecare.input;

import com.algaecare.view.Window;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class KeyboardInput extends GameInput {
    Window window;

    public KeyboardInput(Window window) {
        super(window);
    }

    public void initialize() {
        // No initialization needed for keyboard input
    }

    public void setInputCallback(Runnable callback) {
        window.getMyScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                callback.run();
            }
        });
    }

    public void cleanup() {
        // Remove event handler
        if (window.getMyScene() != null) {
            window.getMyScene().setOnKeyPressed(null);
        }
    }
}