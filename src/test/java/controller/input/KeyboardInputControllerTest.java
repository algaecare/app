package controller.input;

import com.algaecare.controller.GameStateEventManager;
import com.algaecare.controller.input.KeyboardInputController;
import com.algaecare.model.GameState;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class KeyboardInputControllerTest {

    @BeforeAll
    static void initJfx() {
        // Initialize JavaFX toolkit for headless test environments
        new JFXPanel();
    }

    @Test
    void pressingSpaceOnTitleShouldEmitIntroduction() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage stage = new Stage();
            Scene scene = new Scene(new javafx.scene.layout.StackPane(), 100, 100);
            stage.setScene(scene);

            GameStateEventManager.EventEmitter emitter = mock(GameStateEventManager.EventEmitter.class);
            KeyboardInputController controller = new KeyboardInputController(stage, emitter);

            controller.onGameStateChanged(GameState.ENDSCREEN_NEGATIVE, GameState.TITLE);

            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.SPACE, false, false, false, false);
            scene.getOnKeyPressed().handle(event);

            verify(emitter).emitGameStateChange(GameState.AXOLOTL_INTRODUCTION);
            latch.countDown();
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS), "FX task did not complete in time");
    }

    @Test
    void pressingEscapeShouldEmitTitle() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage stage = new Stage();
            Scene scene = new Scene(new javafx.scene.layout.StackPane(), 100, 100);
            stage.setScene(scene);

            GameStateEventManager.EventEmitter emitter = mock(GameStateEventManager.EventEmitter.class);
            KeyboardInputController controller = new KeyboardInputController(stage, emitter);

            controller.onGameStateChanged(GameState.GAMEPLAY, GameState.GAMEPLAY);

            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false);
            scene.getOnKeyPressed().handle(event);

            verify(emitter).emitGameStateChange(GameState.TITLE);
            latch.countDown();
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS), "FX task did not complete in time");
    }

    @Test
    void pressingDigitKeyShouldEmitCorrectObjectState() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage stage = new Stage();
            Scene scene = new Scene(new javafx.scene.layout.StackPane(), 100, 100);
            stage.setScene(scene);

            GameStateEventManager.EventEmitter emitter = mock(GameStateEventManager.EventEmitter.class);
            KeyboardInputController controller = new KeyboardInputController(stage, emitter);

            controller.onGameStateChanged(GameState.AXOLOTL_INTRODUCTION, GameState.GAMEPLAY);

            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DIGIT1, false, false, false, false);
            scene.getOnKeyPressed().handle(event);

            verify(emitter).emitGameStateChange(GameState.OBJECT_GARBAGE_BAG);
            latch.countDown();
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS), "FX task did not complete in time");
    }

    @Test
    void pressingEnterOnIntroductionShouldEmitGameplay() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage stage = new Stage();
            Scene scene = new Scene(new javafx.scene.layout.StackPane(), 100, 100);
            stage.setScene(scene);

            GameStateEventManager.EventEmitter emitter = mock(GameStateEventManager.EventEmitter.class);
            KeyboardInputController controller = new KeyboardInputController(stage, emitter);

            controller.onGameStateChanged(GameState.TITLE, GameState.AXOLOTL_INTRODUCTION);

            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, false, false, false, false);
            scene.getOnKeyPressed().handle(event);

            verify(emitter).emitGameStateChange(GameState.GAMEPLAY);
            latch.countDown();
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS), "FX task did not complete in time");
    }

    @Test
    void getCurrentStateShouldThrowUnsupportedOperationException() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage stage = new Stage();
            GameStateEventManager.EventEmitter emitter = mock(GameStateEventManager.EventEmitter.class);
            KeyboardInputController controller = new KeyboardInputController(stage, emitter);

            assertThrows(UnsupportedOperationException.class, controller::getCurrentState);
            latch.countDown();
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS), "FX task did not complete in time");
    }
}