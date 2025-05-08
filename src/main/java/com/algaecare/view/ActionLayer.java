package com.algaecare.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.algaecare.model.GameState;

public class ActionLayer extends Layer {
    private static final Logger LOGGER = Logger.getLogger(ActionLayer.class.getName());
    private static final Duration FRAME_DURATION = Duration.millis(41.7710944027); // 23.94 FPS (1000ms/23.94)

    private final ImageView imageView;
    private final Timeline animation;
    private final List<Image> frames;
    private int currentFrame = 0;
    private Runnable onSequenceComplete;
    private final GameState gameState;

    public ActionLayer(GameState gameState, String sequenceFolderPath) {
        this.gameState = gameState;

        // Initialize ImageView
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        // Load all frames from the sequence folder
        frames = loadFrames(sequenceFolderPath);
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("No frames found in sequence folder: " + sequenceFolderPath);
        }

        // Set initial frame
        imageView.setImage(frames.get(0));

        // Create animation timeline
        animation = new Timeline();
        animation.setCycleCount(frames.size());
        animation.getKeyFrames().add(
                new KeyFrame(FRAME_DURATION, event -> playNextFrame()));
        animation.setOnFinished(event -> {
            if (onSequenceComplete != null) {
                onSequenceComplete.run();
            }
        });

        getChildren().add(imageView);
        setVisible(false);
    }

    private List<Image> loadFrames(String folderPath) {
        try {
            Path path = Paths.get(Objects.requireNonNull(
                    getClass().getResource(folderPath)).toURI());
            File folder = path.toFile();

            return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                    .filter(file -> file.getName().toLowerCase().matches(".*\\.(png|jpg|jpeg)$"))
                    .sorted() // Ensure frames are in order
                    .map(file -> new Image(file.toURI().toString()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load frames from: " + folderPath, e);
            throw new RuntimeException("Failed to load animation frames", e);
        }
    }

    private void playNextFrame() {
        currentFrame = (currentFrame + 1) % frames.size();
        imageView.setImage(frames.get(currentFrame));
    }

    public void setOnSequenceComplete(Runnable callback) {
        this.onSequenceComplete = callback;
    }

    @Override
    public void showLayer() {
        currentFrame = 0;
        imageView.setImage(frames.get(0));
        setVisible(true);
        animation.playFromStart();
    }

    @Override
    public void hideLayer() {
        setVisible(false);
        animation.stop();
    }

    public GameState getGameState() {
        return gameState;
    }
}