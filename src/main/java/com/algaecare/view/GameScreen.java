package com.algaecare.view;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;

import java.util.logging.Logger;
import java.util.logging.Level;

import com.algaecare.model.Environment;

public class GameScreen extends Screen {
    private static final Logger LOGGER = Logger.getLogger(GameScreen.class.getName());

    private final Rectangle colorOverlay;
    private final ColorAdjust colorAdjust;

    public GameScreen(String videoPath) {
        super(videoPath);
        this.colorAdjust = new ColorAdjust();
        this.colorOverlay = createColorOverlay();
        initializeOverlay();
    }

    private Rectangle createColorOverlay() {
        Rectangle overlay = new Rectangle();
        overlay.setFill(Color.TRANSPARENT);
        overlay.setOpacity(0.75); // Adjust opacity as needed

        // Bind overlay size to parent size
        overlay.widthProperty().bind(this.widthProperty());
        overlay.heightProperty().bind(this.heightProperty());

        return overlay;
    }

    private void initializeOverlay() {
        StackPane videoContainer = (StackPane) this.getChildren().get(0);
        videoContainer.getChildren().add(colorOverlay);
    }

    @Override
    protected void configureMediaPlayer() {
        if (mediaPlayer == null) {
            LOGGER.severe("MediaPlayer not initialized");
            return;
        }

        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    public void updateEnvironmentDisplay(Environment environment) {
        // Calculate tint based on environment values
        double co2Tint = environment.getCo2Level() / 100.0;
        double o2Tint = environment.getO2Level() / 100.0;
        double algaeTint = environment.getAlgaeLevel() / 100.0;
        double tempTint = (environment.getTemperature() - 15) / 30.0; // Assuming 15-45°C range

        // Create a color blend based on environmental factors
        Color tintColor = Color.rgb(
                (int) (255 * co2Tint), // Red for CO2
                (int) (255 * algaeTint), // Green for algae
                (int) (255 * o2Tint), // Blue for oxygen
                0.3 // Alpha (transparency)
        );

        // Apply color adjustments
        colorAdjust.setBrightness(-0.3 * tempTint); // Darker when hot
        colorAdjust.setSaturation(0.5 * algaeTint); // More saturated with more algae

        // Update the overlay color
        colorOverlay.setFill(tintColor);

        LOGGER.info(String.format("Updated environment display - CO2: %d%%, O2: %d%%, Algae: %d%%, Temp: %d°C",
                environment.getCo2Level(),
                environment.getO2Level(),
                environment.getAlgaeLevel(),
                environment.getTemperature()));
    }

    @Override
    protected void handleInitializationError(Exception e) {
        LOGGER.log(Level.SEVERE, "Failed to initialize game screen", e);
    }

    protected Rectangle getColorOverlay() {
        return colorOverlay;
    }
}