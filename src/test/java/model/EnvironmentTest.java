package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.algaecare.model.Environment;
import com.algaecare.model.EnvironmentObject;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Feature: Environment Management")
class EnvironmentTest {

    private Environment environment;

    @BeforeEach
    void setUp() {
        environment = new Environment(90, 16, 70, 85);
    }

    @Nested
    @DisplayName("Scenario Group: Environment Initialization")
    class EnvironmentInitialization {

        @Test
        @DisplayName("Scenario: Environment objects are initialized with correct count")
        void scenario_Environment_Objects_Are_Initialized() {
            // Given: A newly created environment
            // When: Getting the list of environment objects
            var objects = environment.getEnvironmentObjects();

            // Then: The list should contain exactly 9 objects
            assertEquals(9, objects.size(), "Environment should have 9 objects");
        }

        @Test
        @DisplayName("Scenario: Environment is initialized with correct values")
        void scenario_Environment_Has_Correct_Initial_Values() {
            // Given: An environment initialized with specific values
            // When: Getting the algae level
            int algaeLevel = environment.getAlgaeLevel();

            // Then: The algae level should match the initialization value
            assertEquals(70, algaeLevel, "Initial algae level should be 70");
        }
    }

    @Nested
    @DisplayName("Scenario Group: Environment Updates")
    class EnvironmentUpdates {

        @ParameterizedTest(name = "Scenario: Update with {0}")
        @CsvSource({
                "Trash, -10, 1, -20, 0",
                "Car, -20, 2, -30, 0",
                "Bicycle, 0, 0, 0, 0",
                "Trash Grabber, 10, -2, 20, 0"
        })
        void scenario_Update_Environment_With_Different_Objects(
                String name, int co2Change, int tempChange, int algaeChange, int o2Change) {
            // Given: An environment and an environment object
            var object = new EnvironmentObject(name, co2Change, tempChange, algaeChange, o2Change);
            int initialAlgaeLevel = environment.getAlgaeLevel();

            // When: Updating the environment with the object
            environment.updateEnvironment(object);

            // Then: The algae level should be updated correctly
            assertEquals(
                    initialAlgaeLevel + algaeChange,
                    environment.getAlgaeLevel(),
                    "Algae level should be updated by " + algaeChange);
        }
    }

    @Nested
    @DisplayName("Scenario Group: Environment Reset")
    class EnvironmentReset {

        @Test
        @DisplayName("Scenario: Reset environment to default values")
        void scenario_Reset_Environment() {
            // Given: An environment with non-zero values
            // When: Resetting the environment
            environment.reset();

            // Then: All values should be reset to 0
            assertEquals(0, environment.getAlgaeLevel(), "Algae level should be reset to 0");

            // And: The environment objects list should still be present
            assertFalse(
                    environment.getEnvironmentObjects().isEmpty(),
                    "Environment objects list should not be empty after reset");
        }
    }

    @Nested
    @DisplayName("Scenario Group: Environment Objects Management")
    class EnvironmentObjectsManagement {

        @Test
        @DisplayName("Scenario: Verify predefined environment objects")
        void scenario_Verify_Predefined_Objects() {
            // Given: A newly created environment
            // When: Getting the environment objects
            var objects = environment.getEnvironmentObjects();

            // Then: The objects should include specific items
            assertTrue(
                    objects.stream().anyMatch(obj -> obj.getName().equals("Car")),
                    "Environment should contain a Car object");
            assertTrue(
                    objects.stream().anyMatch(obj -> obj.getName().equals("Bicycle")),
                    "Environment should contain a Bicycle object");
            assertTrue(
                    objects.stream().anyMatch(obj -> obj.getName().equals("Trash Grabber")),
                    "Environment should contain a Trash Grabber object");
        }
    }
}