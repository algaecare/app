package com.algaecare.view;

import com.algaecare.model.GameState;
import com.algaecare.model.TextLayerData;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Animation;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.scene.text.Font;

public class TextLayer extends Layer {
    private enum FontType {
        INTER, SUPERWATER_BIG, SUPERWATER_SMALL
    }

    private final GameState id;
    private final Label textLabel;
    private final int width;
    private final int height;
    private final int x;
    private final int y;
    private final FontType fontType;

    public TextLayer(GameState id, int width, int height, int x, int y, String fontType) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.fontType = FontType.valueOf(fontType.toUpperCase());
        this.textLabel = new Label(TextLayerData.getText(String.valueOf(id)));
        initializeLayer();
        hideLayer();
    }

    private void initializeLayer() {
        // Layer position
        setPrefSize(1920, 1080);
        setMaxSize(1920, 1080);
        setMinSize(1920, 1080);
        setAlignment(Pos.TOP_LEFT);

        // Create and style the label with specific size
        textLabel.setPrefSize(width, height);
        textLabel.setMaxSize(width, height);
        textLabel.setMinSize(width, height);

        // Position the label within the layer
        textLabel.setTranslateX(x);
        textLabel.setTranslateY(y);

        CornerRadii cornerRadii = new CornerRadii(10);
        BackgroundFill backgroundFill = new BackgroundFill(null, cornerRadii, Insets.EMPTY);

        // Load the font
        if (fontType == FontType.INTER) {
            Font font = Font.loadFont(getClass().getResourceAsStream("/Inter_24pt-Medium.ttf"), 24);
            textLabel.setFont(Font.font(font.getFamily(), javafx.scene.text.FontWeight.BOLD, 40));
            textLabel.setTextFill(Color.BLACK);
            backgroundFill = new BackgroundFill(Color.WHITE, cornerRadii, Insets.EMPTY);
        } else if (fontType == FontType.SUPERWATER_BIG) {
            Font.loadFont(getClass().getResourceAsStream("/SuperWater.ttf"), 170);
            textLabel.setTextFill(Color.rgb(204, 243, 255));
            textLabel.setStyle("-fx-font-family: 'Super Water'; -fx-font-size: 170px; -fx-font-weight: normal;");
            backgroundFill = new BackgroundFill(Color.TRANSPARENT, cornerRadii, Insets.EMPTY);
        } else if (fontType == FontType.SUPERWATER_SMALL) {
            Font.loadFont(getClass().getResourceAsStream("/SuperWater.ttf"), 40);
            textLabel.setTextFill(Color.rgb(204, 243, 255));
            textLabel.setStyle("-fx-font-family: 'Super Water'; -fx-font-size: 40px; -fx-font-weight: normal;");
            backgroundFill = new BackgroundFill(Color.TRANSPARENT, cornerRadii, Insets.EMPTY);
        }

        // 80s blinking animation
        if (fontType == FontType.SUPERWATER_SMALL) {
            Timeline blinkTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(textLabel.opacityProperty(), 1.0)),
                    new KeyFrame(Duration.seconds(0.8), new KeyValue(textLabel.opacityProperty(), 0.3)),
                    new KeyFrame(Duration.seconds(1.6), new KeyValue(textLabel.opacityProperty(), 1.0)));
            blinkTimeline.setCycleCount(Animation.INDEFINITE);
            blinkTimeline.play();
        }

        // Style the label
        textLabel.setAlignment(Pos.TOP_LEFT);

        textLabel.setPadding(new Insets(25));
        textLabel.setWrapText(true);

        Background background = new Background(backgroundFill);
        textLabel.setBackground(background);

        // Add label to the layer
        getChildren().add(textLabel);
    }

    public GameState getID() {
        return id;
    }
}