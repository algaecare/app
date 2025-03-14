package com.algaecare.controller;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.algaecare.model.GameState;
import com.algaecare.view.AnimationScreen;
import com.algaecare.view.IdleScreen;
import com.algaecare.view.Screen;
import com.algaecare.view.Window;

import javafx.stage.Stage;

public class ScreenController implements GameStateChangeListener {
    private static final Logger LOGGER = Logger.getLogger(ScreenController.class.getName());
    private final Window window;
    private final MainController mainController;
    List<Screen> screens;

    public ScreenController(MainController controller, Stage stage) {
        this.window = new Window(stage);
        this.mainController = controller;
        initializeScreens();
        setScreen(screens.get(0));
        window.setVisible(true);
        window.setDisable(false);
    }

    private void initializeScreens() {
        // Title Screen
        String titleScreenPath = "/animations/Title.mp4";
        IdleScreen titleScreen = new IdleScreen(titleScreenPath);

        // Opening Screen
        String openingScreenPath = "/animations/Opening.mp4";
        AnimationScreen openingScreen = new AnimationScreen(
                openingScreenPath,
                unused -> {
                    LOGGER.info("Opening animation completed, transitioning to PLAYING state");
                    mainController.setGameState(GameState.GAMEPLAY);
                });

        screens = List.of(titleScreen, openingScreen);
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        try {
            updateScreen(newState);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error changing game state from " + oldState + " to " + newState, e);
        }
    }

    private void setScreen(Screen screen) {
        window.getChildren().clear();
        window.getChildren().add(screen);
        window.setVisible(true);
        window.setDisable(false);
    }

    private void updateScreen(GameState newState) {
        LOGGER.info("Updating screen to state: " + newState);
        switch (newState) {
            case TITLE:
                setScreen(screens.get(0));
                break;
            case OPENING:
                setScreen(screens.get(1));
                break;
            default:
                break;
        }
    }
}