package com.algaecare.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.algaecare.model.GameState;
import com.algaecare.view.*;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ScreenController implements GameStateEventManager {
    private static final Logger LOGGER = Logger.getLogger(ScreenController.class.getName());
    private final List<Layer> layers = new ArrayList<>();
    private final AxolotlLayer axolotlLayer = new AxolotlLayer(1425, 610, 550, 505);
    private final Label debugText = createDebugText();
    private final GameStateEventManager.EventEmitter stateEmitter;

    private Label createDebugText() {
        Label label = new Label();
        label.setStyle(
                "-fx-font-size: 20px; -fx-text-fill: white; -fx-background-color: black; -fx-padding: 10px; -fx-opacity: 1;");
        // Position the label at the bottom left corner
        label.setTranslateX(10); // 10px from the left
        label.setTranslateY(-10); // 10px from the bottom (will be set using StackPane alignment)
        StackPane.setAlignment(label, javafx.geometry.Pos.BOTTOM_LEFT);
        return label;
    }

    public ScreenController(Stage stage, GameStateEventManager.EventEmitter stateEmitter) {
        this.stateEmitter = stateEmitter;
        MainScene scene = new MainScene(stage);
        initializeLayers();
        ((StackPane) scene.getScene().getRoot()).getChildren().addAll(layers);
        ((StackPane) scene.getScene().getRoot()).getChildren().add(debugText);
        updateScreen(GameState.TITLE);
    }

    private void initializeLayers() {
        // STATIC LAYERS 07 && 06
        layers.add(new StaticLayer(0, 0, "/07-LAYER.png"));
        layers.add(new StaticLayer(0, 91, "/06-LAYER.png"));
        // ITEM LAYERS
        layers.add(new ItemLayer(GameState.TRASH, 250, 358, "/20-ITEM-BAG.png"));
        layers.add(new ItemLayer(GameState.RECYCLING, 250, 335, "/20-ITEM-COMPOST.png"));
        layers.add(new ItemLayer(GameState.SHOPPING_BAG_LOCAL, 250, 385, "/20-ITEM-LOCAL.png"));
        layers.add(new ItemLayer(GameState.SHOPPING_BAG_WORLD, 250, 385, "/20-ITEM-WORLD.png"));
        // ALGAE LAYERS 05
        layers.add(new AlgaeLayer(1205, 640, 190, 225,
                "/05-LAYER-CORAL-1.png"));
        layers.add(new AlgaeLayer(1660, 370, 120, 175,
                "/05-LAYER-CORAL-2.png"));
        layers.add(new AlgaeLayer(120, 390, 190, 300,
                "/05-LAYER-CORAL-3.png"));
        layers.add(new AlgaeLayer(1445, 405, 190, 300,
                "/05-LAYER-CORAL-4.png"));
        layers.add(new AlgaeLayer(365, 615, 205, 240,
                "/05-LAYER-CORAL-5.png"));
        layers.add(new AlgaeLayer(830, 560, 335, 330,
                "/05-LAYER-CORAL-6.png"));
        layers.add(new AlgaeLayer(605, 705, 155, 165,
                "/05-LAYER-CORAL-7.png"));
        // STATIC LAYER 04
        layers.add(new StaticLayer(0, 237, "/04-LAYER.png"));
        // ALGAE LAYERS 03
        layers.add(new AlgaeLayer(1545, 700, 130, 70,
                "/03-LAYER-CORAL-1.png"));
        layers.add(new AlgaeLayer(210, 705, 245, 235,
                "/03-LAYER-CORAL-2.png"));
        layers.add(new AlgaeLayer(800, 765, 170, 220,
                "/03-LAYER-CORAL-3.png"));
        layers.add(new AlgaeLayer(480, 775, 135, 200,
                "/03-LAYER-CORAL-4.png"));
        layers.add(new AlgaeLayer(1285, 670, 170, 250,
                "/03-LAYER-CORAL-5.png"));
        layers.add(new AlgaeLayer(1075, 805, 175, 145,
                "/03-LAYER-CORAL-6.png"));
        // STATIC LAYER 02 && 01
        layers.add(new StaticLayer(0, 225, "/02-LAYER.png"));
        layers.add(new StaticLayer(0, 353, "/01-LAYER.png"));
        // TEXT LAYERS
        layers.add(new TextLayer(TextId.TITLE, 1040, 220, 450, 125, "SUPERWATER_BIG"));
        layers.add(new TextLayer(TextId.SUBTITLE, 450, 100, 775, 350, "SUPERWATER_SMALL"));
        layers.add(new TextLayer(TextId.NOT_AXOLOTL, 1800, 480, 60, 55, "INTER"));
        layers.add(new TextLayer(TextId.AXOLOTL_INTRODUCTION, 1800, 540, 60, 55, "INTER"));
        layers.add(new TextLayer(TextId.NOT_OBJECT, 1800, 540, 60, 55, "INTER"));
        layers.add(new TextLayer(TextId.OBJECT_GARBAGE_BAG, 1800, 355, 60, 55, "INTER"));
        layers.add(new TextLayer(TextId.OBJECT_RECYCLING_BIN, 1800, 415, 60, 55, "INTER"));
        layers.add(new TextLayer(TextId.OBJECT_CAR, 1800, 415, 60, 55, "INTER"));
        layers.add(new TextLayer(TextId.OBJECT_AIRPLANE, 1800, 355, 60, 55, "INTER"));
        layers.add(new TextLayer(TextId.OBJECT_TRAIN, 1800, 355, 60, 55, "INTER"));
        layers.add(new TextLayer(TextId.OBJECT_BICYCLE, 1800, 355, 60, 55, "INTER"));
        layers.add(new TextLayer(TextId.OBJECT_SHOPPING_BASKET_INTERNATIONAL, 1800, 355, 60, 55, "INTER"));
        layers.add(new TextLayer(TextId.OBJECT_SHOPPING_BASKET_LOCAL, 1800, 415, 60, 55, "INTER"));
        layers.add(new TextLayer(TextId.ENDSCREEN_NEGATIVE, 1800, 480, 60, 55, "INTER"));
        layers.add(new TextLayer(TextId.ENDSCREEN_POSITIVE, 1800, 355, 60, 55, "INTER"));
        layers.add(new TextLayer(TextId.GOODBYE, 1800, 415, 60, 55, "INTER"));


        // AXOLOTL LAYER
        layers.add(axolotlLayer);

        // SET TITLE LAYER AND ALGAE LAYER TO SHOW
        for (Layer layer : layers) {
            if (layer instanceof AlgaeLayer) {
                layer.showLayer();
                ((AlgaeLayer) layer).setSkipIntroAnimation(true);
            }
        }
        updateScreen(GameState.TITLE);
    }

    private void emitStateChange(GameState newState) {
        LOGGER.info("Screen emitting state change to: " + newState);
        stateEmitter.emitGameStateChange(newState);
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        try {
            updateScreen(newState);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e,
                    () -> String.format("Error changing game state from %s to %s", oldState, newState));
        }
    }

    @Override
    public GameState getCurrentState() {
        throw new UnsupportedOperationException("ScreenController doesn't manage game state");
    }

    public void updateScreen(GameState newState) {
        debugText.setText("Current Game State: " + newState);
        // Hide all layers first, except for static layers and algae layers
        for (Layer layer : layers) {
            boolean isStaticLayer = layer instanceof StaticLayer;
            boolean isAlgaeLayer = layer instanceof AlgaeLayer;
            if (!isStaticLayer && !isAlgaeLayer) {
                layer.hideLayer();
            }
        }
        // Show only the relevant layers based on the new state
        switch (newState) {
            case TITLE -> {
                for (Layer layer : layers) {
                    boolean isTitle = layer instanceof TextLayer textLayer && textLayer.getID() == TextId.TITLE;
                    boolean isSubtitle = layer instanceof TextLayer textLayer && textLayer.getID() == TextId.SUBTITLE;
                    if (isTitle || isSubtitle) {
                        layer.showLayer();
                    }
                }
            }

            case AXOLOTL_ERROR -> {
                for (Layer layer : layers) {
                    boolean isAxolotlError = layer instanceof TextLayer textLayer
                            && textLayer.getID() == TextId.NOT_AXOLOTL;
                    if (isAxolotlError) {
                        layer.showLayer();
                    }
                }
            }

            case OPENING -> {
                for (Layer layer : layers) {
                    boolean isAxolotlIntroduction = layer instanceof TextLayer textLayer
                            && textLayer.getID() == TextId.AXOLOTL_INTRODUCTION;
                    if (isAxolotlIntroduction) {
                        layer.showLayer();
                    }
                    if (layer instanceof AxolotlLayer axolotllayer) {
                        axolotllayer.showLayer();
                        axolotllayer.setExpression(AxolotlLayer.Expression.HAPPY);
                    }
                }
            }

            case GAMEPLAY -> {
                for (Layer layer : layers) {
                    break;
                }
            }

            case TRASH, SHOPPING_BAG_WORLD, RECYCLING, SHOPPING_BAG_LOCAL -> {
                for (Layer layer : layers) {
                    if (layer instanceof ItemLayer itemLayer) {
                        if (itemLayer.getGameState() == newState) {
                            itemLayer.showLayer();
                            itemLayer.setOnAnimationComplete(() -> {
                                emitStateChange(GameState.GAMEPLAY);
                                itemLayer.hideLayer();
                            });
                        }
                    }
                }
            }

            default -> LOGGER.log(Level.INFO, () -> String.format("Updating screen to state %s", newState));
        }
    }
}