package com.algaecare.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.algaecare.controller.MainController;

// Main class for the Algae Care application
// This class initializes the JavaFX application and sets up the main controller.
public class Main extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        configureSystemProperties();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            configureMacOSProperties();
            MainController gameController = new MainController();
            gameController.initializeApplication(primaryStage);
            LOGGER.info("Application started successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize application", e);
            Platform.exit();
        }
    }

    private static void configureSystemProperties() {
        System.setProperty("javafx.macosx.enableRetinaScale", "true");
        System.setProperty("prism.verbose", "false");
        System.setProperty("javafx.verbose", "false");
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }

    private void configureMacOSProperties() {
        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("apple.awt.application.name", "Algae Care");
    }
}