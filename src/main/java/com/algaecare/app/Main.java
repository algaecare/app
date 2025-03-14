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

public class Main extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String LOG_FILE = "app.log";

    public static void main(String[] args) {
        configureLogging();
        configureSystemProperties();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            configureMacOSProperties();
            new MainController(primaryStage);
            primaryStage.show();
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

    void configureMacOSProperties() {
        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("apple.awt.application.name", "Algae Care");
    }

    private static void configureLogging() {
        try {
            Files.createDirectories(Path.of("logs"));
            FileHandler fileHandler = new FileHandler("logs/" + LOG_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
            Logger rootLogger = Logger.getLogger("");
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("Could not initialize logging: " + e.getMessage());
        }
    }
}