package com.algaecare.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.algaecare.controller.ScreenController;

import javafx.scene.media.MediaPlayer;

public class IdleScreen extends Screen {
    private static final Logger LOGGER = Logger.getLogger(ScreenController.class.getName());

    public IdleScreen(String videoPath) {
        super(videoPath);
    }

    @Override
    protected void configureMediaPlayer() {
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    @Override
    protected void handleInitializationError(Exception e) {
        LOGGER.log(Level.SEVERE, "Failed to initialize idle screen", e);
    }
}