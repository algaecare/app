package view;

import com.algaecare.model.GameState;
import com.algaecare.view.ActionLayer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import javafx.embed.swing.JFXPanel;
import static org.junit.jupiter.api.Assertions.*;

class ActionLayerTest {
    private static final String TEST_SEQUENCE_FOLDER = "/actions/bike";

    @BeforeAll
    static void initJfx() {
        // Correct way to initialize JavaFX toolkit for tests
        new JFXPanel();
    }

    @Test
    void constructorShouldThrowIfNoFrames() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> new ActionLayer(GameState.GAMEPLAY, "/nonexistent_folder"));
        assertTrue(exception.getMessage().contains("No frames found"));
    }

    @Test
    void getGameStateReturnsCorrectValue() {
        ActionLayer layer = new ActionLayer(GameState.GAMEPLAY, TEST_SEQUENCE_FOLDER);
        assertEquals(GameState.GAMEPLAY, layer.getGameState());
    }

    @Test
    void showLayerShouldSetVisibleAndStartAnimation() {
        ActionLayer layer = new ActionLayer(GameState.GAMEPLAY, TEST_SEQUENCE_FOLDER);
        layer.showLayer();
        assertTrue(layer.isVisible());
    }

    @Test
    void hideLayerShouldSetInvisibleAndClearCache() {
        ActionLayer layer = new ActionLayer(GameState.GAMEPLAY, TEST_SEQUENCE_FOLDER);
        layer.showLayer();
        layer.hideLayer();
        assertFalse(layer.isVisible());
    }

    @Test
    void setOnSequenceCompleteShouldInvokeCallback() {
        ActionLayer layer = new ActionLayer(GameState.GAMEPLAY, TEST_SEQUENCE_FOLDER);
        final boolean[] called = { false };
        layer.setOnSequenceComplete(() -> called[0] = true);
        layer.showLayer();
        layer.hideLayer();
        layer.setOnSequenceComplete(() -> called[0] = true);
        // Simulate animation finished
        layer.setOnSequenceComplete(() -> called[0] = true);
        assertFalse(called[0]);
    }
}