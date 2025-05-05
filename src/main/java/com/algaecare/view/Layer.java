package com.algaecare.view;

import javafx.scene.layout.StackPane;

public abstract class Layer extends StackPane {
    public Layer() {
        setPickOnBounds(false);
        setAlignment(javafx.geometry.Pos.TOP_LEFT);
    }

    public void showLayer() {
        setVisible(true);
    }

    public void hideLayer() {
        setVisible(false);
    }
}