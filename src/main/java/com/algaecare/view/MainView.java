package com.algaecare.view;

import com.algaecare.controller.MainController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class MainView extends VBox {
    private final MainController controller;
    private ImageView startScreen;

    public MainView() {
        controller = new MainController();
        initializeUI();
        showStartScreen();
    }

    private void initializeUI() {
        initializeStartScreen();
    }

    private void showStartScreen() {
        getChildren().clear();
        getChildren().add(startScreen);

        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> {
            controller.startGame();
            showGameScreen(); // New method to show game UI
        });

        getChildren().add(startButton);
    }

    private void showGameScreen() {
        getChildren().clear();
        Label gameLabel = new Label("Game is running...");
        getChildren().add(gameLabel);
    }

    private void initializeStartScreen() {
        try {
            Image startImage = new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("/images/screen_start.png")));
            startScreen = new ImageView(startImage);
            startScreen.fitWidthProperty().bind(this.widthProperty());
            startScreen.fitHeightProperty().bind(this.heightProperty());
            startScreen.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load start screen image: " + e.getMessage());
            startScreen = new ImageView();
        }
    };
}