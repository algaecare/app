package com.algaecare.view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@ExtendWith(ApplicationExtension.class)
class WindowTest {
    private Window window;
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
                window = new Window(stage);
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
        assertNotNull(window, "Window should be initialized");
        assertNotNull(window.getScene(), "Scene should be initialized");
    }

    @Test
    void testConfigureStage() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                assertEquals("Algae Care", stage.getTitle(), "Stage title should be 'Algae Care'");
                assertEquals(StageStyle.UNDECORATED, stage.getStyle(), "Stage style should be UNDECORATED");
                assertEquals(window.getScene(), stage.getScene(), "Stage scene should be set to window's scene");
                assertFalse(stage.isResizable(), "Stage should not be resizable");
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                latch.countDown();
            }
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
    }
}