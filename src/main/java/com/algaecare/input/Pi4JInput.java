package com.algaecare.input;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.PullResistance;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.algaecare.view.Window;

public class Pi4JInput extends GameInput {
    private static final Logger LOGGER = Logger.getLogger(Pi4JInput.class.getName());
    private static final int BUTTON_PIN = 24;
    private Context pi4j;
    private DigitalInput button;

    public Pi4JInput(Window window) {
        super(window);
        initializePi4J();
    }

    private void initializePi4J() {
        try {
            pi4j = Pi4J.newAutoContext();
            button = pi4j.create(DigitalInput.newConfigBuilder(pi4j)
                    .id("gameButton")
                    .name("Game Input Button")
                    .address(BUTTON_PIN)
                    .pull(PullResistance.PULL_UP)
                    .build());

            button.addListener(e -> {
                if (e.state().isLow() && getCallback() != null) {
                    getCallback().run();
                }
            });
            LOGGER.info("Pi4J input initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize Pi4J input", e);
            throw new RuntimeException("Pi4J initialization failed", e);
        }
    }

    private Runnable getCallback() {
        return callback;
    }

    @Override
    public void cleanup() {
        super.cleanup(); // Clean up keyboard input
        if (pi4j != null) {
            pi4j.shutdown();
            LOGGER.info("Pi4J input cleaned up");
        }
    }
}