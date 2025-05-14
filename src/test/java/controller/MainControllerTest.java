package controller;

import com.algaecare.controller.GameStateEventManager;
import com.algaecare.controller.MainController;
import com.algaecare.model.GameState;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainControllerTest {

    private Stage stage;

    @BeforeAll
    static void initJfx() {
        // Initialize JavaFX toolkit for headless test environments
        new JFXPanel();
    }

    @BeforeEach
    void setUp() throws Exception {
        // Stage und Scene müssen im FX-Thread erzeugt werden!
        final Stage[] fxStage = new Stage[1];
        final Exception[] thrown = new Exception[1];
        final Object lock = new Object();

        javafx.application.Platform.runLater(() -> {
            try {
                fxStage[0] = new Stage();
                fxStage[0].setScene(new Scene(new StackPane()));
            } catch (Exception e) {
                thrown[0] = e;
            }
            synchronized (lock) {
                lock.notify();
            }
        });
        synchronized (lock) {
            lock.wait(5000);
        }
        if (thrown[0] != null)
            throw thrown[0];
        stage = fxStage[0];
    }

    @Test
    void constructorShouldInitializeAndSetTitleState() throws Exception {
        final MainController[] controller = new MainController[1];
        final Object lock = new Object();

        javafx.application.Platform.runLater(() -> {
            controller[0] = new MainController(stage);
            synchronized (lock) {
                lock.notify();
            }
        });
        synchronized (lock) {
            lock.wait(5000);
        }
        assertEquals(GameState.TITLE, controller[0].getCurrentState());
    }

    @Test
    void emitGameStateChangeShouldUpdateStateAndNotifyListeners() throws Exception {
        final MainController[] controller = new MainController[1];
        final Object lock = new Object();

        javafx.application.Platform.runLater(() -> {
            controller[0] = new MainController(stage);
            synchronized (lock) {
                lock.notify();
            }
        });
        synchronized (lock) {
            lock.wait(5000);
        }

        GameStateEventManager listener = mock(GameStateEventManager.class);
        controller[0].addGameStateChangeListener(listener);

        controller[0].emitGameStateChange(GameState.GAMEPLAY);

        assertEquals(GameState.GAMEPLAY, controller[0].getCurrentState());
        verify(listener).onGameStateChanged(GameState.TITLE, GameState.GAMEPLAY);
    }

    @Test
    void addGameStateChangeListenerShouldAddListener() throws Exception {
        final MainController[] controller = new MainController[1];
        final Object lock = new Object();

        javafx.application.Platform.runLater(() -> {
            controller[0] = new MainController(stage);
            synchronized (lock) {
                lock.notify();
            }
        });
        synchronized (lock) {
            lock.wait(5000);
        }

        GameStateEventManager listener = mock(GameStateEventManager.class);
        controller[0].addGameStateChangeListener(listener);

        controller[0].emitGameStateChange(GameState.ENDSCREEN_NEGATIVE);
        verify(listener).onGameStateChanged(GameState.TITLE, GameState.ENDSCREEN_NEGATIVE);
    }

    @Test
    void getPlatformShouldReturnNoneOnNonRaspberryPi() throws Exception {
        final MainController[] controller = new MainController[1];
        final Object lock = new Object();

        javafx.application.Platform.runLater(() -> {
            controller[0] = new MainController(stage);
            synchronized (lock) {
                lock.notify();
            }
        });
        synchronized (lock) {
            lock.wait(5000);
        }
        String platform = controller[0].getPlatform();
        assertNotNull(platform);
        assertTrue(platform.equals("none") || platform.equals("raspberrypi"));
    }
}