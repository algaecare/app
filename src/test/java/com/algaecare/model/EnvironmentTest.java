package com.algaecare.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

class EnvironmentTest {
    private Environment environment;

    @BeforeEach
    void setUp() {
        environment = new Environment(50, 25, 100, 100);
    }

    @Test
    void testInitialization() {
        assertEquals(50, environment.getCo2Level(), "Initial CO2 level should be 50%");
        assertEquals(25, environment.getTemperature(), "Initial temperature should be 25°C");
        assertEquals(100, environment.getAlgaeLevel(), "Initial algae level should be 100%");
        assertEquals(100, environment.getO2Level(), "Initial O2 level should be 100%");

        assertNotNull(environment.getEnvironmentObjects(), "Environment objects should be initialized");
        assertFalse(environment.getEnvironmentObjects().isEmpty(), "Environment objects list should not be empty");
    }

    @ParameterizedTest
    @CsvSource({
            "10, 60, 25, 100, 100", // Increase CO2 by 10
            "-10, 40, 25, 100, 100", // Decrease CO2 by 10
            "100, 100, 25, 100, 100", // Max CO2
            "-100, 0, 25, 100, 100" // Min CO2
    })
    void testCo2LevelBoundaries(int change, int expectedCo2, int expectedTemp,
            int expectedAlgae, int expectedO2) {
        EnvironmentObject testObject = new EnvironmentObject("Test", change, 0, 0, 0);
        environment.updateEnvironment(testObject);

        assertEquals(expectedCo2, environment.getCo2Level(),
                "CO2 level should be properly bounded");
        assertEquals(expectedTemp, environment.getTemperature(),
                "Temperature should remain unchanged");
        assertEquals(expectedAlgae, environment.getAlgaeLevel(),
                "Algae level should remain unchanged");
        assertEquals(expectedO2, environment.getO2Level(),
                "O2 level should remain unchanged");
    }

    @Test
    void testEnvironmentUpdate() {
        // Create test object with known values
        EnvironmentObject testObject = new EnvironmentObject("Test", 10, 50, -30, -20);
        environment.updateEnvironment(testObject);

        // CO2: 50 + 10 = 60
        assertEquals(60, environment.getCo2Level(), "CO2 should increase by 10");

        // Temperature: 25 + (10/100 * 4) = 25 + 0.4 = 25.4 (rounded to 25)
        assertEquals(25, environment.getTemperature(),
                "Temperature should increase by 0.4 degrees");

        // Algae: 100 - (0.4 * 10) = 100 - 4 = 96
        assertEquals(96, environment.getAlgaeLevel(),
                "Algae should decrease by 4%");

        // O2: 100 - (4 * 0.5) = 100 - 2 = 98
        assertEquals(98, environment.getO2Level(),
                "O2 should decrease by 2%");
    }

    @Test
    void testEnvironmentObjectsList() {
        List<EnvironmentObject> objects = environment.getEnvironmentObjects();

        // Test for required objects
        assertTrue(objects.stream().anyMatch(obj -> obj.getName().equals("Trash")),
                "Should contain Trash object");
        assertTrue(objects.stream().anyMatch(obj -> obj.getName().equals("Car")),
                "Should contain Car object");
        assertTrue(objects.stream().anyMatch(obj -> obj.getName().equals("Bicycle")),
                "Should contain Bicycle object");

        // Test object properties
        EnvironmentObject trash = objects.stream()
                .filter(obj -> obj.getName().equals("Trash"))
                .findFirst()
                .orElseThrow();

        assertEquals(-10, trash.getCo2Change(), "Trash should have -10 CO2 change");
        assertEquals(-20, trash.getAlgaeChange(), "Trash should have -20 algae change");
    }

    @Test
    void testMaximumValues() {
        environment = new Environment(100, 30, 100, 100);
        EnvironmentObject testObject = new EnvironmentObject("Test", 10, 100, 10, 10);
        environment.updateEnvironment(testObject);

        assertEquals(100, environment.getCo2Level(), "CO2 should not exceed 100%");
        assertEquals(34, environment.getTemperature(), "Temperature should increase");
        assertEquals(100, environment.getAlgaeLevel(), "Algae should not exceed 100%");
        assertEquals(100, environment.getO2Level(), "O2 should not exceed 100%");
    }

    @Test
    void testMinimumValues() {
        environment = new Environment(0, 5, 0, 0);
        EnvironmentObject testObject = new EnvironmentObject("Test", -10, -100, -10, -10);
        environment.updateEnvironment(testObject);

        assertEquals(0, environment.getCo2Level(), "CO2 should not go below 0%");
        assertEquals(1, environment.getTemperature(), "Temperature should not go below 0°C");
        assertEquals(0, environment.getAlgaeLevel(), "Algae should not go below 0%");
        assertEquals(0, environment.getO2Level(), "O2 should not go below 0%");
    }
}