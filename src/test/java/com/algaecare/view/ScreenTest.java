package com.algaecare.view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@ExtendWith(ApplicationExtension.class)
class ScreenTest {
    private TestScreen testScreen;
    private Stage stage;

    @Start
    private void start(Stage stage) {
        this.stage = stage;
    }

    @BeforeEach
    void setUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                String videoPath = "/animations/Title.mp4";
                testScreen = new TestScreen(videoPath);
                stage.setScene(new Scene(testScreen));
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testInitialization() {
        assertNotNull(testScreen, "TestScreen should be initialized");
        assertNotNull(testScreen.getMediaPlayer(), "MediaPlayer should be initialized");
    }

    @Test
    void testConfigureMediaPlayer() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                testScreen.configureMediaPlayer();
                MediaPlayer mediaPlayer = testScreen.getMediaPlayer();
                assertEquals(MediaPlayer.Status.PLAYING, mediaPlayer.getStatus(), "MediaPlayer should be playing");
                assertEquals(MediaPlayer.INDEFINITE, mediaPlayer.getCycleCount(),
                        "MediaPlayer should be set to loop indefinitely");
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                latch.countDown();
            }
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
    }

    @Test
    void testHandleInitializationError() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                Exception testException = new Exception("Test exception");
                testScreen.handleInitializationError(testException);
                // Check logs or other error handling mechanisms if necessary
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                latch.countDown();
            }
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
    }

    @Test
    void testDispose() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                testScreen.dispose();
                MediaPlayer mediaPlayer = testScreen.getMediaPlayer();
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