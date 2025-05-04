package com.algaecare.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Environment {
    private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());

    private int co2Level; // in percentage (0-100)
    private int temperature; // in degrees Celsius
    private int algaeLevel; // in percentage (0-100)
    private int o2Level; // in percentage (0-100)

    private List<EnvironmentObject> environmentObjects = new ArrayList<>();

    public Environment(int co2Level, int temperature, int algaeLevel, int o2Level) {
        this.co2Level = co2Level;
        this.temperature = temperature;
        this.algaeLevel = algaeLevel;
        this.o2Level = o2Level;

        this.environmentObjects = initializeEnvironmentObjects();
    }

    private List<EnvironmentObject> initializeEnvironmentObjects() {
        // https://fhnw-projecttrack.atlassian.net/wiki/spaces/IP1224vt3/pages/594149391/Alltagsobjekte
        // Bad Objects
        environmentObjects.add(new EnvironmentObject("Trash", -10, 1, -20, 0));
        environmentObjects.add(new EnvironmentObject("Car", -20, 2, -30, 0));
        environmentObjects.add(new EnvironmentObject("Airplane", -30, 3, -40, 0));
        environmentObjects.add(new EnvironmentObject("Shopping Bag World", -40, 4, -50, 0));
        // Neutral Objects
        environmentObjects.add(new EnvironmentObject("Recycling", 0, 0, 0, 0));
        environmentObjects.add(new EnvironmentObject("Train", 0, 0, 0, 0));
        environmentObjects.add(new EnvironmentObject("Shopping Bag Local", 0, 0, 0, 0));
        environmentObjects.add(new EnvironmentObject("Bicycle", 0, 0, 0, 0));
        // Good Objects
        environmentObjects.add(new EnvironmentObject("Trash Grabber", 10, -2, 20, 0));
        return environmentObjects;
    }

    public void updateEnvironment(EnvironmentObject environmentObject) {
        LOGGER.info("Updating environment with object: " + environmentObject.getName());

        // Calculate changes based on the environment object
        int co2Change = environmentObject.getCo2Change();
        this.co2Level = co2Level + co2Change;

        int temperatureChange = environmentObject.getTemperatureChange();
        this.temperature = temperature + temperatureChange;

        int oxygenChange = environmentObject.getOxygenChange();
        this.o2Level = o2Level + oxygenChange;

        // Calculate and apply algae change based on temperature change
        int algaeChange = environmentObject.getAlgaeChange();
        this.algaeLevel = algaeLevel + algaeChange;

        LOGGER.info(String.format(
                "Environment updated: CO2 Level: %d%%, Temperature: %d°C, Algae Level: %d%%, O2 Level: %d%%",
                co2Level, temperature, algaeLevel, o2Level));
    }

    public void reset() {
        this.co2Level = 0;
        this.temperature = 0;
        this.algaeLevel = 0;
        this.o2Level = 0;

        LOGGER.info("Environment reset to default values.");
    }

    public int getAlgaeLevel() {
        return algaeLevel;
    }

    public List<EnvironmentObject> getEnvironmentObjects() {
        return environmentObjects;
    }
}