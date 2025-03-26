package com.algaecare.view.layer;

import javafx.scene.layout.StackPane;

public abstract class Layer extends StackPane {
    public enum LayerType {
        STATIC, DYNAMIC
    }

    private final LayerType layerType;

    public Layer(LayerType type) {
        this.layerType = type;
        setHeight(Double.MAX_VALUE);
        setWidth(Double.MAX_VALUE);
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);
    }

    public LayerType getLayerType() {
        return layerType;
    }
}