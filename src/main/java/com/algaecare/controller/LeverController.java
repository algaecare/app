package com.algaecare.controller;

import com.pi4j.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LeverController {
    private final List<LeverInputListener> listeners = new ArrayList<>();

    public void addListener(LeverInputListener listener) {
        listeners.add(listener);
    }

    public LeverController() {
        var pi4j = Pi4J.newAutoContext();
        var button = pi4j.digitalOutput().create(16);
        Thread asyncThread = new Thread(() -> {
            while (true) {
                boolean buttonPressed = button.isHigh();

                if (buttonPressed) {
                    for (LeverInputListener listener : listeners) {
                        listener.onLeverInput();
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("LeverController listening loop interrupted; resuming.");
                }
            }
        });

        asyncThread.setDaemon(true);
        asyncThread.start();
    }
}
