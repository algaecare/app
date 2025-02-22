package com.algaecare.controller;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Duration;

import com.algaecare.model.GameState;
import com.algaecare.view.View;
import com.algaecare.view.Screen;
import com.algaecare.view.Transition;

import java.util.logging.Logger;

public class ScreenController {
    private static final Logger LOGGER = Logger.getLogger(ScreenController.class.getName());
    private final MainController gameController;
    private GameState currentState = GameState.START;
    private final List<Runnable> screenChangeListeners = new ArrayList<>();

    public ScreenController(MainController gameController) {
        this.gameController = gameController;
    }

    public void showInitialScreen() {
        gameController.getMainView().mountView(getScreenForState());
    }

    public View getScreenForState() {
        return switch (currentState) {
            case START -> new Screen(
                    "/images/screen/start.png",
                    () -> handleStateChange(GameState.TRANSITION));
            case TRANSITION -> new Transition(
                    "/images/transitions/start_to_main.png",
                    Duration.seconds(2),
                    () -> handleStateChange(GameState.MAIN));
            case MAIN -> new Screen(
                    "/images/screen/main.png",
                    this::handleMainScreenInput);
        };
    }

    private void handleStateChange(GameState newState) {
        LOGGER.info("Changing state from " + currentState + " to " + newState);
        currentState = newState;
        notifyScreenChange();
    }

    private void handleMainScreenInput() {
        LOGGER.info("Main screen input received");
    }

    private void notifyScreenChange() {
        screenChangeListeners.forEach(Runnable::run);
    }
}