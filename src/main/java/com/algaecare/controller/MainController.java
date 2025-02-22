package com.algaecare.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import com.algaecare.view.Window;
import com.algaecare.controller.input.InputController;
import com.algaecare.controller.input.KeyboardController;
import com.algaecare.controller.input.RFIDController;
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

    public MainController() {
        this.screenController = new ScreenController(this);
    }

    public Window getMainView() {
        return window;
    }

    public void initializeApplication(Stage primaryStage) {
        window = new Window(primaryStage);
        initializeInput(primaryStage.getScene());
        screenController.showInitialScreen();
        primaryStage.show();
    }

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
            controller.setInputCallback(action -> handleInput(action));
        });

        LOGGER.info("Input controllers initialized");
    }

    public void handleInput(InputAction action) {
        System.out.println("Input received: " + action);
        // Handle input here
    }

    private boolean isRaspberryPi() {
        return System.getProperty("os.name").toLowerCase().contains("linux")
                && System.getProperty("os.arch").toLowerCase().contains("arm");
    }
}