package com.algaecare.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.algaecare.model.Environment;
import com.algaecare.model.GameState;
import com.algaecare.view.*;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ScreenController implements GameStateEventManager {
    private static final Logger LOGGER = Logger.getLogger(ScreenController.class.getName());
    private final List<Layer> layers = new ArrayList<>();
    private final AxolotlLayer axolotlLayer = new AxolotlLayer(1425, 610, 550, 505);
    private final Label gameStateDebugText = createGameStateDebugText();
    private final Label environmentLevelDebugText = createEnvironmentLevelDebugText();
    private final GameStateEventManager.EventEmitter stateEmitter;
    private Environment environment;
    private final StaticLayer environmentLayer = new StaticLayer(0, 0, "/30-ENVIRONMENT.png");
    private List<AlgaeLayer> allAlgaeLayers = new ArrayList<>();
    private List<AlgaeLayer> hiddenAlgaeLayers = new ArrayList<>();
    private List<AlgaeLayer> shownAlgaeLayers = new ArrayList<>();

    private Label createGameStateDebugText() {
        Label label = new Label();
        label.setStyle(
                "-fx-font-size: 20px; -fx-text-fill: white; -fx-background-color: black; -fx-padding: 10px; -fx-opacity: 1;");
        // Position the label at the bottom left corner
        label.setTranslateX(10); // 10px from the left
        label.setTranslateY(-10); // 10px from the bottom (will be set using StackPane alignment)
        StackPane.setAlignment(label, javafx.geometry.Pos.BOTTOM_LEFT);
        return label;
    }

    private Label createEnvironmentLevelDebugText() {
        Label label = new Label();
        label.setStyle(
                "-fx-font-size: 20px; -fx-text-fill: white; -fx-background-color: black; -fx-padding: 10px; -fx-opacity: 1;");
        // Position the label at the bottom left corner
        label.setTranslateX(-10); // 10px from the right
        label.setTranslateY(-10); // 10px from the bottom (will be set using StackPane alignment)
        StackPane.setAlignment(label, javafx.geometry.Pos.BOTTOM_RIGHT);
        return label;
    }

    public ScreenController(Stage stage, GameStateEventManager.EventEmitter stateEmitter, Environment environment) {
        this.stateEmitter = stateEmitter;
        this.environment = environment;
        MainScene scene = new MainScene(stage);
        initializeLayers();
        ((StackPane) scene.getScene().getRoot()).getChildren().addAll(layers);
        ((StackPane) scene.getScene().getRoot()).getChildren().add(gameStateDebugText);
        ((StackPane) scene.getScene().getRoot()).getChildren().add(environmentLevelDebugText);
        updateScreen(GameState.TITLE, GameState.TITLE);
    }

    private void initializeLayers() {
        // STATIC LAYERS 07 && 06
        layers.add(new StaticLayer(0, 0, "/07-LAYER.png"));
        layers.add(new StaticLayer(0, 91, "/06-LAYER.png"));
        // ITEM LAYERS
        layers.add(new ItemLayer(GameState.OBJECT_GARBAGE_BAG, 250, 358, "/20-ITEM-BAG.png"));
        layers.add(new ItemLayer(GameState.OBJECT_RECYCLING_BIN, 250, 335, "/20-ITEM-COMPOST.png"));
        layers.add(new ItemLayer(GameState.OBJECT_SHOPPING_BASKET_LOCAL, 250, 385, "/20-ITEM-LOCAL.png"));
        layers.add(new ItemLayer(GameState.OBJECT_SHOPPING_BASKET_INTERNATIONAL, 250, 385, "/20-ITEM-WORLD.png"));
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
        // ENVIRONMENT LAYER
        environmentLayer.hideLayer();
        layers.add(environmentLayer);
        // TEXT LAYERS
        layers.add(new TextLayer(GameState.TITLE, 1040, 220, 450, 125, "SUPERWATER_BIG"));
        layers.add(new TextLayer(GameState.SUBTITLE, 450, 100, 775, 350, "SUPERWATER_SMALL"));
        layers.add(new TextLayer(GameState.NOT_AXOLOTL, 1800, 480, 60, 55, "INTER"));
        layers.add(new TextLayer(GameState.AXOLOTL_INTRODUCTION, 1800, 435, 60, 55, "INTER"));
        layers.add(new TextLayer(GameState.OBJECT_GARBAGE_BAG, 1800, 300, 60, 55, "INTER"));
        layers.add(new TextLayer(GameState.OBJECT_RECYCLING_BIN, 1800, 300, 60, 55, "INTER"));
        layers.add(new TextLayer(GameState.OBJECT_CAR, 1800, 300, 60, 55, "INTER"));
        layers.add(new TextLayer(GameState.OBJECT_AIRPLANE, 1800, 250, 60, 55, "INTER"));
        layers.add(new TextLayer(GameState.OBJECT_TRAIN, 1800, 300, 60, 55, "INTER"));
        layers.add(new TextLayer(GameState.OBJECT_BICYCLE, 1800, 300, 60, 55, "INTER"));
        layers.add(new TextLayer(GameState.OBJECT_SHOPPING_BASKET_INTERNATIONAL, 1800, 250, 60, 55, "INTER"));
        layers.add(new TextLayer(GameState.OBJECT_SHOPPING_BASKET_LOCAL, 1800, 300, 60, 55, "INTER"));
        layers.add(new TextLayer(GameState.OBJECT_TRASH_GRABBER, 1800, 300, 60, 55, "INTER"));
        layers.add(new TextLayer(GameState.ENDSCREEN_NEGATIVE, 1800, 340, 60, 55, "INTER"));
        layers.add(new TextLayer(GameState.ENDSCREEN_POSITIVE, 1800, 250, 60, 55, "INTER"));
        layers.add(new TextLayer(GameState.GOODBYE, 1800, 300, 60, 55, "INTER"));
        // AXOLOTL LAYER
        layers.add(axolotlLayer);
        // ACTION LAYERS
        layers.add(new ActionLayer(GameState.OBJECT_BICYCLE, "/actions/bike/"));
        layers.add(new ActionLayer(GameState.OBJECT_CAR, "/actions/car/"));
        layers.add(new ActionLayer(GameState.OBJECT_AIRPLANE, "/actions/airplane/"));
        layers.add(new ActionLayer(GameState.OBJECT_TRAIN, "/actions/train/"));
        layers.add(new ActionLayer(GameState.OBJECT_TRASH_GRABBER, "/actions/trash-grabber/"));
        // SET TITLE LAYER AND ALGAE LAYER TO SHOW
        for (Layer layer : layers) {
            if (layer instanceof AlgaeLayer) {
                layer.showLayer();
                ((AlgaeLayer) layer).setSkipIntroAnimation(true);
            }
        }

        for (Layer layer : layers) {
            if (layer instanceof AlgaeLayer algaeLayer) {
                allAlgaeLayers.add(algaeLayer);
            }
        }

        updateScreen(GameState.TITLE, GameState.TITLE);
    }

    private void emitStateChange(GameState newState) {
        stateEmitter.emitGameStateChange(newState);
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        try {
            updateScreen(oldState, newState);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e,
                    () -> String.format("Error changing game state from %s to %s", oldState, newState));
        }
    }

    @Override
    public GameState getCurrentState() {
        throw new UnsupportedOperationException("ScreenController doesn't manage game state");
    }

    public void updateScreen(GameState oldState, GameState newState) {
        gameStateDebugText.setText("Current Game State: " + newState);
        environmentLevelDebugText.setText("Algae Level: " + environment.getAlgaeLevel() + "%");

        // Hide all layers first, except for static layers and algae layers
        for (Layer layer : layers) {
            boolean isStaticLayer = layer instanceof StaticLayer;
            boolean isAlgaeLayer = layer instanceof AlgaeLayer;
            if (!isStaticLayer && !isAlgaeLayer) {
                layer.hideLayer();
            }
        }

        if (!newState.name().startsWith("OBJECT_") && newState != GameState.GAMEPLAY) {
            environmentLayer.hideLayer();
        } else {
            if (newState == GameState.TITLE || newState == GameState.GOODBYE) {
                environment.reset();
            }
            if (newState.name().startsWith("OBJECT_")) {
                environment.updateEnvironment(newState);
            }
            environmentLayer.showLayer();

            // LOGIC FOR ALGAE LAYER
            int algaeLevel = environment.getAlgaeLevel();
            environmentLayer.setTargetOpacity(1 - algaeLevel / 100.0);
            showAlgaeLayersFromAlgaeLevel(algaeLevel);
            showAxolotlExpression(algaeLevel);
        }

        // Show only the relevant layers based on the new state
        switch (newState) {
            case TITLE -> {
                for (Layer layer : layers) {
                    boolean isTitle = layer instanceof TextLayer textLayer && textLayer.getID() == GameState.TITLE;
                    boolean isSubtitle = layer instanceof TextLayer textLayer
                            && textLayer.getID() == GameState.SUBTITLE;
                    if (isTitle || isSubtitle) {
                        layer.showLayer();
                    }
                }
            }

            case NOT_AXOLOTL -> {
                for (Layer layer : layers) {
                    boolean isAxolotlError = layer instanceof TextLayer textLayer
                            && textLayer.getID() == GameState.NOT_AXOLOTL;
                    if (isAxolotlError) {
                        layer.showLayer();
                    }
                }
            }

            case AXOLOTL_INTRODUCTION -> {
                for (Layer layer : layers) {
                    boolean isAxolotlIntroduction = layer instanceof TextLayer textLayer
                            && textLayer.getID() == GameState.AXOLOTL_INTRODUCTION;
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
                    if (layer instanceof AxolotlLayer axolotllayer) {
                        axolotllayer.showLayer();
                    }
                    if (layer instanceof TextLayer textLayer) {
                        if (textLayer.getID() == oldState) {
                            layer.showLayer();
                        }
                    }
                }
            }

            case OBJECT_GARBAGE_BAG, OBJECT_SHOPPING_BASKET_INTERNATIONAL, OBJECT_RECYCLING_BIN,
                    OBJECT_SHOPPING_BASKET_LOCAL -> {
                for (Layer layer : layers) {
                    if (layer instanceof ItemLayer itemLayer && itemLayer.getGameState() == newState) {
                        itemLayer.showLayer();
                        itemLayer.setOnAnimationComplete(() -> {
                            emitStateChange(GameState.GAMEPLAY);
                            itemLayer.hideLayer();
                        });
                    }

                    int algaeLevel = environment.getAlgaeLevel();
                    environmentLayer.setTargetOpacity(1 - algaeLevel / 100.0);
                }
            }

            case OBJECT_CAR, OBJECT_AIRPLANE, OBJECT_TRAIN, OBJECT_BICYCLE -> {
                for (Layer layer : layers) {
                    if (layer instanceof ActionLayer actionLayer) {
                        if (actionLayer.getGameState() == newState) {
                            actionLayer.showLayer();
                            actionLayer.setOnSequenceComplete(() -> {
                                emitStateChange(GameState.GAMEPLAY);
                                actionLayer.hideLayer();
                            });
                        }
                    }
                }
            }

            default -> LOGGER.log(Level.INFO, () -> String.format("Updating screen to state %s", newState));
        }
    }

    private void showAlgaeLayersFromAlgaeLevel(int algaeLevel) {
        // Calculate how many layers should be shown based on percentage
        int totalLayers = 13;
        int targetLayersToShow = (int) Math.round((algaeLevel / 100.0) * totalLayers);

        // If we need to show more layers than currently shown
        while (shownAlgaeLayers.size() < targetLayersToShow && !hiddenAlgaeLayers.isEmpty()) {
            // Pick a random hidden layer
            int randomIndex = (int) (Math.random() * hiddenAlgaeLayers.size());
            AlgaeLayer layerToShow = hiddenAlgaeLayers.remove(randomIndex);
            layerToShow.showLayer();
            shownAlgaeLayers.add(layerToShow);
        }

        // If we need to hide layers
        while (shownAlgaeLayers.size() > targetLayersToShow && !shownAlgaeLayers.isEmpty()) {
            // Pick a random shown layer
            int randomIndex = (int) (Math.random() * shownAlgaeLayers.size());
            AlgaeLayer layerToHide = shownAlgaeLayers.remove(randomIndex);
            layerToHide.hideLayer();
            hiddenAlgaeLayers.add(layerToHide);
        }

        // On first run, initialize the lists
        if (hiddenAlgaeLayers.isEmpty() && shownAlgaeLayers.isEmpty()) {
            for (AlgaeLayer layer : allAlgaeLayers) {
                if (shownAlgaeLayers.size() < targetLayersToShow) {
                    layer.showLayer();
                    shownAlgaeLayers.add(layer);
                } else {
                    layer.hideLayer();
                    hiddenAlgaeLayers.add(layer);
                }
            }
        }
    }

    private void showAxolotlExpression(int algaeLevel) {
        if (algaeLevel < 75) {
            axolotlLayer.setExpression(AxolotlLayer.Expression.HAPPY);
        } else if (algaeLevel < 50) {
            axolotlLayer.setExpression(AxolotlLayer.Expression.BAD);
        } else if (algaeLevel < 25) {
            axolotlLayer.setExpression(AxolotlLayer.Expression.WORSE);
        } else {
            axolotlLayer.setExpression(AxolotlLayer.Expression.WORST);
        }
    }
}
