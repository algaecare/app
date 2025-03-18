package com.algaecare.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ImageSequence extends StackPane {
    private static final Logger LOGGER = Logger.getLogger(ImageSequence.class.getName());

    private final List<Image> images;
    private final ImageView imageView;
    private final Timeline timeline;
    private int currentFrame;
    private boolean isPlaying;

    public ImageSequence(String folderPath, double frameRate) {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            LOGGER.log(Level.SEVERE, "Folder does not exist or is not a directory: " + folderPath);
            throw new IllegalArgumentException("Invalid folder path");
        }

        File[] files = folder.listFiles();
        if (files == null) {
            LOGGER.log(Level.SEVERE, "Unable to access folder: " + folderPath);
            throw new IllegalArgumentException("Unable to access folder");
        }

        images = Arrays.stream(files)
                .filter(file -> file.getName().toLowerCase().matches(".*\\.(png|jpg|jpeg|gif)$"))
                .sorted()
                .map(file -> new Image(file.toURI().toString()))
                .collect(Collectors.toList());

        if (images.isEmpty()) {
            LOGGER.log(Level.SEVERE, "No images found in folder: " + folderPath);
            throw new IllegalArgumentException("No images found in folder");
        }

        imageView = new ImageView(images.get(0));
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.fitHeightProperty().bind(this.heightProperty());
        getChildren().add(imageView);

        Duration frameDuration = Duration.seconds(1.0 / frameRate);
        timeline = new Timeline(
                new KeyFrame(frameDuration, event -> updateFrame()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        currentFrame = 0;
        isPlaying = false;
    }

    private void updateFrame() {
        currentFrame = (currentFrame + 1) % images.size();
        imageView.setImage(images.get(currentFrame));
    }

    public void play() {
        if (!isPlaying) {
            timeline.play();
            isPlaying = true;
            LOGGER.info("Started playing image sequence");
        }
    }

    public void pause() {
        if (isPlaying) {
            timeline.pause();
            isPlaying = false;
            LOGGER.info("Paused image sequence");
        }
    }

    public void stop() {
        timeline.stop();
        currentFrame = 0;
        imageView.setImage(images.get(0));
        isPlaying = false;
        LOGGER.info("Stopped image sequence");
    }

    public void dispose() {
        stop();
        images.clear();
        LOGGER.info("Disposed image sequence");
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public int getTotalFrames() {
        return images.size();
    }
}