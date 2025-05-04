package com.algaecare.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.Objects;

public class AlgaeLayer extends Layer {

    public enum AnimationState {
        HIDDEN, INTRO, IDLE, OUTRO
    }

    private final ImageView imageView;
    private final Timeline introTimeline;
    private final Timeline idleTimeline;
    private final Timeline outroTimeline;
    private AnimationState currentState = AnimationState.HIDDEN;

    public AlgaeLayer(int x, int y, int width, int height, String imagePath) {
        super(LayerType.DYNAMIC);

        if (imagePath == null || imagePath.isEmpty()) {
            throw new IllegalArgumentException("Image path cannot be null or empty");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("X and Y coordinates must be non-negative");
        }
        if (getClass().getResource(imagePath) == null) {
            throw new IllegalArgumentException("Image not found at path: " + imagePath);
        }

        // Set up the image view
        Image image = new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm());
        imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setSmooth(true);
        imageView.setCache(true);

        imageView.setTranslateX(x);
        imageView.setTranslateY(y);

        // Initialize animations
        introTimeline = createIntroAnimation();
        idleTimeline = createIdleAnimation();
        outroTimeline = createOutroAnimation();

        // Start hidden
        imageView.setOpacity(0);
        imageView.setScaleX(0);
        imageView.setScaleY(0);
        getChildren().add(imageView);
    }

    private Timeline createIntroAnimation() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(imageView.opacityProperty(), 0),
                        new KeyValue(imageView.scaleXProperty(), 0),
                        new KeyValue(imageView.scaleYProperty(), 0),
                        new KeyValue(imageView.rotateProperty(), 0)),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(imageView.opacityProperty(), 1),
                        new KeyValue(imageView.scaleXProperty(), 1),
                        new KeyValue(imageView.scaleYProperty(), 1),
                        new KeyValue(imageView.rotateProperty(), 0)));
        timeline.setOnFinished(e -> setStateInternal(AnimationState.IDLE));
        return timeline;
    }

    private Timeline createIdleAnimation() {
        double amplitude = 1 + Math.random() * 2; // between 1 and 3 degrees
        double period = 3 + Math.random() * 2; // between 3 and 5 seconds
        double phase = Math.random() * period; // random phase offset

        Timeline timeline = new Timeline();
        int steps = 60; // number of keyframes for smoothness
        for (int i = 0; i <= steps; i++) {
            double t = i * period / steps;
            double angle = amplitude * Math.sin(2 * Math.PI * (t + phase) / period);
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(t),
                            new KeyValue(imageView.rotateProperty(), angle, javafx.animation.Interpolator.LINEAR)));
        }
        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }

    private Timeline createOutroAnimation() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(imageView.opacityProperty(), 1),
                        new KeyValue(imageView.scaleXProperty(), 1),
                        new KeyValue(imageView.scaleYProperty(), 1),
                        new KeyValue(imageView.rotateProperty(), 0)),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(imageView.opacityProperty(), 0),
                        new KeyValue(imageView.scaleXProperty(), 0),
                        new KeyValue(imageView.scaleYProperty(), 0),
                        new KeyValue(imageView.rotateProperty(), 0)));
        timeline.setOnFinished(e -> setStateInternal(AnimationState.HIDDEN));
        return timeline;
    }

    @Override
    public void showLayer() {
        if (currentState == AnimationState.HIDDEN || currentState == AnimationState.OUTRO) {
            setStateInternal(AnimationState.INTRO);
        }
    }

    @Override
    public void hideLayer() {
        if (currentState == AnimationState.IDLE || currentState == AnimationState.INTRO) {
            setStateInternal(AnimationState.OUTRO);
        }
    }

    private void setStateInternal(AnimationState newState) {
        // Stop all animations
        introTimeline.stop();
        idleTimeline.stop();
        outroTimeline.stop();

        currentState = newState;

        switch (newState) {
            case HIDDEN:
                imageView.setOpacity(0);
                break;
            case INTRO:
                introTimeline.setOnFinished(e -> {
                    setStateInternal(AnimationState.IDLE);
                });
                introTimeline.play();
                break;
            case IDLE:
                idleTimeline.play();
                break;
            case OUTRO:
                outroTimeline.setOnFinished(e -> {
                    setStateInternal(AnimationState.HIDDEN);
                });
                outroTimeline.play();
                break;
        }
    }

    public AnimationState getState() {
        return currentState;
    }
}