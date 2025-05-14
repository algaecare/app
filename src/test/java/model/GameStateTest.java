package model;

import org.junit.jupiter.api.Test;

import com.algaecare.model.GameState;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void allEnumValuesShouldBePresent() {
        GameState[] values = GameState.values();
        assertTrue(values.length >= 17); // Should match the number of enum constants

        // Check a few specific values
        assertNotNull(GameState.valueOf("TITLE"));
        assertNotNull(GameState.valueOf("GAMEPLAY"));
        assertNotNull(GameState.valueOf("ENDSCREEN_NEGATIVE"));
        assertNotNull(GameState.valueOf("GOODBYE"));
    }

    @Test
    void valueOfShouldReturnCorrectEnum() {
        assertEquals(GameState.OBJECT_BICYCLE, GameState.valueOf("OBJECT_BICYCLE"));
        assertEquals(GameState.ENDSCREEN_POSITIVE, GameState.valueOf("ENDSCREEN_POSITIVE"));
    }
}