package com.algaecare.model;

public class EnvironmentObject {
    private final String name;
    int co2Change;
    int temperatureChange;
    int algaeChange;
    int oxygenChange;

    public EnvironmentObject(String name, int co2Change, int temperatureChange, int algaeChange, int oxygenChange) {
        this.name = name;
        this.co2Change = co2Change;
        this.temperatureChange = temperatureChange;
        this.algaeChange = algaeChange;
        this.oxygenChange = oxygenChange;
    }

    public String getName() {
        return name;
    }

    public int getCo2Change() {
        return co2Change;
    }

    public int getTemperatureChange() {
        return temperatureChange;
    }

    public int getAlgaeChange() {
        return algaeChange;
    }

    public int getOxygenChange() {
        return oxygenChange;
    }
}