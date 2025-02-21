package com.algaecare.input;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.PullResistance;

public class Pi4JInput implements GameInput {
    private Context pi4j;
    private DigitalInput button;
    private static final int BUTTON_PIN = 24;
    private static final boolean IS_RASPBERRY_PI = System.getProperty("os.name").toLowerCase().contains("linux")
            && System.getProperty("os.arch").toLowerCase().contains("arm");

    @Override
    public void initialize() throws UnsupportedOperationException {
        if (!IS_RASPBERRY_PI) {
            throw new UnsupportedOperationException("Not running on Raspberry Pi");
        }

        pi4j = Pi4J.newAutoContext();
        button = pi4j.create(DigitalInput.newConfigBuilder(pi4j)
                .id("gameButton")
                .name("Game Input Button")
                .address(BUTTON_PIN)
                .pull(PullResistance.PULL_UP)
                .build());
    }

    @Override
    public void setInputCallback(Runnable callback) {
        button.addListener(e -> {
            if (e.state().isLow()) { // Button pressed (active low)
                callback.run();
            }
        });
    }

    @Override
    public void cleanup() {
        if (pi4j != null) {
            pi4j.shutdown();
        }
    }
}