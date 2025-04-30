package com.algaecare.controller;

// TODO: ADAPT THIS TEST TO USE THE NEW VIDEO SCREEN CONTROLLER

//import static org.junit.jupiter.api.Assertions.*;
//
//import com.algaecare.view.GameVideoScreen;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.testfx.framework.junit5.ApplicationExtension;
//import org.testfx.framework.junit5.Start;
//import org.testfx.util.WaitForAsyncUtils;
//
//import javafx.application.Platform;
//import javafx.scene.layout.Pane;
//import javafx.stage.Stage;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicReference;
//
//import com.algaecare.model.GameState;
//import com.algaecare.model.Environment;
//import com.algaecare.view.IdleVideoScreen;
//import com.algaecare.view.AnimationVideoScreen;
//
//@ExtendWith(ApplicationExtension.class)
//class VideoScreenControllerTest {
//    private MainController mainController;
//    private Stage stage;
//    private Environment environment;
//
//    @Start
//    private void start(Stage stage) {
//        this.stage = stage;
//    }
//
//    @BeforeEach
//    void setUp() throws Exception {
//        CountDownLatch latch = new CountDownLatch(1);
//        Platform.runLater(() -> {
//            try {
//                environment = new Environment(50, 25, 100, 100);
//                mainController = new MainController(stage);
//                new ScreenController(mainController, stage);
//                latch.countDown();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
//        WaitForAsyncUtils.waitForFxEvents();
//    }
//
//    @Test
//    void testInitialScreen() throws Exception {
//        CountDownLatch latch = new CountDownLatch(1);
//        Platform.runLater(() -> {
//            try {
//                Pane window = (Pane) stage.getScene().getRoot();
//                assertTrue(window.getChildren().get(0) instanceof IdleVideoScreen,
//                        "Initial screen should be IdleScreen");
//                assertEquals(GameState.TITLE, mainController.getGameState(),
//                        "Initial state should be TITLE");
//            } finally {
//                latch.countDown();
//            }
//        });
//        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
//    }
//
//    @Test
//    void testStateChangeToOpening() throws Exception {
//        CountDownLatch latch = new CountDownLatch(1);
//        AtomicReference<Boolean> isAnimationScreen = new AtomicReference<>(false);
//
//        Platform.runLater(() -> {
//            try {
//                // Change state
//                mainController.setGameState(GameState.OPENING);
//
//                // Wait for all JavaFX events to complete
//                WaitForAsyncUtils.waitForFxEvents();
//
//                // Get the current screen
//                Pane window = (Pane) stage.getScene().getRoot();
//                isAnimationScreen.set(window.getChildren().get(0) instanceof AnimationScreen);
//            } finally {
//                latch.countDown();
//            }
//        });
//
//        // Wait for our test to complete
//        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
//
//        // Verify the screen changed
//        assertTrue(isAnimationScreen.get(),
//                "Screen should change to AnimationScreen for OPENING state");
//    }
//
//    @Test
//    void testStateChangeToGameplay() throws Exception {
//        CountDownLatch latch = new CountDownLatch(1);
//        Platform.runLater(() -> {
//            try {
//                mainController.setGameState(GameState.GAMEPLAY);
//                Pane window = (Pane) stage.getScene().getRoot();
//                assertTrue(window.getChildren().get(0) instanceof GameScreen,
//                        "Screen should change to GameScreen for GAMEPLAY state");
//            } finally {
//                latch.countDown();
//            }
//        });
//        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
//    }
//
//    @Test
//    void testEnvironmentUpdateInGameScreen() throws Exception {
//        CountDownLatch latch = new CountDownLatch(1);
//        Platform.runLater(() -> {
//            try {
//                mainController.setGameState(GameState.GAMEPLAY);
//                Pane window = (Pane) stage.getScene().getRoot();
//                GameScreen gameScreen = (GameScreen) window.getChildren().get(0);
//
//                gameScreen.updateEnvironmentDisplay(environment);
//
//                // Add assertions for visual changes based on environment
//            } finally {
//                latch.countDown();
//            }
//        });
//        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
//    }
//
//    @Test
//    void testAnimationCompletionCallback() throws Exception {
//        CountDownLatch latch = new CountDownLatch(1);
//        Platform.runLater(() -> {
//            try {
//                mainController.setGameState(GameState.OPENING);
//                assertEquals(GameState.GAMEPLAY, mainController.getGameState(),
//                        "State should change to GAMEPLAY after opening animation");
//            } finally {
//                latch.countDown();
//            }
//        });
//        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX operations timed out");
//    }
//}