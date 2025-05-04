package com.algaecare.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.algaecare.model.GameState;
import com.algaecare.model.TextLayerData;
import com.algaecare.view.MainScene;
import com.algaecare.view.layer.AlgaeLayer;
import com.algaecare.view.layer.Layer;
import com.algaecare.view.layer.StaticLayer;
import com.algaecare.view.layer.TextLayer;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ScreenController implements GameStateChangeListener {
    private static final Logger LOGGER = Logger.getLogger(ScreenController.class.getName());
    private final List<Layer> layers = new ArrayList<>();
    private final List<AlgaeLayer> coralLayers = new ArrayList<>();
    private final List<TextLayer> titleTextLayers = new ArrayList<>();
    private TextLayer notAxolotlLayer;
    private final MainScene scene;

    public ScreenController(MainController controller, Stage stage) {
        this.scene = new MainScene(stage);
        initializeLayers();
        ((StackPane) scene.getScene().getRoot()).getChildren().addAll(layers);
    }

    private void initializeLayers() {
        StaticLayer groundLayerNine = new StaticLayer(0, 0, "/static/09-LAYER.png");
        layers.add(groundLayerNine);

        StaticLayer groundLayerEight = new StaticLayer(0, 91, "/static/08-LAYER.png");
        layers.add(groundLayerEight);

        addCoralThreeLayer();

        StaticLayer groundLayerFour = new StaticLayer(0, 237, "/static/04-LAYER.png");
        layers.add(groundLayerFour);

        addCoralFiveLayer();

        StaticLayer groundLayerTwo = new StaticLayer(0, 225, "/static/02-LAYER.png");
        layers.add(groundLayerTwo);

        StaticLayer groundLayerOne = new StaticLayer(0, 353, "/static/01-LAYER.png");
        layers.add(groundLayerOne);

        // StaticLayer sheenLayer = new StaticLayer(0, 0, "/static/00-LAYER.png");
        // layers.add(sheenLayer);

        TextLayer titleTextLayer = new TextLayer("TITLE", 1040, 220, 450, 125, TextLayerData.getText("TITLE"),
                "SUPERWATER_BIG");
        layers.add(titleTextLayer);
        titleTextLayers.add(titleTextLayer);
        titleTextLayer.showLayer();

        TextLayer subtitleTextLayer = new TextLayer("SUBTITLE", 450, 100, 775, 350, TextLayerData.getText("SUBTITLE"),
                "SUPERWATER_SMALL");
        layers.add(subtitleTextLayer);
        titleTextLayers.add(subtitleTextLayer);
        subtitleTextLayer.showLayer();

        notAxolotlLayer = new TextLayer("NOT_AXOLOTL", 1800, 325, 40, 720,
                TextLayerData.getText("NOT_AXOLOTL"),
                "INTER");
        layers.add(notAxolotlLayer);
    }

    public void addCoralThreeLayer() {
        AlgaeLayer coralOneLayerFive = new AlgaeLayer(1205, 640, 190, 225,
                "/static/05-LAYER-CORAL-1.png");
        layers.add(coralOneLayerFive);
        coralLayers.add(coralOneLayerFive);
        coralOneLayerFive.showLayer();

        AlgaeLayer coralTwoLayerFive = new AlgaeLayer(1660, 370, 120, 175,
                "/static/05-LAYER-CORAL-2.png");
        layers.add(coralTwoLayerFive);
        coralLayers.add(coralTwoLayerFive);
        coralTwoLayerFive.showLayer();

        AlgaeLayer coralThreeLayerFive = new AlgaeLayer(120, 390, 190, 300,
                "/static/05-LAYER-CORAL-3.png");
        layers.add(coralThreeLayerFive);
        coralLayers.add(coralThreeLayerFive);
        coralThreeLayerFive.showLayer();

        AlgaeLayer coralFourLayerFive = new AlgaeLayer(1445, 405, 190, 300,
                "/static/05-LAYER-CORAL-4.png");
        layers.add(coralFourLayerFive);
        coralLayers.add(coralFourLayerFive);
        coralFourLayerFive.showLayer();

        AlgaeLayer coralFiveLayerFive = new AlgaeLayer(365, 615, 205, 240,
                "/static/05-LAYER-CORAL-5.png");
        layers.add(coralFiveLayerFive);
        coralLayers.add(coralFiveLayerFive);
        coralFiveLayerFive.showLayer();

        AlgaeLayer coralSixLayerFive = new AlgaeLayer(830, 560, 335, 330,
                "/static/05-LAYER-CORAL-6.png");
        layers.add(coralSixLayerFive);
        coralLayers.add(coralSixLayerFive);
        coralSixLayerFive.showLayer();

        AlgaeLayer coralSevenLayerFive = new AlgaeLayer(605, 705, 155, 165,
                "/static/05-LAYER-CORAL-7.png");
        layers.add(coralSevenLayerFive);
        coralLayers.add(coralSevenLayerFive);
        coralSevenLayerFive.showLayer();
    }

    public void addCoralFiveLayer() {
        AlgaeLayer coralOneLayerThree = new AlgaeLayer(1545, 700, 130, 70,
                "/static/03-LAYER-CORAL-1.png");
        layers.add(coralOneLayerThree);
        coralLayers.add(coralOneLayerThree);
        coralOneLayerThree.showLayer();

        AlgaeLayer coralTwoLayerThree = new AlgaeLayer(210, 705, 245, 235,
                "/static/03-LAYER-CORAL-2.png");
        layers.add(coralTwoLayerThree);
        coralLayers.add(coralTwoLayerThree);
        coralTwoLayerThree.showLayer();

        AlgaeLayer coralThreeLayerThree = new AlgaeLayer(800, 765, 170, 220,
                "/static/03-LAYER-CORAL-3.png");
        layers.add(coralThreeLayerThree);
        coralLayers.add(coralThreeLayerThree);
        coralThreeLayerThree.showLayer();

        AlgaeLayer coralFourLayerThree = new AlgaeLayer(480, 775, 135, 200,
                "/static/03-LAYER-CORAL-4.png");
        layers.add(coralFourLayerThree);
        coralLayers.add(coralFourLayerThree);
        coralFourLayerThree.showLayer();

        AlgaeLayer coralFiveLayerThree = new AlgaeLayer(1285, 670, 170, 250,
                "/static/03-LAYER-CORAL-5.png");
        layers.add(coralFiveLayerThree);
        coralLayers.add(coralFiveLayerThree);
        coralFiveLayerThree.showLayer();

        AlgaeLayer coralSixLayerThree = new AlgaeLayer(1075, 805, 175, 145,
                "/static/03-LAYER-CORAL-6.png");
        layers.add(coralSixLayerThree);
        coralLayers.add(coralSixLayerThree);
        coralSixLayerThree.showLayer();
    }

    public void updateScreen(GameState newState) {
        notAxolotlLayer.hideLayer();
        switch (newState) {
            case TITLE -> {
                for (AlgaeLayer layer : coralLayers) {
                    layer.showLayer();
                }
                for (TextLayer layer : titleTextLayers) {
                    layer.showLayer();
                }
            }

            case AXOLOTL_ERROR -> {
                notAxolotlLayer.showLayer();
            }

            default -> {
                for (AlgaeLayer layer : coralLayers) {
                    layer.hideLayer();
                }
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