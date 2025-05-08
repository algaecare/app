package com.algaecare.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Environment {
    private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());

    private int algaeLevel; // in percentage (0-100)

    private int timer;
    private boolean timerStarted = true;

    private final int TIMER_DURATION = 130;

    private List<EnvironmentObject> environmentObjects = new ArrayList<>();

    public Environment(int algaeLevel) {
        this.algaeLevel = algaeLevel;

        this.environmentObjects = initializeEnvironmentObjects();
    }

    private List<EnvironmentObject> initializeEnvironmentObjects() {
        // https://fhnw-projecttrack.atlassian.net/wiki/spaces/IP1224vt3/pages/594149391/Alltagsobjekte
        // Bad Objects
        environmentObjects.add(new EnvironmentObject("Trash", -10));
        environmentObjects.add(new EnvironmentObject("Car", -5));
        environmentObjects.add(new EnvironmentObject("Airplane", -20));
        environmentObjects.add(new EnvironmentObject("Shopping Bag World", -15));
        // Neutral Objects
        environmentObjects.add(new EnvironmentObject("Recycling", 5));
        environmentObjects.add(new EnvironmentObject("Train", 5));
        environmentObjects.add(new EnvironmentObject("Shopping Bag Local", 5));
        environmentObjects.add(new EnvironmentObject("Bicycle", 10));
        // Good Objects
        environmentObjects.add(new EnvironmentObject("Trash Grabber", 10));
        return environmentObjects;
    }

    public void updateEnvironment(EnvironmentObject environmentObject) {
        if (!timerStarted) {
            timerStarted = true;
            startTimer();
        }
        LOGGER.info("Updating environment with object: " + environmentObject.getName());

        // Calculate and apply algae change based on temperature change
        int algaeChange = environmentObject.getAlgaeChange();
        this.algaeLevel = algaeLevel + algaeChange;

        LOGGER.info(String.format(
                "Environment updated: CO2 Level: %d%%, Temperature: %d°C, Algae Level: %d%%, O2 Level: %d%%",
                algaeLevel));
    }

    public void reset() {
        this.algaeLevel = 0;

        LOGGER.info("Environment reset to default values.");
    }

    public int getAlgaeLevel() {
        return algaeLevel;
    }

    public List<EnvironmentObject> getEnvironmentObjects() {
        return environmentObjects;
    }

    public void startTimer() {
        timer = 0;
        new Thread(() -> {
            while (timer <= TIMER_DURATION) {
                System.out.println("Timer: " + timer + " Sekunden");
                try {
                    Thread.sleep(1000); // 1 Sekunde warten
                } catch (InterruptedException e) {
                    System.out.println("Timer wurde unterbrochen.");
                    return;
                }
                timer++;
            }
            System.out.println("Timer abgelaufen.");
        }).start();
    }

    public boolean isBelowZero() {
        return algaeLevel <= 0;
    }

    public boolean finishGame() {
        return (isBelowZero() && timer <= 130 || timer <= 130);
    }
}