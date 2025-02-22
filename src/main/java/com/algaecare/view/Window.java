package com.algaecare.view;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Window extends VBox {
    private Scene scene;
    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;

    public Window(Stage stage) {
        scene = new Scene(this, WINDOW_WIDTH, WINDOW_HEIGHT);
        configureStage(stage);
    }

    public Scene getMyScene() {
        return scene;
    }

    private void configureStage(Stage stage) {
        stage.setTitle("Algae Care");
        stage.setScene(scene);
        stage.setResizable(false);
    }

    public void mountView(View view) {
        getChildren().clear();
        getChildren().add(view);
    }
}