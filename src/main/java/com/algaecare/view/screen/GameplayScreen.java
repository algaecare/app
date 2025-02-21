package com.algaecare.view.screen;

import com.algaecare.controller.MainController;

import javafx.scene.control.Label;

public class GameplayScreen extends GameScreen {

    public GameplayScreen(MainController controller) {
        super(controller);
    }

    @Override
    protected void initialize() {
        Label gameLabel = new Label("Game is running...");
        getChildren().add(gameLabel);
    }
}