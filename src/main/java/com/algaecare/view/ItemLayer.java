package com.algaecare.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.util.Objects;

import com.algaecare.model.GameState;

public class ItemLayer extends Layer {
    public enum State {
        HIDDEN, PLAYING
    }

    private final ImageView objectImageView;
    private final Timeline animation;
    private State currentState = State.HIDDEN;
    private Runnable onAnimationComplete;
    private final GameState gameState;

    public ItemLayer(GameState gameState, int width, int height, String imagePath) {
        this.gameState = gameState;

        // Object setup
        objectImageView = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm()));
        objectImageView.setPreserveRatio(true);
        objectImageView.setFitWidth(width);
        objectImageView.setFitHeight(height);
        objectImageView.setSmooth(true);
        objectImageView.setCache(true);

        // Position setup
        int y = -height;
        objectImageView.setOpacity(0);
        int x = (int) ((int) (1920 - width) / 2.0);
        objectImageView.setTranslateX(x);
        objectImageView.setTranslateY(y);

        // Set default opacity
        objectImageView.setOpacity(0);

        // Create single sequential animation
        animation = createAnimation(y);

        getChildren().add(objectImageView);
    }

    private Timeline createAnimation(double startY) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(objectImageView.translateYProperty(), startY),
                        new KeyValue(objectImageView.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(objectImageView.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(objectImageView.translateYProperty(),
                                startY + 1920.0 / 4 + objectImageView.getFitHeight()),
                        new KeyValue(objectImageView.opacityProperty(), 0)));

        timeline.setOnFinished(e -> {
            currentState = State.HIDDEN;
            if (onAnimationComplete != null) {
                onAnimationComplete.run();
            }
        });

        return timeline;
    }

    @Override
    public void showLayer() {
        if (currentState == State.HIDDEN) {
            currentState = State.PLAYING;
            animation.play();
        }
    }

    @Override
    public void hideLayer() {
        if (currentState == State.PLAYING) {
            animation.stop();
            currentState = State.HIDDEN;
            objectImageView.setOpacity(0);
        }
    }

    public void setOnAnimationComplete(Runnable handler) {
        this.onAnimationComplete = handler;
    }

    public State getState() {
        return currentState;
    }

    public GameState getGameState() {
        return gameState;
    }
}