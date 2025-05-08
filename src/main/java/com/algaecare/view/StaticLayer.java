package com.algaecare.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.util.Objects;

public class StaticLayer extends Layer {
    private static final Duration FADE_DURATION = Duration.seconds(4);
    private final ImageView imageView;
    private double currentOpacity = 1.0;

    public StaticLayer(int x, int y, String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            throw new IllegalArgumentException("Image path cannot be null or empty");
        }
        if (getClass().getResource(imagePath) == null) {
            throw new IllegalArgumentException("Image not found at path: " + imagePath);
        }
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("X and Y coordinates must be non-negative");
        }

        // Set up the image view
        Image image = new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm());

        imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(1920);
        imageView.setFitHeight(1080);
        imageView.setSmooth(true);
        imageView.setX(x);
        imageView.setY(y);

        getChildren().add(imageView);
    }

    public void setTargetOpacity(double newOpacity) {
        if (newOpacity < 0 || newOpacity > 1) {
            throw new IllegalArgumentException("Opacity must be between 0 and 1");
        }

        if (currentOpacity != newOpacity) {
            Timeline fadeAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(imageView.opacityProperty(), currentOpacity)),
                    new KeyFrame(FADE_DURATION,
                            new KeyValue(imageView.opacityProperty(), newOpacity)));
            fadeAnimation.setOnFinished(event -> currentOpacity = newOpacity);
            fadeAnimation.play();
        }
    }

    @Override
    public void showLayer() {
        setOpacity(1);
    }

    @Override
    public void hideLayer() {
        setOpacity(0);
    }
}