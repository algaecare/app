package com.algaecare.view;

import com.algaecare.controller.input.TimeController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class TimeLineLayer extends Layer {
    private static final int TIMELINE_HEIGHT = 20;
    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 1080;
    private static final int TIMELINE_Y_POSITION = SCREEN_HEIGHT - TIMELINE_HEIGHT;

    private final Rectangle timelineBar;
    private final TimeController timeController;
    private final Timeline updateTimeline;

    public TimeLineLayer(TimeController timeController) {
        this.timeController = timeController;

        // Create the timeline bar rectangle
        timelineBar = new Rectangle();
        timelineBar.setWidth(SCREEN_WIDTH);
        timelineBar.setHeight(TIMELINE_HEIGHT);
        timelineBar.setFill(Color.web("#CCF3FF"));
        timelineBar.setX(0);
        timelineBar.setY(TIMELINE_Y_POSITION);

        // Create update timeline that refreshes every ~100ms for smoother animation
        // (about 60 FPS)
        updateTimeline = new Timeline(
                new KeyFrame(Duration.millis(100), event -> updateTimelineBar()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);

        // Add the rectangle to this layer
        getChildren().add(timelineBar);

        // Start hidden initially
        setVisible(false);
    }

    private void updateTimelineBar() {
        if (timeController != null) {
            int secondsRemaining = timeController.getSecondsRemaining();
            int totalSeconds = timeController.getGameDurationSeconds();

            // Calculate the width based on time remaining
            double progress = (double) secondsRemaining / totalSeconds;
            double newWidth = SCREEN_WIDTH * progress;

            // Ensure width doesn't go below 0
            newWidth = Math.max(0, newWidth);

            timelineBar.setWidth(newWidth);
        }
    }

    @Override
    public void showLayer() {
        setVisible(true);
        updateTimeline.play();
        updateTimelineBar(); // Initial update
    }

    @Override
    public void hideLayer() {
        setVisible(false);
        updateTimeline.stop();
    }

    public void resetTimeline() {
        timelineBar.setWidth(SCREEN_WIDTH);
    }
}