package com.algaecare.model;

import java.util.List;
import java.util.logging.Logger;

import com.algaecare.app.Main;

public class Environment {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final double TEMPERATURE_CHANGE_FACTOR = 4.0; // 4°C per 100% CO2
    private static final double ALGAE_CHANGE_FACTOR = 10.0; // 10% decrease per 1°C
    private static final double OXYGEN_CHANGE_FACTOR = 0.5; // 50% of algae change

    private int co2Level; // in percentage (0-100)
    private int temperature; // in degrees Celsius
    private int algaeLevel; // in percentage (0-100)
    private int o2Level; // in percentage (0-100)

    private List<EnvironmentObject> environmentObjects;

    public Environment(int co2Level, int temperature, int algaeLevel, int o2Level) {
        this.co2Level = co2Level;
        this.temperature = temperature;
        this.algaeLevel = algaeLevel;
        this.o2Level = o2Level;

        // Initialize environment objects
        this.environmentObjects = initializeEnvironmentObjects();
    }

    public void updateEnvironment(EnvironmentObject environmentObject) {
        LOGGER.info("Updating environment with object: " + environmentObject.getName());

        // First apply direct CO2 change
        int co2Change = environmentObject.getCo2Change();
        setCo2Level(co2Change);

        // Then calculate and apply temperature change based on CO2 change
        double temperatureChange = (co2Change / 100.0) * TEMPERATURE_CHANGE_FACTOR;
        setTemperature((int) Math.round(temperatureChange));

        // Calculate and apply algae change based on temperature change
        double algaeChange = temperatureChange * ALGAE_CHANGE_FACTOR;
        setAlgaeLevel(-(int) Math.round(algaeChange));

        // Calculate and apply oxygen change (50% of algae change)
        double oxygenChange = algaeChange * OXYGEN_CHANGE_FACTOR;
        setO2Level(-(int) Math.round(oxygenChange));

        LOGGER.info(String.format(
                "Environment updated: CO2 Level: %d%%, Temperature: %d°C, Algae Level: %d%%, O2 Level: %d%%",
                co2Level, temperature, algaeLevel, o2Level));
    }

    public int getCo2Level() {
        return co2Level;
    }

    private void setCo2Level(int change) {
        this.co2Level = Math.max(0, Math.min(100, this.co2Level + change));
    }

    public int getTemperature() {
        return temperature;
    }

    private void setTemperature(int change) {
        this.temperature = Math.max(0, this.temperature + change);
    }

    public int getAlgaeLevel() {
        return algaeLevel;
    }

    private void setAlgaeLevel(int change) {
        this.algaeLevel = Math.max(0, Math.min(100, this.algaeLevel + change));
    }

    public int getO2Level() {
        return o2Level;
    }

    private void setO2Level(int change) {
        this.o2Level = Math.max(0, Math.min(100, this.o2Level + change));
    }

    public List<EnvironmentObject> initializeEnvironmentObjects() {
        // CHECK DOCUMENTATION:
        // https://fhnw-projecttrack.atlassian.net/wiki/spaces/IP1224vt3/pages/594149391/Alltagsobjekte
        // Bad Objects
        EnvironmentObject trash = new EnvironmentObject("Trash", -10, 0, -20, 0);
        EnvironmentObject car = new EnvironmentObject("Car", -20, 0, -30, 0);
        EnvironmentObject airplane = new EnvironmentObject("Airplane", -30, 0, -40, 0);
        EnvironmentObject shoppingBagWorld = new EnvironmentObject("Shopping Bag World", -40, 0, -50, 0);

        // Neutral Objects
        EnvironmentObject recycling = new EnvironmentObject("Recycling", 0, 0, 0, 0);
        EnvironmentObject train = new EnvironmentObject("Train", 0, 0, 0, 0);
        EnvironmentObject shoppingBagLocal = new EnvironmentObject("Shopping Bag Local", 0, 0, 0, 0);
        EnvironmentObject bicycle = new EnvironmentObject("Bicycle", 0, 0, 0, 0);

        // Good Objects
        EnvironmentObject trashGrabber = new EnvironmentObject("Trash Grabber", 10, 0, 20, 0);

        // Add all objects to a list
        environmentObjects = List.of(
                trash,
                car,
                airplane,
                shoppingBagWorld,
                recycling,
                train,
                shoppingBagLocal,
                bicycle,
                trashGrabber);
        return environmentObjects;
    }

    public List<EnvironmentObject> getEnvironmentObjects() {
        return environmentObjects;
    }
}