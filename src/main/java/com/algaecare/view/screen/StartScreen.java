package com.algaecare.view.screen;

import com.algaecare.controller.MainController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Objects;

public class StartScreen extends GameScreen {
    private ImageView backgroundImage;

    public StartScreen(MainController controller, Runnable onStartGame) {
        super(controller);
    }

    @Override
    protected void initialize() {
        initializeBackground();
    }

    private void initializeBackground() {
        try {
            Image startImage = new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("/images/screen_start.png")));
            backgroundImage = new ImageView(startImage);
            backgroundImage.setPreserveRatio(true);

            // Check if scene is available
            if (getScene() != null) {
                backgroundImage.fitWidthProperty().bind(getScene().widthProperty());
                backgroundImage.fitHeightProperty().bind(getScene().heightProperty());
            } else {
                // Add a listener so once the scene is set, bind the properties.
                sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                        backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
                    }
                });
            }
            getChildren().add(backgroundImage);
        } catch (Exception e) {
            System.err.println("Could not load start screen image: " + e.getMessage());
        }
    }
}
