package com.algaecare.controller.input;

import com.algaecare.controller.GameStateEventManager;
import com.algaecare.model.Environment;
import com.algaecare.model.GameState;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.logging.Logger;

public class TimeController implements GameStateEventManager {
    private static final Logger LOGGER = Logger.getLogger(TimeController.class.getName());
    private static final int GAME_DURATION_SECONDS = 120; // 2 minutes
    private static final int ALGAE_THRESHOLD = 50; // Extract constant

    private final Environment environment;
    private final Timeline timer;
    private final GameStateEventManager.EventEmitter eventEmitter;
    private GameState currentState;
    private int secondsRemaining;
    private boolean isRunning = false;

    public TimeController(GameStateEventManager.EventEmitter eventEmitter, Environment environment) {
        this.eventEmitter = eventEmitter;
        this.environment = environment;
        this.secondsRemaining = GAME_DURATION_SECONDS;

        // Create a repeating timeline that fires every second
        timer = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    if (secondsRemaining > 0) {
                        secondsRemaining--;
                        LOGGER.info("Time remaining: " + secondsRemaining + " seconds");
                        if (secondsRemaining == 0) {
                            handleTimerComplete();
                        }
                    }
                }));
        timer.setCycleCount(Timeline.INDEFINITE);
    }

    private void handleTimerComplete() {
        // Already on FX thread from Timeline, no need for Platform.runLater
        GameState endState = environment.getAlgaeLevel() > ALGAE_THRESHOLD
                ? GameState.ENDSCREEN_POSITIVE
                : GameState.ENDSCREEN_NEGATIVE;

        LOGGER.info("Game ended with algae level: " + environment.getAlgaeLevel());
        eventEmitter.emitGameStateChange(endState);
        resetTimer();
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        this.currentState = newState;

        if (newState == GameState.GAMEPLAY && !isRunning) {
            startTimer();
        } else if (newState == GameState.TITLE) {
            resetTimer();
        }
    }

    private void startTimer() {
        LOGGER.info("Starting game timer");
        secondsRemaining = GAME_DURATION_SECONDS;
        isRunning = true;
        timer.play();
    }

    private void resetTimer() {
        LOGGER.info("Resetting game timer");
        timer.stop();
        isRunning = false;
        secondsRemaining = GAME_DURATION_SECONDS;
    }

    @Override
    public GameState getCurrentState() {
        return currentState;
    }

    public int getSecondsRemaining() {
        return secondsRemaining;
    }
}