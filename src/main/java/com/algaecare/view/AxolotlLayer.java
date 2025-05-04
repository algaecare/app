package com.algaecare.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.Objects;

public class AxolotlLayer extends Layer {

    public enum AnimationState {
        HIDDEN, INTRO, IDLE, OUTRO
    }

    public enum Expression {
        HAPPY("/10-AXOLOTL-HAPPY.png"),
        SAD("/10-AXOLOTL-SAD.png"),
        BAD("/10-AXOLOTL-BAD.png"),
        WORSE("/10-AXOLOTL-WORSE.png"),
        WORST("/10-AXOLOTL-WORST.png");

        private final String imagePath;

        Expression(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

    private final ImageView baseImageView;
    private final ImageView expressionImageView;
    private final Timeline introTimeline;
    private final Timeline idleTimeline;
    private final Timeline outroTimeline;
    private AnimationState currentState = AnimationState.HIDDEN;
    private Expression currentExpression = Expression.HAPPY;

    public AxolotlLayer(int x, int y, int width, int height) {
        super(LayerType.DYNAMIC);

        // Base layer setup
        baseImageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/10-AXOLOTL-BASE.png")).toExternalForm()));
        baseImageView.setPreserveRatio(true);
        baseImageView.setFitWidth(width);
        baseImageView.setFitHeight(height);
        baseImageView.setSmooth(true);
        baseImageView.setCache(true);

        // Expression layer setup
        expressionImageView = new ImageView();
        expressionImageView.setPreserveRatio(true);
        expressionImageView.setFitWidth(width);
        expressionImageView.setFitHeight(height);
        expressionImageView.setSmooth(true);
        expressionImageView.setCache(true);

        // Position setup for both layers
        baseImageView.setTranslateX(x);
        baseImageView.setTranslateY(y);
        expressionImageView.setTranslateX(x);
        expressionImageView.setTranslateY(y);

        // Load initial expression
        updateExpression(Expression.WORST);

        // Initialize animations
        introTimeline = createIntroAnimation();
        idleTimeline = createIdleAnimation();
        outroTimeline = createOutroAnimation();

        // Start hidden
        baseImageView.setOpacity(0);
        expressionImageView.setOpacity(0);
        baseImageView.setScaleX(0);
        baseImageView.setScaleY(0);
        expressionImageView.setScaleX(0);
        expressionImageView.setScaleY(0);

        getChildren().addAll(baseImageView, expressionImageView);
    }

    public void setExpression(Expression expression) {
        if (expression != currentExpression) {
            updateExpression(expression);
        }
    }

    private void updateExpression(Expression expression) {
        Image newImage = new Image(getClass().getResource(expression.getImagePath()).toExternalForm());
        expressionImageView.setImage(newImage);
        currentExpression = expression;
    }

    private Timeline createIntroAnimation() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(baseImageView.opacityProperty(), 0),
                        new KeyValue(expressionImageView.opacityProperty(), 0),
                        new KeyValue(baseImageView.scaleXProperty(), 0.5),
                        new KeyValue(baseImageView.scaleYProperty(), 0.5),
                        new KeyValue(expressionImageView.scaleXProperty(), 0.5),
                        new KeyValue(expressionImageView.scaleYProperty(), 0.5)),
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(baseImageView.opacityProperty(), 1),
                        new KeyValue(expressionImageView.opacityProperty(), 1),
                        new KeyValue(baseImageView.scaleXProperty(), 1.2),
                        new KeyValue(baseImageView.scaleYProperty(), 1.2),
                        new KeyValue(expressionImageView.scaleXProperty(), 1.2),
                        new KeyValue(expressionImageView.scaleYProperty(), 1.2)),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(baseImageView.scaleXProperty(), 1),
                        new KeyValue(baseImageView.scaleYProperty(), 1),
                        new KeyValue(expressionImageView.scaleXProperty(), 1),
                        new KeyValue(expressionImageView.scaleYProperty(), 1)));
        timeline.setOnFinished(e -> setStateInternal(AnimationState.IDLE));
        return timeline;
    }

    private Timeline createIdleAnimation() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(baseImageView.translateYProperty(), baseImageView.getTranslateY()),
                        new KeyValue(expressionImageView.translateYProperty(), expressionImageView.getTranslateY())),
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(baseImageView.translateYProperty(), baseImageView.getTranslateY() - 15),
                        new KeyValue(expressionImageView.translateYProperty(),
                                expressionImageView.getTranslateY() - 15)),
                new KeyFrame(Duration.seconds(3),
                        new KeyValue(baseImageView.translateYProperty(), baseImageView.getTranslateY()),
                        new KeyValue(expressionImageView.translateYProperty(), expressionImageView.getTranslateY())));
        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }

    private Timeline createOutroAnimation() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(baseImageView.opacityProperty(), 1),
                        new KeyValue(expressionImageView.opacityProperty(), 1),
                        new KeyValue(baseImageView.scaleXProperty(), 1),
                        new KeyValue(baseImageView.scaleYProperty(), 1),
                        new KeyValue(expressionImageView.scaleXProperty(), 1),
                        new KeyValue(expressionImageView.scaleYProperty(), 1)),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(baseImageView.opacityProperty(), 0),
                        new KeyValue(expressionImageView.opacityProperty(), 0),
                        new KeyValue(baseImageView.scaleXProperty(), 0.5),
                        new KeyValue(baseImageView.scaleYProperty(), 0.5),
                        new KeyValue(expressionImageView.scaleXProperty(), 0.5),
                        new KeyValue(expressionImageView.scaleYProperty(), 0.5)));
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
                baseImageView.setOpacity(0);
                expressionImageView.setOpacity(0);
                break;
            case INTRO:
                introTimeline.play();
                break;
            case IDLE:
                idleTimeline.play();
                break;
            case OUTRO:
                outroTimeline.play();
                break;
        }
    }

    public AnimationState getState() {
        return currentState;
    }

    public Expression getExpression() {
        return currentExpression;
    }
}