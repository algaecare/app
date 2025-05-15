package com.algaecare.controller.output;

import com.algaecare.controller.GameStateEventManager;
import com.algaecare.model.Environment;
import com.algaecare.model.GameState;
import com.pi4j.context.Context;

public class LedController implements GameStateEventManager {
    private static final int[] PIN_LEDS = {14, 15, 18, 23, 24, 25};
    private static final int PIN_LEDS_LEVER = 11;
    private Environment environment;
    private Context pi4j;
    private com.pi4j.io.gpio.digital.DigitalOutput[] ledOutputs;
    private com.pi4j.io.gpio.digital.DigitalOutput leverLed;

    public LedController(Environment environment, Context pi4j) {
        this.environment = environment;
        this.pi4j = pi4j;
        leverLed = pi4j.digitalOutput().create(PIN_LEDS_LEVER);
        updateLeverLed(true);
        ledOutputs = new com.pi4j.io.gpio.digital.DigitalOutput[PIN_LEDS.length];
        for (int i = 0; i < PIN_LEDS.length; i++) {
            int ledPin = PIN_LEDS[i];
            var led = pi4j.digitalOutput().create(ledPin);
            ledOutputs[i] = led;
            updateLed(i, false);
        }

        for (int i = 0; i < PIN_LEDS.length; i++) {
            updateLed(i, true);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) { }
            updateLed(i, false);
        }
    }

    public void updateLed(int ledPin, boolean on) {
        com.pi4j.io.gpio.digital.DigitalOutput led = ledOutputs[ledPin];
        if (on) {
            led.high();
        } else {
            led.low();
        }
    }

    public void updateLeverLed(boolean on) {
        if (on) {
            leverLed.high();
        } else {
            leverLed.low();
        }
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        int level = environment.getAlgaeLevel();

        int thresholdIndex = (((100 - level) * 6) / 100);

        for (int i = 0; i < PIN_LEDS.length; i++) {
            if (i <= thresholdIndex) {
                updateLed(i, true);
            } else {
                updateLed(i, false);
            }
        }
    }

    @Override
    public GameState getCurrentState() {
        return null;
    }
}
