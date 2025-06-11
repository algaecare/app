package com.algaecare.controller.input;

import com.algaecare.controller.GameStateEventManager;
import com.algaecare.controller.MainController;
import com.algaecare.model.GameState;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.logging.Logger;

public class TimeController implements GameStateEventManager {
    private static final Logger LOGGER = Logger.getLogger(TimeController.class.getName());
    private static final int GAME_DURATION_SECONDS = 120; // 2 minutes

    private final Timeline timer;
    private GameState currentState;
    private MainController mainController;
    private int secondsRemaining;
    private boolean isRunning = false;

    public TimeController(MainController MainController) {
        this.mainController = MainController;
        this.secondsRemaining = GAME_DURATION_SECONDS;

        timer = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    if (secondsRemaining > 0) {
                        secondsRemaining--;
                        LOGGER.info("Time remaining: " + secondsRemaining + " seconds");
                        if (secondsRemaining == 0) {
                            mainController.finishGame();
                        }
                    }
                }));
        timer.setCycleCount(Timeline.INDEFINITE);
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

    public void startTimer() {
        LOGGER.info("Starting game timer");
        secondsRemaining = GAME_DURATION_SECONDS;
        isRunning = true;
        timer.play();
    }

    public void resetTimer() {
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