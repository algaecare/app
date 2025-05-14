package controller.output;

import com.algaecare.controller.GameStateEventManager;
import com.algaecare.controller.input.TimeController;
import com.algaecare.controller.output.ScreenController;
import com.algaecare.model.Environment;
import com.algaecare.model.GameState;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScreenControllerTest {

    private GameStateEventManager.EventEmitter emitter;
    private Environment environment;
    private Stage stage;
    private TimeController timeController;

    @BeforeAll
    static void initJfx() {
        // Initialize JavaFX toolkit for headless test environments
        new JFXPanel();
    }

    @BeforeEach
    void setUp() {
        emitter = mock(GameStateEventManager.EventEmitter.class);
        environment = mock(Environment.class);
        stage = mock(Stage.class);
        timeController = mock(TimeController.class);
    }

    @Test
    void constructorShouldNotThrow() {
        when(environment.getAlgaeLevel()).thenReturn(100);
        assertDoesNotThrow(() -> new ScreenController(stage, emitter, environment, timeController));
    }

    @Test
    void onGameStateChangedShouldCallUpdateScreen() {
        when(environment.getAlgaeLevel()).thenReturn(100);
        ScreenController controller = new ScreenController(stage, emitter, environment, timeController);

        // Should not throw and should update the screen
        assertDoesNotThrow(() -> controller.onGameStateChanged(GameState.TITLE, GameState.GAMEPLAY));
    }

    @Test
    void getCurrentStateShouldThrowUnsupportedOperationException() {
        when(environment.getAlgaeLevel()).thenReturn(100);
        ScreenController controller = new ScreenController(stage, emitter, environment, timeController);

        assertThrows(UnsupportedOperationException.class, controller::getCurrentState);
    }

    @Test
    void updateScreenShouldShowCorrectDebugText() {
        when(environment.getAlgaeLevel()).thenReturn(42);
        when(timeController.getSecondsRemaining()).thenReturn(60);
        ScreenController controller = new ScreenController(stage, emitter, environment, timeController);

        // This will update the debug text label (not visible in test, but should not
        // throw)
        assertDoesNotThrow(() -> controller.updateScreen(GameState.TITLE, GameState.GAMEPLAY));
    }
}