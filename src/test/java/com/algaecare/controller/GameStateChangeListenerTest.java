package com.algaecare.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.algaecare.model.GameState;

class GameStateChangeListenerTest {

    private TestGameStateChangeListener listener;
    private GameState lastOldState;
    private GameState lastNewState;
    private int stateChangeCount;

    @BeforeEach
    void setUp() {
        listener = new TestGameStateChangeListener();
        stateChangeCount = 0;
    }

    @Test
    void testStateChange_FromTitleToOpening() {
        // Arrange
        GameState oldState = GameState.TITLE;
        GameState newState = GameState.OPENING;

        // Act
        listener.onGameStateChanged(oldState, newState);

        // Assert
        assertEquals(oldState, lastOldState, "Old state should be TITLE");
        assertEquals(newState, lastNewState, "New state should be OPENING");
        assertEquals(1, stateChangeCount, "State change should be recorded once");
    }

    @Test
    void testStateChange_FromOpeningToGameplay() {
        // Arrange
        GameState oldState = GameState.OPENING;
        GameState newState = GameState.GAMEPLAY;

        // Act
        listener.onGameStateChanged(oldState, newState);

        // Assert
        assertEquals(oldState, lastOldState, "Old state should be OPENING");
        assertEquals(newState, lastNewState, "New state should be GAMEPLAY");
        assertEquals(1, stateChangeCount, "State change should be recorded once");
    }

    @Test
    void testMultipleStateChanges() {
        // Act
        listener.onGameStateChanged(GameState.TITLE, GameState.OPENING);
        listener.onGameStateChanged(GameState.OPENING, GameState.GAMEPLAY);
        listener.onGameStateChanged(GameState.GAMEPLAY, GameState.CUTSCENE);

        // Assert
        assertEquals(3, stateChangeCount, "Should record three state changes");
        assertEquals(GameState.GAMEPLAY, lastOldState, "Last old state should be GAMEPLAY");
        assertEquals(GameState.CUTSCENE, lastNewState, "Last new state should be CUTSCENE");
    }

    @Test
    void testInvalidStateChange() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> listener.onGameStateChanged(GameState.TITLE, null),
                "Should throw exception when new state is null");
    }

    // Test implementation of GameStateChangeListener
    private class TestGameStateChangeListener implements GameStateChangeListener {
        @Override
        public void onGameStateChanged(GameState oldState, GameState newState) {
            if (newState == null) {
                throw new IllegalArgumentException("New state cannot be null");
            }
            lastOldState = oldState;
            lastNewState = newState;
            stateChangeCount++;
        }
    }
}