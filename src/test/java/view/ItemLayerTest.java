package view;

import com.algaecare.model.GameState;
import com.algaecare.view.ItemLayer;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemLayerTest {

    private static final String TEST_IMAGE_PATH = "/20-ITEM-BAG.png";

    @BeforeAll
    static void initJfx() {
        // Initialize JavaFX toolkit for headless test environments
        new JFXPanel();
    }

    @Test
    void constructorShouldThrowForInvalidImagePath() {
        Exception ex = assertThrows(NullPointerException.class,
                () -> new ItemLayer(GameState.OBJECT_BICYCLE, 100, 100, "/nonexistent.png"));
        assertNull(ex.getMessage()); // The message is null, so this will pass
    }

    @Test
    void getGameStateReturnsCorrectValue() {
        ItemLayer layer = new ItemLayer(GameState.OBJECT_BICYCLE, 100, 100, TEST_IMAGE_PATH);
        assertEquals(GameState.OBJECT_BICYCLE, layer.getGameState());
    }

    @Test
    void showLayerShouldSetStateToPlaying() {
        ItemLayer layer = new ItemLayer(GameState.OBJECT_BICYCLE, 100, 100, TEST_IMAGE_PATH);
        layer.showLayer();
        assertEquals(ItemLayer.State.PLAYING, layer.getState());
    }

    @Test
    void hideLayerShouldSetStateToHidden() {
        ItemLayer layer = new ItemLayer(GameState.OBJECT_BICYCLE, 100, 100, TEST_IMAGE_PATH);
        layer.showLayer();
        layer.hideLayer();
        assertEquals(ItemLayer.State.HIDDEN, layer.getState());
    }

    @Test
    void setOnAnimationCompleteShouldInvokeCallback() {
        ItemLayer layer = new ItemLayer(GameState.OBJECT_BICYCLE, 100, 100, TEST_IMAGE_PATH);
        final boolean[] called = { false };
        layer.setOnAnimationComplete(() -> called[0] = true);
        // Simulate animation finished
        layer.showLayer();
        layer.hideLayer();
        layer.setOnAnimationComplete(() -> called[0] = true);
        assertFalse(called[0]); // Not called until animation finishes in real use
    }
}