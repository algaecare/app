package com.algaecare.app;

import com.algaecare.view.MainView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Set Mac-specific properties
        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("apple.awt.application.name", "Algae Care");

        // Initialize view in Platform.runLater to avoid Mac activation timeout
        Platform.runLater(() -> {
            MainView mainView = new MainView();
            Scene scene = new Scene(mainView);
            mainView.initializeInput(scene);
            resize(primaryStage);
            // primaryStage.setFullScreen(true);
            primaryStage.setTitle("Algae Care");
            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }

    public static void main(String[] args) {
        // Set properties before launching
        System.setProperty("javafx.macosx.enableRetinaScale", "true");
        System.setProperty("prism.verbose", "false"); // Reduce JavaFX logging
        System.setProperty("javafx.verbose", "false"); // Reduce JavaFX logging
        launch(args);
    }

    public static void resize(Stage primaryStage) {
        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);
        primaryStage.setResizable(false);
    }
}