package com.algaecare.controller;

import com.algaecare.model.GameState;
import com.algaecare.view.View;
import com.algaecare.view.Screen;
import com.algaecare.view.Transition;
import javafx.util.Duration;
import java.util.logging.Logger;

public class ScreenController {
    private static final Logger LOGGER = Logger.getLogger(ScreenController.class.getName());
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
            case START -> new Screen(
                    "/images/screen/start.png");
            case TRANSITION -> new Transition(
                    "/images/transition/start_to_main.png",
                    Duration.seconds(2),
                    gameController::handleTransitionComplete);
            case MAIN -> new Screen(
                    "/images/screen/main.png");
        };
    }
}