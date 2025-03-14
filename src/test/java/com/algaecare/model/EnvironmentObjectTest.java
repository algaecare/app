package com.algaecare.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnvironmentObjectTest {
    private EnvironmentObject environmentObject;

    @BeforeEach
    void setUp() {
        environmentObject = new EnvironmentObject("TestObject", 10, 5, -20, 15);
    }

    @Test
    void testInitialization() {
        assertEquals("TestObject", environmentObject.getName(), "Name should be 'TestObject'");
        assertEquals(10, environmentObject.getCo2Change(), "CO2 change should be 10");
        assertEquals(5, environmentObject.getTemperatureChange(), "Temperature change should be 5");
        assertEquals(-20, environmentObject.getAlgaeChange(), "Algae change should be -20");
        assertEquals(15, environmentObject.getOxygenChange(), "Oxygen change should be 15");
    }

    @Test
    void testGetName() {
        assertEquals("TestObject", environmentObject.getName(), "Name should be 'TestObject'");
    }

    @Test
    void testGetCo2Change() {
        assertEquals(10, environmentObject.getCo2Change(), "CO2 change should be 10");
    }

    @Test
    void testGetTemperatureChange() {
        assertEquals(5, environmentObject.getTemperatureChange(), "Temperature change should be 5");
    }

    @Test
    void testGetAlgaeChange() {
        assertEquals(-20, environmentObject.getAlgaeChange(), "Algae change should be -20");
    }

    @Test
    void testGetOxygenChange() {
        assertEquals(15, environmentObject.getOxygenChange(), "Oxygen change should be 15");
    }
}