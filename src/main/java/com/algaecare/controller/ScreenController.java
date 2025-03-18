package com.algaecare.controller;

import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.algaecare.model.GameState;
import com.algaecare.view.AnimationScreen;
import com.algaecare.view.GameScreen;
import com.algaecare.view.IdleScreen;
import com.algaecare.view.ImageSequence;
import com.algaecare.view.Screen;
import com.algaecare.view.Window;

import javafx.stage.Stage;

public class ScreenController implements GameStateChangeListener {
    private static final Logger LOGGER = Logger.getLogger(ScreenController.class.getName());
    private final Window window;
    private final MainController mainController;
    Map<GameState, Screen> screens;

    public ScreenController(MainController controller, Stage stage) {
        this.window = new Window(stage);
        this.mainController = controller;
        initializeScreens();
        String imageSequencePath = "/Users/timbuser/Documents/PRIMEOENERGIE/algae-care/src/main/resources/animations/Idle";
        ImageSequence imageSequence = new ImageSequence(imageSequencePath, 24);
        // setScreen(screens.get(GameState.TITLE));
        window.getChildren().add(imageSequence);
        imageSequence.play();
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
                    mainController.setGameState(GameState.GAMEPLAY);
                });

        // Gameplay Screen
        String gameplayScreenPath = "/animations/Gameplay.mp4";
        GameScreen gameplayScreen = new GameScreen(gameplayScreenPath);

        // Cutscene Screen
        String cutsceneScreenPath = "/animations/Cutscene.mp4";
        AnimationScreen cutsceneScreen = new AnimationScreen(
                cutsceneScreenPath,
                unused -> {
                    mainController.setGameState(GameState.GAMEPLAY);
                });

        // Ending Screen
        String endingScreenPath = "/animations/Ending.mp4";
        AnimationScreen endingScreen = new AnimationScreen(
                endingScreenPath,
                unused -> {
                    mainController.setGameState(GameState.TITLE);
                });

        // Add screens to the map
        screens = Map.of(
                GameState.TITLE, titleScreen,
                GameState.OPENING, openingScreen,
                GameState.GAMEPLAY, gameplayScreen,
                GameState.CUTSCENE, cutsceneScreen,
                GameState.ENDING, endingScreen);
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
        Screen newScreen = screens.get(newState);
        if (newScreen != null) {
            if (newScreen instanceof GameScreen) {
                ((GameScreen) newScreen).updateEnvironmentDisplay(mainController.getEnvironment());
            }
            setScreen(newScreen);
        } else {
            LOGGER.warning("No screen found for game state: " + newState);
        }
    }
}