package com.algaecare.model;

public class EnvironmentObject {
    private final String name;
    int co2Change;
    int temperatureChange;
    int algaeChange;
    int oxygenChange;

    public EnvironmentObject(String name, int algaeChange) {
        this.name = name;
        this.algaeChange = algaeChange;

    }

    public String getName() {
        return name;
    }

    public int getAlgaeChange() {
        return algaeChange;
    }


}