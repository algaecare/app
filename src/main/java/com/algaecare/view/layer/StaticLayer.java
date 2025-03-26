package com.algaecare.view.layer;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Objects;

public class StaticLayer extends Layer {
    private final ColorAdjust colorAdjust;

    public StaticLayer(String imagePath) {
        super(LayerType.STATIC);

        if (imagePath == null || imagePath.isEmpty()) {
            throw new IllegalArgumentException("Image path cannot be null or empty");
        }
        Image image = new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm());

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(1920);
        imageView.setFitHeight(1080);

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