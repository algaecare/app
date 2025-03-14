package com.algaecare.view;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.algaecare.controller.ScreenController;

public class AnimationScreen extends Screen {
    private static final Logger LOGGER = Logger.getLogger(ScreenController.class.getName());
    private final Consumer<Void> onAnimationComplete;
    private boolean isPlaying = false;

    public AnimationScreen(String videoPath, Consumer<Void> onAnimationComplete) {
        super(videoPath);
        this.onAnimationComplete = onAnimationComplete;
    }

    @Override
    protected void configureMediaPlayer() {
        if (mediaPlayer == null) {
            LOGGER.severe("MediaPlayer not initialized");
            return;
        }

        mediaPlayer.setOnEndOfMedia(() -> {
            LOGGER.info("Animation completed");
            isPlaying = false;
            mediaPlayer.stop();
            mediaPlayer.seek(javafx.util.Duration.ZERO);
            onAnimationComplete.accept(null);
        });

        mediaPlayer.setOnError(() -> {
            LOGGER.severe("Error playing animation: " + mediaPlayer.getError());
            isPlaying = false;
            onAnimationComplete.accept(null);
        });

        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && !isPlaying) {
                LOGGER.info("Starting animation playback");
                isPlaying = true;
                mediaPlayer.seek(javafx.util.Duration.ZERO);
                mediaPlayer.play();
            } else if (oldScene != null) {
                LOGGER.info("Stopping animation playback");
                isPlaying = false;
                mediaPlayer.stop();
            }
        });
    }

    @Override
    protected void handleInitializationError(Exception e) {
        LOGGER.log(Level.SEVERE, "Failed to initialize animation screen", e);
        onAnimationComplete.accept(null);
    }

    public void restart() {
        if (mediaPlayer != null && !isPlaying) {
            LOGGER.info("Restarting animation");
            isPlaying = true;
            mediaPlayer.seek(javafx.util.Duration.ZERO);
            mediaPlayer.play();
        }
    }

    @Override
    public void dispose() {
        isPlaying = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            LOGGER.info("Animation player disposed");
        }
    }
}