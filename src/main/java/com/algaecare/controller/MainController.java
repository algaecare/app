package com.algaecare.controller;

import javafx.stage.Stage;
import com.algaecare.view.Window;
import com.algaecare.input.GameInput;
import java.util.logging.Logger;

// MainController class for the Algae Care application
// This class manages the main view and input handling for the application.
public class MainController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());
    private Window window;
    private final ScreenController screenController;

    public MainController() {
        this.screenController = new ScreenController(this);
    }

    public Window getMainView() {
        return window;
    }

    public void initializeApplication(Stage primaryStage) {
        window = new Window(primaryStage);
        initializeInput();
        screenController.showInitialScreen();
        primaryStage.show();
    }

    private void initializeInput() {
        GameInput gameInput = new GameInput(window);
        gameInput.setInputCallback(this::handleInput);
        LOGGER.info("Input initialized successfully");
    }

    public void handleInput() {
        System.out.println("Input received");
        // Handle input here
    }
}