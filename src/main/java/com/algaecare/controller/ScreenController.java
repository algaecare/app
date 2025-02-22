package com.algaecare.controller;

import com.algaecare.model.GameState;
import com.algaecare.view.View;
import com.algaecare.view.Screen;
import com.algaecare.view.Transition;
import javafx.util.Duration;

public class ScreenController {
    private final MainController gameController;

    public ScreenController(MainController controller) {
        this.gameController = controller;
    }

    public void updateScreen() {
        View newView = getScreenForState(gameController.getGameState());
        gameController.getWindow().mountView(newView);
    }

    private View getScreenForState(GameState state) {
        return switch (state) {
            case NO_GAME -> new Screen(
                    "/images/screen/NO_GAME.png");
            case START_TRANSITION -> new Transition(
                    "/images/transition/NO_GAME_TRANSITION.png",
                    Duration.seconds(2),
                    gameController::handleTransitionComplete);
            case INTRO -> new Screen(
                    "/images/screen/INTRO.png");
            case INTRO_TRANSITION -> new Transition(
                    "/images/transition/INTRO_TRANSITION.png",
                    Duration.seconds(2),
                    gameController::handleTransitionComplete);
            case MAIN -> new Screen(
                    "/images/screen/MAIN.png");
        };
    }
}