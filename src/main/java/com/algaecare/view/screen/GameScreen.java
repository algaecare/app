package com.algaecare.view.screen;

import javafx.scene.layout.VBox;
import com.algaecare.controller.MainController;

public abstract class GameScreen extends VBox {
    protected final MainController controller;

    public GameScreen(MainController controller) {
        this.controller = controller;
        initialize();
    }

    protected abstract void initialize();
}