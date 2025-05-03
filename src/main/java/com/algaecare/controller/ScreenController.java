package com.algaecare.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.algaecare.model.GameState;
import com.algaecare.model.TextLayerData;
import com.algaecare.view.MainScene;
import com.algaecare.view.layer.DynamicLayer;
import com.algaecare.view.layer.Layer;
import com.algaecare.view.layer.StaticLayer;
import com.algaecare.view.layer.TextLayer;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ScreenController implements GameStateChangeListener {
    private static final Logger LOGGER = Logger.getLogger(ScreenController.class.getName());
    private final List<Layer> layers;
    private final MainScene scene;

    public ScreenController(MainController controller, Stage stage) {
        this.scene = new MainScene(stage);
        this.layers = new ArrayList<>();
        initializeLayers();
        ((StackPane) scene.getScene().getRoot()).getChildren().addAll(layers);
    }

    private void initializeLayers() {
        // Initialize static layers
        StaticLayer backdropLayer = new StaticLayer("/static/5-BACKDROP.png");
        StaticLayer backgroundLayer = new StaticLayer("/static/4-BACKGROUND.png");
        StaticLayer layerThree = new StaticLayer("/static/3-LAYER-3.png");
        StaticLayer layerTwo = new StaticLayer("/static/2-LAYER-2.png");
        StaticLayer layerOne = new StaticLayer("/static/1-LAYER-1.png");

        // Initialize dynamic layers
        DynamicLayer axolotl = new DynamicLayer("dynamic/axolotl");

        TextLayer titleTextLayer = new TextLayer(1040, 220, 450, 150, TextLayerData.getText("TITLE"), "SUPERWATER_BIG");
        TextLayer subtitleTextLayer = new TextLayer(380, 100, 800, 390, TextLayerData.getText("SUBTITLE"),
                "SUPERWATER_SMALL");

        // Initialize text layers from JSON
        layers.add(backdropLayer);
        layers.add(backgroundLayer);
        layers.add(layerThree);
        layers.add(layerTwo);
        layers.add(layerOne);
        layers.add(axolotl);
        layers.add(titleTextLayer);
        layers.add(subtitleTextLayer);
    }

    public void updateScreen(GameState newState) {
        for (Layer layer : layers) {
            if (layer instanceof DynamicLayer) {
                ((DynamicLayer) layer).showLayer();
            }
        }
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        try {
            updateScreen(newState);
            return;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error changing game state from " + oldState + " to " + newState, e);
        }
    }
}