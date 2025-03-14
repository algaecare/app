package com.algaecare.view;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.net.URL;
import java.util.logging.Logger;
import java.util.logging.Level;

public abstract class Screen extends VBox {
    private static final Logger LOGGER = Logger.getLogger(Screen.class.getName());
    protected final String videoPath;
    protected MediaView mediaView;
    protected MediaPlayer mediaPlayer;

    protected Screen(String videoPath) {
        this.videoPath = videoPath;
        initialize();
    }

    protected void initialize() {
        try {
            LOGGER.info("Initializing video with path: " + videoPath);
            URL resourceUrl = getClass().getResource(videoPath);
            if (resourceUrl == null) {
                throw new IllegalArgumentException("Video file not found: " + videoPath);
            }

            Media media = new Media(resourceUrl.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaView = new MediaView(mediaPlayer);

            configureMediaView();

            StackPane videoContainer = createVideoContainer();

            configureVBox();
            this.getChildren().clear();
            this.getChildren().add(videoContainer);

            configureMediaPlayer();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize video player", e);
            handleInitializationError(e);
        }
    }

    private void configureMediaView() {
        mediaView.fitWidthProperty().bind(this.widthProperty());
        mediaView.fitHeightProperty().bind(this.heightProperty());
        mediaView.setPreserveRatio(true);
        mediaView.setSmooth(true);
    }

    private StackPane createVideoContainer() {
        StackPane videoContainer = new StackPane(mediaView);
        videoContainer.prefWidthProperty().bind(this.widthProperty());
        videoContainer.prefHeightProperty().bind(this.heightProperty());
        return videoContainer;
    }

    private void configureVBox() {
        this.setFillWidth(true);
        this.setAlignment(Pos.CENTER);
        this.setPrefSize(1920, 1080);
    }

    protected abstract void configureMediaPlayer();

    protected abstract void handleInitializationError(Exception e);

    public void dispose() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            LOGGER.info("Video player disposed");
        }
    }
}