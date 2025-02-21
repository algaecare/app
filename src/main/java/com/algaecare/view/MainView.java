package com.algaecare.view;

import com.algaecare.controller.MainController;
import com.algaecare.input.GameInput;
import com.algaecare.input.KeyboardInput;
import com.algaecare.input.Pi4JInput;
import com.algaecare.view.screen.GameScreen;
import com.algaecare.view.screen.GameplayScreen;
import com.algaecare.view.screen.StartScreen;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;

public class MainView extends VBox {
    private final MainController controller;
    private GameInput gameInput;
    private GameScreen currentScreen;

    public MainView() {
        controller = new MainController();
        showStartScreen();
    }

    public void initializeInput(Scene scene) {
        try {
            gameInput = new Pi4JInput();
            gameInput.initialize();
        } catch (Exception e) {
            System.out.println("Pi4J not available, falling back to keyboard input");
            gameInput = new KeyboardInput(scene);
            try {
                gameInput.initialize();
            } catch (Exception keyboardError) {
                System.err.println("Failed to initialize keyboard input: " + keyboardError.getMessage());
                return;
            }
        }
        gameInput.setInputCallback(this::handleGameInput);
    }

    private void handleGameInput() {
        if (!controller.isGameRunning()) {
            controller.startGame();
            showGameScreen();
        }
    }

    public void cleanup() {
        gameInput.cleanup();
    }

    private void showStartScreen() {
        if (currentScreen != null) {
            getChildren().clear();
        }
        currentScreen = new StartScreen(controller, this::showGameScreen);
        getChildren().add(currentScreen);
    }

    private void showGameScreen() {
        getChildren().clear();
        currentScreen = new GameplayScreen(controller);
        getChildren().add(currentScreen);
    }
}