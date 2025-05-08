package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.algaecare.model.EnvironmentObject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Feature: Environment Object")
class EnvironmentObjectTest {

    @Nested
    @DisplayName("Scenario: Creating a new environment object")
    class CreatingEnvironmentObject {

        @Test
        @DisplayName("Given a car with environmental impact values" +
                "When the object is created" +
                "Then it should have the correct name")
        void shouldHaveCorrectName() {
            // Given
            String name = "Car";
            int co2Change = 10;
            int temperatureChange = 5;
            int algaeChange = -3;
            int oxygenChange = -2;

            // When
            EnvironmentObject object = new EnvironmentObject(
                    name, co2Change, temperatureChange, algaeChange, oxygenChange);

            // Then
            assertEquals(name, object.getName());
        }

        @ParameterizedTest(name = "Then {0} should be {1}")
        @CsvSource({
                "co2Change, 10",
                "temperatureChange, 5",
                "algaeChange, -3",
                "oxygenChange, -2"
        })
        @DisplayName("Given a car with environmental impact values" +
                "When the object is created" +
                "Then it should have correct environmental changes")
        void shouldHaveCorrectEnvironmentalChanges(String field, int expectedValue) {
            // Given
            EnvironmentObject object = new EnvironmentObject(
                    "Car", 10, 5, -3, -2);

            // When
            int actualValue = switch (field) {
                case "co2Change" -> object.getCo2Change();
                case "temperatureChange" -> object.getTemperatureChange();
                case "algaeChange" -> object.getAlgaeChange();
                case "oxygenChange" -> object.getOxygenChange();
                default -> throw new IllegalArgumentException("Unknown field: " + field);
            };

            // Then
            assertEquals(expectedValue, actualValue,
                    "Incorrect value for " + field);
        }
    }
}