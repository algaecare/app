package com.algaecare.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class GameStateTest {

    @Test
    void testEnumValues() {
        GameState[] expectedValues = {
                GameState.TITLE,
                GameState.OPENING,
                GameState.GAMEPLAY,
                GameState.CUTSCENE,
                GameState.ENDING
        };

        GameState[] actualValues = GameState.values();
        assertArrayEquals(expectedValues, actualValues, "Enum values should match expected values");
    }

    @Test
    void testValueOf() {
        assertEquals(GameState.TITLE, GameState.valueOf("TITLE"), "GameState.TITLE should be found");
        assertEquals(GameState.OPENING, GameState.valueOf("OPENING"), "GameState.OPENING should be found");
        assertEquals(GameState.GAMEPLAY, GameState.valueOf("GAMEPLAY"), "GameState.GAMEPLAY should be found");
        assertEquals(GameState.CUTSCENE, GameState.valueOf("CUTSCENE"), "GameState.CUTSCENE should be found");
        assertEquals(GameState.ENDING, GameState.valueOf("ENDING"), "GameState.ENDING should be found");
    }

    @Test
    void testInvalidValueOf() {
        assertThrows(IllegalArgumentException.class, () -> {
            GameState.valueOf("INVALID");
        }, "Should throw IllegalArgumentException for invalid enum value");
    }
}