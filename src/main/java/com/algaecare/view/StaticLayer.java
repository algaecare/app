package com.algaecare.view;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Objects;

public class StaticLayer extends Layer {
    private final ColorAdjust colorAdjust;

    public StaticLayer(int x, int y, String imagePath) {
        super(LayerType.STATIC);

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

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(1920);
        imageView.setFitHeight(1080);
        imageView.setSmooth(true);
        imageView.setX(x);
        imageView.setY(y);

        colorAdjust = new ColorAdjust();
        imageView.setEffect(colorAdjust);

        getChildren().add(imageView);
    }

    public void setHue(double hue) {
        colorAdjust.setHue(hue);
    }

    public double getHue() {
        return colorAdjust.getHue();
    }

    public void setSaturation(double saturation) {
        colorAdjust.setSaturation(saturation);
    }

    public double getSaturation() {
        return colorAdjust.getSaturation();
    }
}