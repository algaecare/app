package view;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.algaecare.view.Layer;

import static org.junit.jupiter.api.Assertions.*;

class LayerTest {

    // Minimal concrete subclass for testing
    static class TestLayer extends Layer {
    }

    @BeforeAll
    static void initJfx() {
        // Initialize JavaFX toolkit for headless test environments
        new JFXPanel();
    }

    @Test
    void showLayerShouldSetVisibleTrue() {
        Layer layer = new TestLayer();
        layer.setVisible(false);
        layer.showLayer();
        assertTrue(layer.isVisible());
    }

    @Test
    void hideLayerShouldSetVisibleFalse() {
        Layer layer = new TestLayer();
        layer.setVisible(true);
        layer.hideLayer();
        assertFalse(layer.isVisible());
    }

    @Test
    void constructorShouldSetPickOnBoundsFalseAndAlignmentTopLeft() {
        Layer layer = new TestLayer();
        assertFalse(layer.isPickOnBounds());
        assertEquals(javafx.geometry.Pos.TOP_LEFT, layer.getAlignment());
    }
}