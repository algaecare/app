package com.algaecare.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Environment {
    private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());
    private int algaeLevel; // in percentage (0-100)
    private int timer;
    private boolean timerStarted = false;
    private final int TIMER_DURATION = 130;
    private List<EnvironmentObject> environmentObjects = new ArrayList<>();

    public Environment(int algaeLevel) {
        this.algaeLevel = algaeLevel;
        this.environmentObjects = initializeEnvironmentObjects();
    }

    private List<EnvironmentObject> initializeEnvironmentObjects() {
        environmentObjects.add(new EnvironmentObject(GameState.OBJECT_GARBAGE_BAG, -10));
        environmentObjects.add(new EnvironmentObject(GameState.OBJECT_CAR, -5));
        environmentObjects.add(new EnvironmentObject(GameState.OBJECT_AIRPLANE, -20));
        environmentObjects.add(new EnvironmentObject(GameState.OBJECT_SHOPPING_BASKET_INTERNATIONAL, -15));
        environmentObjects.add(new EnvironmentObject(GameState.OBJECT_RECYCLING_BIN, 5));
        environmentObjects.add(new EnvironmentObject(GameState.OBJECT_TRAIN, 5));
        environmentObjects.add(new EnvironmentObject(GameState.OBJECT_SHOPPING_BASKET_LOCAL, 5));
        environmentObjects.add(new EnvironmentObject(GameState.OBJECT_BICYCLE, 10));
        environmentObjects.add(new EnvironmentObject(GameState.OBJECT_TRASH_GRABBER, 10));
        return environmentObjects;
    }

    public void updateEnvironment(GameState environmentObjectID) {
        if (!timerStarted) {
            timerStarted = true;
            startTimer();
        }

        EnvironmentObject environmentObject = environmentObjects.stream()
                .filter(obj -> obj.getObjectID() == environmentObjectID)
                .findFirst()
                .orElse(null);
        if (environmentObject == null) {
            LOGGER.warning("Umweltobjekt nicht gefunden: " + environmentObjectID);
            return;
        } else {
            int newAlgaeLevel = algaeLevel + environmentObject.getAlgaeChange();
            if (newAlgaeLevel < 0) {
                newAlgaeLevel = 0;
            } else if (newAlgaeLevel > 100) {
                newAlgaeLevel = 100;
            }
            this.algaeLevel = newAlgaeLevel;
            LOGGER.info("Algenlevel: " + algaeLevel + "%");
        }
    }

    public void reset() {
        this.algaeLevel = 0;
        this.timer = 0;
        this.timerStarted = false;
    }

    public int getAlgaeLevel() {
        return algaeLevel;
    }

    public List<EnvironmentObject> getEnvironmentObjects() {
        return environmentObjects;
    }

    public boolean isBelowZero() {
        return algaeLevel <= 0;
    }

    public boolean finishGame() {
        return (isBelowZero() && timer <= 130 || timer <= 130);
    }

    public void startTimer() {
        timer = 0;
        Thread timerThread = new Thread(() -> {
            while (timer < TIMER_DURATION) {
                LOGGER.info("Timer: " + timer + " Sekunden");
                try {
                    Thread.sleep(1000); // 1 Sekunde warten
                } catch (InterruptedException e) {
                    LOGGER.warning("Timer wurde unterbrochen.");
                    Thread.currentThread().interrupt();
                    return;
                }
                timer++;
            }
            LOGGER.info("Timer abgelaufen.");
            timerStarted = false;
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }
}