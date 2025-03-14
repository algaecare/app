package com.algaecare.view;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.algaecare.controller.ScreenController;

public class AnimationScreen extends Screen {
    private static final Logger LOGGER = Logger.getLogger(ScreenController.class.getName());
    private final Consumer<Void> onAnimationComplete;

    public AnimationScreen(String videoPath, Consumer<Void> onAnimationComplete) {
        super(videoPath);
        this.onAnimationComplete = onAnimationComplete;
    }

    @Override
    protected void configureMediaPlayer() {
        mediaPlayer.setOnEndOfMedia(() -> {
            LOGGER.info("Animation completed");
            dispose();
            onAnimationComplete.accept(null);
        });

        mediaPlayer.setOnError(() -> {
            LOGGER.severe("Error playing animation: " + mediaPlayer.getError());
            dispose();
            onAnimationComplete.accept(null);
        });

        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                LOGGER.info("Starting animation playback");
                mediaPlayer.play();
            }
        });
    }

    @Override
    protected void handleInitializationError(Exception e) {
        LOGGER.log(Level.SEVERE, "Failed to initialize animation screen", e);
        onAnimationComplete.accept(null);
    }
}