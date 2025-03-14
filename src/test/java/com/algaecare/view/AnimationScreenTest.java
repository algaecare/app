package com.algaecare.view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.application.Platform;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@ExtendWith(ApplicationExtension.class)
class AnimationScreenTest {
    private AnimationScreen animationScreen;
    private Stage stage;
    private AtomicBoolean animationCompleted;

    @Start
    private void start(Stage stage) {
        this.stage = stage;
    }

    @BeforeEach
    void setUp() throws Exception {
        animationCompleted = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                String videoPath = "/animations/Title.mp4";
                animationScreen = new AnimationScreen(videoPath, (v) -> animationCompleted.set(true));
                stage.setScene(new javafx.scene.Scene(animationScreen));
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testAnimationCompletion() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                MediaPlayer mediaPlayer = animationScreen.getMediaPlayer();
                mediaPlayer.setOnEndOfMedia(() -> {
                    animationCompleted.set(true);
                    latch.countDown();
                });
                mediaPlayer.setOnError(() -> latch.countDown());
                mediaPlayer.play();
            } catch (Exception e) {
                e.printStackTrace();
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS), "Animation did not complete in time");
        assertTrue(animationCompleted.get(), "Animation should complete and trigger callback");
    }

    @Test
    void testRestartAnimation() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                animationScreen.restart();
                MediaPlayer mediaPlayer = animationScreen.getMediaPlayer();
                mediaPlayer.setOnEndOfMedia(() -> {
                    animationCompleted.set(true);
                    latch.countDown();
                });
                mediaPlayer.setOnError(() -> latch.countDown());
            } catch (Exception e) {
                e.printStackTrace();
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS), "Animation did not complete in time");
        assertTrue(animationCompleted.get(), "Animation should complete and trigger callback");
    }

    @Test
    void testDispose() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                animationScreen.dispose();
                MediaPlayer mediaPlayer = animationScreen.getMediaPlayer();
                assertTrue(mediaPlayer.getStatus() == MediaPlayer.Status.DISPOSED, "MediaPlayer should be disposed");
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                latch.countDown();
            }
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
    }
}