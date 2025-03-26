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
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Algae Care");
        stage.setResizable(false);
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);

        // Add root pane
        StackPane root = new StackPane();
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        // Create the base scene
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    public Scene getScene() {
        return scene;
    }
}