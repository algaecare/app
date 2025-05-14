package view;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.algaecare.view.AlgaeLayer;

import static org.junit.jupiter.api.Assertions.*;

class AlgaeLayerTest {

    private static final String TEST_IMAGE_PATH = "/03-LAYER-CORAL-1.png";

    @BeforeAll
    static void initJfx() {
        // Initialize JavaFX toolkit for headless test environments
        new JFXPanel();
    }

    @Test
    void constructorShouldThrowForInvalidImagePath() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new AlgaeLayer(0, 0, 100, 100, "/nonexistent.png"));
        assertTrue(ex.getMessage().contains("Image not found"));
    }

    @Test
    void constructorShouldThrowForInvalidDimensions() {
        Exception ex1 = assertThrows(IllegalArgumentException.class,
                () -> new AlgaeLayer(0, 0, 0, 100, TEST_IMAGE_PATH));
        Exception ex2 = assertThrows(IllegalArgumentException.class,
                () -> new AlgaeLayer(0, 0, 100, 0, TEST_IMAGE_PATH));
        assertTrue(ex1.getMessage().contains("Width and height must be positive"));
        assertTrue(ex2.getMessage().contains("Width and height must be positive"));
    }

    @Test
    void constructorShouldThrowForNegativeCoordinates() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new AlgaeLayer(-1, 0, 100, 100, TEST_IMAGE_PATH));
        assertTrue(ex.getMessage().contains("X and Y coordinates must be non-negative"));
    }

    @Test
    void showLayerShouldSetStateToIdleIfSkipIntro() {
        AlgaeLayer layer = new AlgaeLayer(0, 0, 100, 100, TEST_IMAGE_PATH);
        layer.setSkipIntroAnimation(true);
        layer.showLayer();
        assertEquals(AlgaeLayer.AnimationState.IDLE, layer.getState());
        assertFalse(layer.isHidden());
    }

    @Test
    void hideLayerShouldSetStateToHidden() {
        AlgaeLayer layer = new AlgaeLayer(0, 0, 100, 100, TEST_IMAGE_PATH);
        layer.setSkipIntroAnimation(true);
        layer.showLayer();
        layer.hideLayer();
        // The animation is async, but after hideLayer() with skipIntro, state should be
        // OUTRO or HIDDEN
        // We'll accept either for this test
        assertTrue(layer.getState() == AlgaeLayer.AnimationState.OUTRO
                || layer.getState() == AlgaeLayer.AnimationState.HIDDEN);
        assertTrue(layer.isHidden());
    }
}