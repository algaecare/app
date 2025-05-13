package com.algaecare.controller;

import com.algaecare.model.Environment;
import com.algaecare.model.GameState;
import com.pi4j.context.Context;

public class LedController implements GameStateEventManager {
    private static final int[] PIN_LEDS = { 11, 14, 15, 18, 23, 24 };
    private Environment environment;
    private Context pi4j;

    public LedController(Environment environment, Context pi4j) {
        this.environment = environment;
        this.pi4j = pi4j;
    }

    public void updateLed(int ledPin, boolean on) {
        var led = pi4j.digitalOutput().create(ledPin);

        if (on) {
            led.high();
        } else {
            led.low();
        }
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        int level = environment.getAlgaeLevel();

        int thresholdIndex = Math.max(0, Math.round((float) level / (float) 100 * (float) 6));

        for (int i = 0; i < PIN_LEDS.length; i++) {
            if (i <= thresholdIndex) {
                updateLed(PIN_LEDS[i], true);
            } else {
                updateLed(PIN_LEDS[i], false);
            }
        }
    }

    @Override
    public GameState getCurrentState() {
        return null;
    }
}
