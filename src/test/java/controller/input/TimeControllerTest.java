package controller.input;

import com.algaecare.controller.GameStateEventManager;
import com.algaecare.controller.input.TimeController;
import com.algaecare.model.Environment;
import com.algaecare.model.GameState;
import javafx.application.Platform;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimeControllerTest {

    private GameStateEventManager.EventEmitter emitter;
    private Environment environment;

    @BeforeAll
    static void initJfx() {
        // Initialize JavaFX toolkit for headless test environments
        new JFXPanel();
    }

    @BeforeEach
    void setUp() {
        emitter = mock(GameStateEventManager.EventEmitter.class);
        environment = mock(Environment.class);
    }

    @Test
    void timerStartsOnGameplayState() {
        TimeController controller = new TimeController(emitter, environment);
        controller.onGameStateChanged(GameState.TITLE, GameState.GAMEPLAY);
        assertEquals(GameState.GAMEPLAY, controller.getCurrentState());
        assertEquals(120, controller.getSecondsRemaining());
    }

    @Test
    void timerResetsOnTitleState() {
        TimeController controller = new TimeController(emitter, environment);
        controller.onGameStateChanged(GameState.TITLE, GameState.GAMEPLAY);
        controller.onGameStateChanged(GameState.GAMEPLAY, GameState.TITLE);
        assertEquals(GameState.TITLE, controller.getCurrentState());
        assertEquals(120, controller.getSecondsRemaining());
    }

    @Test
    void handleTimerCompleteEmitsPositiveEndscreenIfAlgaeLevelHigh() throws Exception {
        when(environment.getAlgaeLevel()).thenReturn(80);
        TimeController controller = new TimeController(emitter, environment);

        // Simulate timer complete
        controller.onGameStateChanged(GameState.TITLE, GameState.GAMEPLAY);
        controller.onGameStateChanged(GameState.GAMEPLAY, GameState.GAMEPLAY);
        controller.onGameStateChanged(GameState.GAMEPLAY, GameState.GAMEPLAY);

        var method = TimeController.class.getDeclaredMethod("handleTimerComplete");
        method.setAccessible(true);

        // Use a latch to wait for Platform.runLater
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(emitter).emitGameStateChange(GameState.ENDSCREEN_POSITIVE);

        method.invoke(controller);

        assertTrue(latch.await(2, java.util.concurrent.TimeUnit.SECONDS), "FX event not emitted in time");
        verify(emitter, atLeastOnce()).emitGameStateChange(GameState.ENDSCREEN_POSITIVE);
    }

    @Test
    void handleTimerCompleteEmitsNegativeEndscreenIfAlgaeLevelLow() throws Exception {
        // Initialize test setup
        when(environment.getAlgaeLevel()).thenReturn(10);
        TimeController controller = new TimeController(emitter, environment);
        controller.onGameStateChanged(GameState.TITLE, GameState.GAMEPLAY);

        // Create latch before accessing the method
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);

        // Set up mock behavior
        doAnswer(invocation -> {
            Platform.runLater(latch::countDown);
            return null;
        }).when(emitter).emitGameStateChange(GameState.ENDSCREEN_NEGATIVE);

        // Get and invoke private method
        var method = TimeController.class.getDeclaredMethod("handleTimerComplete");
        method.setAccessible(true);

        // Run on FX thread to ensure proper execution
        Platform.runLater(() -> {
            try {
                method.invoke(controller);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Wait for completion
        assertTrue(latch.await(5, java.util.concurrent.TimeUnit.SECONDS),
                "FX event not emitted in time");
        verify(emitter, times(1)).emitGameStateChange(GameState.ENDSCREEN_NEGATIVE);
    }
}