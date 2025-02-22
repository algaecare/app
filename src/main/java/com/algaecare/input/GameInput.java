package com.algaecare.input;

import javafx.scene.input.KeyCode;

import com.algaecare.view.Window;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.PullResistance;
import java.util.logging.Logger;
import java.util.logging.Level;

public class GameInput {
    private static final Logger LOGGER = Logger.getLogger(GameInput.class.getName());
    private static final int BUTTON_PIN = 24;

    private final Window window;
    private Context pi4j;
    private DigitalInput button;
    protected Runnable callback;
    private boolean isPi4JInitialized;

    public GameInput(Window window) {
        this.window = window;
        initializeInputs();
    }

    private void initializeInputs() {
        // Always initialize keyboard input
        initializeKeyboard();

        // Try to initialize Pi4J if available
        if (isPi4JAvailable()) {
            try {
                initializePi4J();
                isPi4JInitialized = true;
                LOGGER.info("Pi4J input initialized");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not initialize Pi4J, falling back to keyboard only");
                isPi4JInitialized = false;
            }
        }
    }

    private void initializeKeyboard() {
        window.getMyScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE && callback != null) {
                callback.run();
            }
        });
        LOGGER.info("Keyboard input initialized");
    }

    private void initializePi4J() {
        pi4j = Pi4J.newAutoContext();
        button = pi4j.create(DigitalInput.newConfigBuilder(pi4j)
                .id("gameButton")
                .name("Game Input Button")
                .address(BUTTON_PIN)
                .pull(PullResistance.PULL_UP)
                .build());

        button.addListener(e -> {
            if (e.state().isLow() && callback != null) {
                callback.run();
            }
        });
    }

    private boolean isPi4JAvailable() {
        return System.getProperty("os.name").toLowerCase().contains("linux")
                && System.getProperty("os.arch").toLowerCase().contains("arm");
    }

    public void setInputCallback(Runnable callback) {
        this.callback = callback;
    }

    public void cleanup() {
        window.getMyScene().setOnKeyPressed(null);
        if (isPi4JInitialized && pi4j != null) {
            pi4j.shutdown();
        }
    }
}