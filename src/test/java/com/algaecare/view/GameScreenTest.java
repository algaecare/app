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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.algaecare.model.Environment;

@ExtendWith(ApplicationExtension.class)
class GameScreenTest {
    private GameScreen gameScreen;
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
                gameScreen = new GameScreen(videoPath);
                stage.setScene(new Scene(gameScreen));
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
        assertNotNull(gameScreen, "GameScreen should be initialized");
        assertNotNull(gameScreen.getMediaPlayer(), "MediaPlayer should be initialized");
    }

    @Test
    void testUpdateEnvironmentDisplay() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                Environment environment = new Environment(50, 25, 100, 100);
                gameScreen.updateEnvironmentDisplay(environment);

                Color overlayColor = (Color) gameScreen.getColorOverlay().getFill();
                assertEquals(Color.rgb(127, 255, 255, 0.3), overlayColor,
                        "Overlay color should be correctly calculated based on environment");

                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                latch.countDown();
            }
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
    }
}