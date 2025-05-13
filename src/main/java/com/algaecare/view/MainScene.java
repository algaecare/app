package com.algaecare.view;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainScene {
    public static final int WINDOW_WIDTH = 1920;
    public static final int WINDOW_HEIGHT = 1080;

    private final Scene scene;

    public MainScene(Stage stage) {
        // Configure Stage
        stage.setTitle("Algae Care");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setFullScreen(true);

        // Set minimum, maximum and preferred size to force dimensions
        stage.setMinWidth(WINDOW_WIDTH);
        stage.setMaxWidth(WINDOW_WIDTH);
        stage.setMinHeight(WINDOW_HEIGHT);
        stage.setMaxHeight(WINDOW_HEIGHT);

        // Add root pane with fixed size
        StackPane root = new StackPane();
        root.setMinSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.setMaxSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        // Create the base scene with fixed size
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getRoot().setStyle("-fx-pref-width: " + WINDOW_WIDTH + "px; " +
                "-fx-pref-height: " + WINDOW_HEIGHT + "px; " +
                "-fx-min-width: " + WINDOW_WIDTH + "px; " +
                "-fx-min-height: " + WINDOW_HEIGHT + "px; " +
                "-fx-max-width: " + WINDOW_WIDTH + "px; " +
                "-fx-max-height: " + WINDOW_HEIGHT + "px;");

        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    public Scene getScene() {
        return scene;
    }
}