package com.algaecare.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import com.algaecare.view.Window;
import com.algaecare.controller.input.InputController;
import com.algaecare.controller.input.KeyboardController;
import com.algaecare.controller.input.RFIDController;
import com.algaecare.model.GameState;
import com.algaecare.model.InputAction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

// MainController class for the Algae Care application
// This class manages the main view and input handling for the application.
public class MainController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());
    private Window window;
    private final ScreenController screenController;
    private GameState gameState;

    // region Constructor
    public MainController() {
        this.screenController = new ScreenController(this);
        setGameState(GameState.START);
    }
    // endregion

    // region Getters and Setters
    public GameState getGameState() {
        return gameState;
    }

    public Window getWindow() {
        return window;
    }

    public void setGameState(GameState newState) {
        LOGGER.info("State changing from " + gameState + " to " + newState);
        this.gameState = newState;
    }
    // endregion

    // region Public Methods
    public void initializeApplication(Stage primaryStage) {
        this.window = new Window(primaryStage);
        initializeInput(primaryStage.getScene());
        screenController.updateScreen();
        primaryStage.show();
    }

    public void handleInput(InputAction action) {
        LOGGER.info("Input received: " + action);

        switch (gameState) {
            case START -> {
                if (action == InputAction.AXOLOTL) {
                    LOGGER.info("AXOLOTL detected, starting transition");
                    setGameState(GameState.TRANSITION);
                    screenController.updateScreen();
                }
            }
            case TRANSITION -> {
                // no Input on Transition possible
            }
            case MAIN -> {
                // handleMainGameInput(action);
            }
        }
    }

    public void handleTransitionComplete() {
        LOGGER.info("Transition complete");
        switch (this.gameState) {
            case TRANSITION -> setGameState(GameState.MAIN);
            default -> throw new IllegalArgumentException("Unexpected value: " + this.gameState);
        }
        screenController.updateScreen();
    }
    // endregion

    // region Private Methods
    private void initializeInput(Scene scene) {
        List<InputController> inputControllers = new ArrayList<>();

        // Always add keyboard controller for testing
        inputControllers.add(new KeyboardController(scene));

        // Add RFID controller if on Raspberry Pi
        if (isRaspberryPi()) {
            try {
                inputControllers.add(new RFIDController());
            } catch (Exception e) {
                LOGGER.warning("Failed to initialize RFID controller");
            }
        }

        // Initialize all controllers
        inputControllers.forEach(controller -> {
            controller.initialize();
            controller.setInputCallback(this::handleInput);
        });

        LOGGER.info("Input controllers initialized");
    }

    private boolean isRaspberryPi() {
        return System.getProperty("os.name").toLowerCase().contains("linux")
                && System.getProperty("os.arch").toLowerCase().contains("arm");
    }
    // endregion
}