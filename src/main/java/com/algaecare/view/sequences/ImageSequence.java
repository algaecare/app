package com.algaecare.view.sequences;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ImageSequence extends StackPane {
    private static final Logger LOGGER = Logger.getLogger(ImageSequence.class.getName());

    private final List<Image> images;
    private final ImageView imageView;
    private final Timeline timeline;
    private int currentFrame;
    private boolean isPlaying;
    private Runnable onCycleComplete;

    public ImageSequence(String folderPath, double frameRate) {
        // Set default frame rate if not provided
        if (frameRate <= 0) {
            frameRate = 24.0; // Default to 24 FPS
        }

        // Load images from the specified folder
        URL resource = Optional.ofNullable(getClass().getResource(folderPath))
                .orElseThrow(() -> new IllegalArgumentException("Invalid folder path: " + folderPath));

        // Filter and sort image files
        File[] files = getFiles(folderPath, resource);

        // Convert files to images
        images = Arrays.stream(files)
                .filter(file -> file.getName().matches("(?i).*\\.(png|jpg|jpeg|gif)$"))
                .sorted()
                .map(file -> new Image(file.toURI().toString()))
                .collect(Collectors.toList());
        if (images.isEmpty()) {
            throw new IllegalArgumentException("No images found in folder: " + folderPath);
        }

        // Set up the ImageView and Timeline
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);

        imageView = new ImageView(images.getFirst());
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(images.getFirst().getWidth());
        imageView.setFitHeight(images.getFirst().getHeight());
        imageView.setSmooth(true);

        getChildren().add(imageView);

        Duration frameDuration = Duration.seconds(1.0 / frameRate);
        timeline = new Timeline(
                new KeyFrame(frameDuration, event -> updateFrame()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        currentFrame = 0;
    }

    private static File[] getFiles(String folderPath, URL resource) {
        File folder;

        try {
            folder = new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid folder path: " + folderPath, e);
        }
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("Folder is not a directory: " + folderPath);
        }

        File[] files = folder.listFiles();
        if (files == null) {
            throw new IllegalArgumentException("Unable to access folder: " + folderPath);
        }
        return files;
    }

    private void updateFrame() {
        currentFrame = (currentFrame + 1) % images.size();
        imageView.setImage(images.get(currentFrame));
        if (currentFrame == 0 && onCycleComplete != null) {
            onCycleComplete.run();
        }
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

    public void setOnCycleComplete(Runnable callback) {
        this.onCycleComplete = callback;
    }
}
