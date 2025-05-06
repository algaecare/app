package com.algaecare.controller;
import com.algaecare.model.Environment;
import com.algaecare.model.EnvironmentObject;
import com.algaecare.model.GameState;
import com.pi4j.*;

import java.util.logging.Level;

public class LedController implements GameStateEventManager{
    private static final int[] PIN_LEDS = {4, 17, 27, 22, 23, 24, 25, 29, 31}; //Pins BCM 7, 11, 13, 15, 16, 18, 22, 5, 6
    private Environment environment = null;
    public LedController(Environment environment) {
        this.environment = environment;
    }
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
        int co2Level = environment.getCo2Level();
        int temperature = environment.getTemperature();
        int o2Level = environment.getO2Level();

        // Calculate LED threshold based on CO2 level
        int thresholdIndex = Math.max(0, Math.min(PIN_LEDS.length - 1, Math.round((float) (co2Level + 50) / 100 * 8)));

        for (int i = 0; i < PIN_LEDS.length; i++) {
            if (i <= thresholdIndex && temperature > -50 && temperature <= 50) {
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
