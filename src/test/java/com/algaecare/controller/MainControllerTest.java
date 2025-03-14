package com.algaecare.controller;

import static org.junit.jupiter.api.Assertions.*;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.algaecare.model.GameState;
import com.algaecare.model.Environment;
import org.testfx.util.WaitForAsyncUtils;

@ExtendWith(ApplicationExtension.class)
class MainControllerTest {
    private MainController controller;
    private Stage stage;

    @Start
    private void start(Stage stage) {
        this.stage = stage;
        Platform.runLater(() -> {
            controller = new MainController(stage);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @BeforeEach
    void setUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller = new MainController(stage);
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testInitialState() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<GameState> stateRef = new AtomicReference<>();
        AtomicReference<Environment> envRef = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                stateRef.set(controller.getGameState());
                envRef.set(controller.getEnvironment());
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
        assertEquals(GameState.TITLE, stateRef.get(), "Initial game state should be TITLE");
        assertNotNull(envRef.get(), "Environment should be initialized");
    }

    @Test
    void testStateChangeListener() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<GameState> oldStateRef = new AtomicReference<>();
        AtomicReference<GameState> newStateRef = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                GameStateChangeListener testListener = (oldState, newState) -> {
                    oldStateRef.set(oldState);
                    newStateRef.set(newState);
                };
                controller.addGameStateChangeListener(testListener);
                controller.setGameState(GameState.OPENING);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
        assertEquals(GameState.TITLE, oldStateRef.get(), "Old state should be TITLE");
        assertEquals(GameState.OPENING, newStateRef.get(), "New state should be OPENING");
    }

    @Test
    void testKeyHandling() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<GameState> stateRef = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                // Test space key in title state
                KeyEvent spaceEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "",
                        KeyCode.SPACE, false, false, false, false);
                stage.getScene().getOnKeyPressed().handle(spaceEvent);
                stateRef.set(controller.getGameState());
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
        assertEquals(GameState.OPENING, stateRef.get(),
                "Space key in TITLE state should transition to OPENING state");
    }

    @Test
    void testSpaceKeyInTitleState() {
        // Arrange
        controller.setGameState(GameState.TITLE);
        KeyEvent spaceEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "",
                KeyCode.SPACE, false, false, false, false);

        // Act
        stage.getScene().getOnKeyPressed().handle(spaceEvent);

        // Assert
        assertEquals(GameState.OPENING, controller.getGameState(),
                "Space key in TITLE state should transition to OPENING state");
    }

    @Test
    void testEscapeKey() {
        // Arrange
        KeyEvent escapeEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "",
                KeyCode.ESCAPE, false, false, false, false);

        // Act
        stage.getScene().getOnKeyPressed().handle(escapeEvent);

        // Assert
        assertEquals(GameState.ENDING, controller.getGameState(),
                "Escape key should transition to ENDING state");
    }

    @Test
    void testDigitKeyInGameplayState() {
        // Arrange
        controller.setGameState(GameState.GAMEPLAY);
        KeyEvent digitEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "",
                KeyCode.DIGIT1, false, false, false, false);

        // Act
        stage.getScene().getOnKeyPressed().handle(digitEvent);

        // Assert
        assertEquals(GameState.CUTSCENE, controller.getGameState(),
                "Digit key in GAMEPLAY state should transition to CUTSCENE state");
    }

    @Test
    void testRemoveStateChangeListener() {
        // Arrange
        AtomicReference<GameState> stateRef = new AtomicReference<>();
        GameStateChangeListener testListener = (oldState, newState) ->
                stateRef.set(newState);

        // Act
        controller.addGameStateChangeListener(testListener);
        controller.removeGameStateChangeListener(testListener);
        controller.setGameState(GameState.GAMEPLAY);

        // Assert
        assertNull(stateRef.get(),
                "Removed listener should not receive state changes");
    }

    @Test
    void testEnvironmentUpdate() {
        // Arrange
        controller.setGameState(GameState.GAMEPLAY);
        Environment env = controller.getEnvironment();
        int initialCo2Level = env.getCo2Level();

        // Act
        stage.getScene().getOnKeyPressed().handle(
                new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DIGIT1,
                        false, false, false, false)
        );

        // Assert
        assertNotEquals(initialCo2Level, env.getCo2Level(),
                "Environment should be updated when digit key is pressed");
        assertEquals(GameState.CUTSCENE, controller.getGameState(),
                "State should change to CUTSCENE after environment update");
    }
}