package com.algaecare.view;

import javafx.scene.media.MediaPlayer;

public class TestScreen extends Screen {
    public TestScreen(String videoPath) {
        super(videoPath);
    }

    @Override
    protected void configureMediaPlayer() {
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    @Override
    protected void handleInitializationError(Exception e) {
        // Handle initialization error for testing
    }
}