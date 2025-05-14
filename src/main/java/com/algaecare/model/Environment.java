package com.algaecare.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Environment {
    private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());
    private int algaeLevel; // in percentage (0-100)

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
        }
    }

    public void reset() {
        this.algaeLevel = 50;
    }

    public int getAlgaeLevel() {
        return algaeLevel;
    }
}