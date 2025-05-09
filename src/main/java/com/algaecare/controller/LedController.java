package com.algaecare.controller;
import com.algaecare.model.Environment;
import com.algaecare.model.EnvironmentObject;
import com.algaecare.model.GameState;
import com.pi4j.*;

import java.util.logging.Level;

public class LedController implements GameStateEventManager {
    private static final int[] PIN_LEDS = {4, 17, 27, 22, 23, 24, 25, 29, 31}; //Pins BCM 7, 11, 13, 15, 16, 18, 22, 5, 6
    private Environment environment = null;

    /**
     * Constructor for the LED controller
     *
     * @param environment The environment object to be used for getting the CO2 level
     */
    public LedController(Environment environment) {
        this.environment = environment;
    }

    /**
     * Updates the LED state based on the given pin and state
     *
     * @param ledPin The pin number of the LED
     * @param on     The state to set the LED to (true for ON, false for OFF)
     */
    public static void updateLed(int ledPin, boolean on) {
        var pi4j = Pi4J.newAutoContext();

        var led = pi4j.digitalOutput().create(ledPin);

        if(on) {
            led.high();
        } else {
            led.low();
        }
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        int level = environment.getAlgaeLevel();

        // Calculate LED threshold based on CO2 level
        int thresholdIndex = Math.max(0, Math.round((float) level / 100 * 6));

        for (int i = 0; i < PIN_LEDS.length; i++) {
            if (i <= thresholdIndex) {
                updateLed(PIN_LEDS[i], true);
            } else {
                updateLed(PIN_LEDS[i], false);
            }
        }
    }

    /**
     * Returns the current state of the game
     *
     * @return The current game state
     */
    @Override
    public GameState getCurrentState() {
        return null;
    }
}
