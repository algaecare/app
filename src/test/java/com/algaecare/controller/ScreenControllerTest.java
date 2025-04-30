package com.algaecare.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.algaecare.model.GameState;
import com.algaecare.model.Environment;
import com.algaecare.view.GameScreen;
import com.algaecare.view.IdleScreen;
import com.algaecare.view.AnimationScreen;

@ExtendWith(ApplicationExtension.class)
class ScreenControllerTest {
    private MainController mainController;
    private Stage stage;
    private Environment environment;

    @Start
    private void start(Stage stage) {
        this.stage = stage;
    }

    @BeforeEach
    void setUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                environment = new Environment(50, 25, 100, 100);
                mainController = new MainController(stage);
                new ScreenController(mainController, stage);
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testInitialScreen() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                Pane window = (Pane) stage.getScene().getRoot();
                assertTrue(window.getChildren().get(0) instanceof IdleScreen,
                        "Initial screen should be IdleScreen");
                assertEquals(GameState.TITLE, mainController.getGameState(),
                        "Initial state should be TITLE");
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
    }
}