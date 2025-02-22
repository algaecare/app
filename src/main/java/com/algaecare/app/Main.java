package com.algaecare.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import com.algaecare.controller.MainController;

// Main class for the Algae Care application
// This class initializes the JavaFX application and sets up the main controller.
public class Main extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String LOG_FILE = "app.log";

    // region Main Method
    // This is the entry point of the JavaFX application.
    // It configures logging, system properties, and launches the application.
    public static void main(String[] args) {
        configureLogging();
        configureSystemProperties();
        launch(args);
    }
    // endregion

    // region Application Methods
    // This method is called when the application starts.
    // It initializes the main controller and starts the application.
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
    // endregion

    // region Private Methods
    // This method configures system properties for the JavaFX application.
    private static void configureSystemProperties() {
        System.setProperty("javafx.macosx.enableRetinaScale", "true");
        System.setProperty("prism.verbose", "false");
        System.setProperty("javafx.verbose", "false");
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }

    // This method configures macOS-specific properties for the JavaFX application.
    private void configureMacOSProperties() {
        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("apple.awt.application.name", "Algae Care");
    }

    // This method configures logging for the application.
    private static void configureLogging() {
        try {
            // Create logs directory if it doesn't exist
            Files.createDirectories(Path.of("logs"));

            // Create FileHandler
            FileHandler fileHandler = new FileHandler("logs/" + LOG_FILE, true);

            // Configure log format
            fileHandler.setFormatter(new SimpleFormatter());

            // Get the root logger and add file handler
            Logger rootLogger = Logger.getLogger("");
            rootLogger.addHandler(fileHandler);

            // Set logging level
            rootLogger.setLevel(Level.INFO);

            LOGGER.info("Logging initialized");
        } catch (IOException e) {
            System.err.println("Could not initialize logging: " + e.getMessage());
        }
    }
    // endregion
}