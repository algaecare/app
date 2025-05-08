package com.algaecare.model;

public class EnvironmentObject {
    private final GameState objectID;
    private int algaeChange;

    public EnvironmentObject(GameState objectIdFGameState, int algaeChange) {
        this.objectID = objectIdFGameState;
        this.algaeChange = algaeChange;
    }

    public GameState getObjectID() {
        return objectID;
    }

    public int getAlgaeChange() {
        return algaeChange;
    }
}