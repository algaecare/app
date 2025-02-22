package com.algaecare.view;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.logging.Logger;

public abstract class View extends VBox {
    private static final Logger LOGGER = Logger.getLogger(View.class.getName());
    protected final ImageView backgroundImage;
    protected final String imagePath;

    protected View(String imagePath) {
        this.imagePath = imagePath;
        this.backgroundImage = new ImageView();
        setAlignment(Pos.CENTER);
        initializeBackground();
    }

    protected void initializeBackground() {
        try {
            Image image = new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));

            if (image.isError()) {
                throw new RuntimeException("Failed to load image: " + image.getException());
            }

            backgroundImage.setImage(image);
            backgroundImage.setPreserveRatio(true);

            // Set default size
            backgroundImage.setFitWidth(800);
            backgroundImage.setFitHeight(600);

            // Bind to scene size when available
            sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                    backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
                }
            });

            getChildren().add(backgroundImage);

        } catch (Exception e) {
            LOGGER.severe("Failed to load image: " + e.getMessage());
            throw new RuntimeException("View initialization failed", e);
        }
    }
}