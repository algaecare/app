package com.algaecare.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class KeyboardInput implements GameInput {
    private Scene scene;

    public KeyboardInput(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void initialize() {
        // No initialization needed for keyboard input
    }

    @Override
    public void setInputCallback(Runnable callback) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                callback.run();
            }
        });
    }

    @Override
    public void cleanup() {
        // Remove event handler
        if (scene != null) {
            scene.setOnKeyPressed(null);
        }
    }
}