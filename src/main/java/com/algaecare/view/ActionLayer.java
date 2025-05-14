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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.algaecare.model.GameState;

public class ActionLayer extends Layer {
    private static final Logger LOGGER = Logger.getLogger(ActionLayer.class.getName());
    private static final Duration FRAME_DURATION = Duration.millis(41.7710944027); // 24 FPS
    private static final int PRELOAD_FRAMES = 5; // Number of frames to keep in memory

    private final ImageView imageView;
    private final Timeline animation;
    private final List<String> frameUrls;
    private final Map<Integer, Image> frameCache;
    private int currentFrame = 0;
    private Runnable onSequenceComplete;
    private final GameState gameState;

    public ActionLayer(GameState gameState, String sequenceFolderPath) {
        this.gameState = gameState;
        this.frameCache = new ConcurrentHashMap<>();

        // Initialize ImageView
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true); // Enable cache for ImageView

        // Load frame URLs
        frameUrls = loadFrameUrls(sequenceFolderPath);

        // Preload initial frames
        preloadFrames(0);

        // Set initial frame
        loadFrame(0);

        // Create animation timeline
        animation = new Timeline();
        animation.setCycleCount(frameUrls.size());
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

    private void preloadFrames(int startIndex) {
        // Clear old cached frames that we won't need soon
        frameCache.keySet().removeIf(index -> index < startIndex || index >= startIndex + PRELOAD_FRAMES);

        // Preload next few frames
        for (int i = 0; i < PRELOAD_FRAMES && startIndex + i < frameUrls.size(); i++) {
            final int frameIndex = startIndex + i;
            if (!frameCache.containsKey(frameIndex)) {
                Image img = new Image(
                        frameUrls.get(frameIndex),
                        1920, // max width
                        1080, // max height
                        true, // preserve ratio
                        true, // smooth
                        true // background loading
                );
                frameCache.put(frameIndex, img);
            }
        }
    }

    private List<String> loadFrameUrls(String folderPath) {
        try {
            Path path = Paths.get(Objects.requireNonNull(
                    getClass().getResource(folderPath)).toURI());
            File folder = path.toFile();

            return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                    .filter(file -> file.getName().toLowerCase().matches(".*\\.(png|jpg|jpeg)$"))
                    .sorted()
                    .map(file -> file.toURI().toString())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load frame URLs from: " + folderPath, e);
            throw new IllegalArgumentException("No frames found in sequence folder: " + folderPath, e);
        }
    }

    private void loadFrame(int frameIndex) {
        Image img = frameCache.get(frameIndex);
        if (img == null) {
            img = new Image(
                    frameUrls.get(frameIndex),
                    1920, 1080, true, true, true);
            frameCache.put(frameIndex, img);
        }
        imageView.setImage(img);

        // Preload next frames
        preloadFrames(frameIndex + 1);
    }

    private void playNextFrame() {
        currentFrame = (currentFrame + 1) % frameUrls.size();
        loadFrame(currentFrame);
    }

    public void setOnSequenceComplete(Runnable callback) {
        this.onSequenceComplete = callback;
    }

    @Override
    public void showLayer() {
        currentFrame = 0;
        loadFrame(currentFrame); // Load first frame instead of accessing frames list
        setVisible(true);
        animation.playFromStart();
    }

    @Override
    public void hideLayer() {
        setVisible(false);
        animation.stop();
        frameCache.clear(); // Clear cache when hiding
    }

    public GameState getGameState() {
        return gameState;
    }
}