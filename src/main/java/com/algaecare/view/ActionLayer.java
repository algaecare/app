package com.algaecare.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            List<String> frameUrls = new ArrayList<>();

            // Get the resource URL
            URL resourceUrl = getClass().getResource(folderPath);
            if (resourceUrl == null) {
                throw new IllegalArgumentException("Resource folder not found: " + folderPath);
            }

            URI uri = resourceUrl.toURI();

            if ("jar".equals(uri.getScheme())) {
                // We're running from a JAR - need to handle differently
                // Since we can't easily "list" files in a JAR directory,
                // we'll try a common naming convention approach

                // Try common frame naming patterns
                String[] patterns = {
                        "frame_%03d.png", // frame_001.png, frame_002.png, etc.
                        "frame_%d.png", // frame_1.png, frame_2.png, etc.
                        "image_%03d.png", // image_001.png, image_002.png, etc.
                        "image_%d.png", // image_1.png, image_2.png, etc.
                        "%03d.png", // 001.png, 002.png, etc.
                        "%d.png" // 1.png, 2.png, etc.
                };

                // Try to find frames using naming patterns
                for (String pattern : patterns) {
                    for (int i = 1; i <= 1000; i++) { // Try up to 1000 frames
                        String frameName = String.format(pattern, i);
                        URL frameUrl = getClass().getResource(folderPath + "/" + frameName);
                        if (frameUrl != null) {
                            frameUrls.add(frameUrl.toExternalForm());
                        } else if (i > 10 && frameUrls.isEmpty()) {
                            // If we haven't found anything after 10 tries, try next pattern
                            break;
                        } else if (frameUrls.size() > 0 && frameUrl == null) {
                            // We found some frames but this one is missing - assume we're done
                            break;
                        }
                    }
                    if (!frameUrls.isEmpty()) {
                        break; // Found frames with this pattern
                    }
                }

                if (frameUrls.isEmpty()) {
                    // Fallback: try to read from a file system if we're in development
                    try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                        Path folderPathInJar = fileSystem.getPath(folderPath);
                        if (Files.exists(folderPathInJar)) {
                            try (Stream<Path> files = Files.walk(folderPathInJar)) {
                                frameUrls = files
                                        .filter(Files::isRegularFile)
                                        .filter(path -> {
                                            String fileName = path.getFileName().toString().toLowerCase();
                                            return fileName.matches(".*\\.(png|jpg|jpeg)$");
                                        })
                                        .sorted()
                                        .map(path -> getClass().getResource(path.toString()).toExternalForm())
                                        .collect(Collectors.toList());
                            }
                        }
                    }
                }

            } else {
                // We're running from the file system (development mode)
                Path path = Paths.get(uri);
                if (Files.exists(path) && Files.isDirectory(path)) {
                    try (Stream<Path> files = Files.walk(path)) {
                        frameUrls = files
                                .filter(Files::isRegularFile)
                                .filter(p -> {
                                    String fileName = p.getFileName().toString().toLowerCase();
                                    return fileName.matches(".*\\.(png|jpg|jpeg)$");
                                })
                                .sorted()
                                .map(p -> p.toUri().toString())
                                .collect(Collectors.toList());
                    }
                }
            }

            if (frameUrls.isEmpty()) {
                throw new IllegalArgumentException("No image frames found in sequence folder: " + folderPath);
            }

            return frameUrls;

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