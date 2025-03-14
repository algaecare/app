package com.algaecare.view;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Animation extends View {
    private MediaPlayer mediaPlayer;

    public Animation(String videoPath) {
        super(videoPath);
        initialize(videoPath);
    }

    private void initialize(String videoPath) {
        // Load the video file from resources
        Media media = new Media(Objects.requireNonNull(getClass().getResource(videoPath)).toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        // Create a layout and add the MediaView
        StackPane root = new StackPane();
        root.getChildren().add(mediaView);

        // Create a scene and set it on the stage
        Scene scene = new Scene(root, 800, 600);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Animation Screen");
        stage.show();

        // Play the video
        mediaPlayer.play();
    }

    // Clean up resources when done
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}