package com.algaecare.controller;

import static org.junit.jupiter.api.Assertions.*;

import javafx.application.Platform;
import javafx.event.EventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@ExtendWith(ApplicationExtension.class)
class KeyboardInputControllerTest {
    private KeyboardInputController controller;
    private Stage stage;
    private Scene scene;

    @Start
    private void start(Stage stage) {
        this.stage = stage;
        this.scene = new Scene(new VBox());
        stage.setScene(scene);
    }

    @BeforeEach
    void setUp() {
        controller = new KeyboardInputController(stage);
    }

    @Test
    void testKeyBinding() throws Exception {
        // Arrange
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            controller.bindKey(KeyCode.SPACE, event -> handlerCalled.set(true));
            scene.getOnKeyPressed().handle(
                    new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.SPACE, false, false, false, false));
            latch.countDown();
        });

        // Act & Assert
        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(handlerCalled.get(), "Key handler should be called");
    }

    @Test
    void testUnbindKey() {
        // Arrange
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        controller.bindKey(KeyCode.SPACE, event -> handlerCalled.set(true));
        controller.unbindKey(KeyCode.SPACE);

        // Act
        scene.getOnKeyPressed().handle(
                new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.SPACE, false, false, false, false));

        // Assert
        assertFalse(handlerCalled.get(), "Key handler should not be called after unbinding");
    }

    @Test
    void testClearBindings() {
        // Arrange
        AtomicBoolean handler1Called = new AtomicBoolean(false);
        AtomicBoolean handler2Called = new AtomicBoolean(false);

        controller.bindKey(KeyCode.SPACE, event -> handler1Called.set(true));
        controller.bindKey(KeyCode.ENTER, event -> handler2Called.set(true));
        controller.clearBindings();

        // Act
        scene.getOnKeyPressed().handle(
                new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.SPACE, false, false, false, false));
        scene.getOnKeyPressed().handle(
                new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, false, false, false, false));

        // Assert
        assertFalse(handler1Called.get(), "Handler 1 should not be called after clearing");
        assertFalse(handler2Called.get(), "Handler 2 should not be called after clearing");
    }

    @Test
    void testSceneChange() throws Exception {
        // Arrange
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            controller.bindKey(KeyCode.SPACE, event -> handlerCalled.set(true));
            Scene newScene = new Scene(new VBox());
            stage.setScene(newScene);
            newScene.getOnKeyPressed().handle(
                    new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.SPACE, false, false, false, false));
            latch.countDown();
        });

        // Act & Assert
        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(handlerCalled.get(), "Handler should work with new scene");
    }

    @Test
    void testDispose() throws Exception {
        // Arrange
        AtomicBoolean handlerCalledBeforeDispose = new AtomicBoolean(false);
        AtomicBoolean handlerCalledAfterDispose = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Set up and verify initial handler
                controller.bindKey(KeyCode.SPACE, event -> handlerCalledBeforeDispose.set(true));

                // Create and fire initial key event
                KeyEvent spaceEvent = new KeyEvent(
                        KeyEvent.KEY_PRESSED, "", "", KeyCode.SPACE,
                        false, false, false, false);
                scene.getOnKeyPressed().handle(spaceEvent);

                // Dispose controller
                controller.dispose();

                // Try to bind and trigger new handler after dispose
                controller.bindKey(KeyCode.SPACE, event -> handlerCalledAfterDispose.set(true));

                // If scene handler exists, try to trigger it
                EventHandler<? super KeyEvent> handler = scene.getOnKeyPressed();
                if (handler != null) {
                    handler.handle(spaceEvent);
                }
            } finally {
                latch.countDown();
            }
        });

        // Wait for JavaFX operations to complete
        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
        WaitForAsyncUtils.waitForFxEvents();

        // Verify the results
        assertTrue(handlerCalledBeforeDispose.get(),
                "Handler should have been called before dispose");
        assertFalse(handlerCalledAfterDispose.get(),
                "Handler should not be called after dispose");
        assertNull(scene.getOnKeyPressed(),
                "Scene key handler should be null after dispose");
    }
}