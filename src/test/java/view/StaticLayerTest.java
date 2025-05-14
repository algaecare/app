package view;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.algaecare.view.StaticLayer;

import static org.junit.jupiter.api.Assertions.*;

class StaticLayerTest {

    private static final String TEST_IMAGE_PATH = "/02-LAYER.png"; // Place a small PNG in src/test/resources

    @BeforeAll
    static void initJfx() {
        // Initialize JavaFX toolkit for headless test environments
        new JFXPanel();
    }

    @Test
    void constructorShouldThrowForInvalidImagePath() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new StaticLayer(0, 0, "/nonexistent.png"));
        assertTrue(ex.getMessage().contains("Image not found"));
    }

    @Test
    void constructorShouldThrowForNegativeCoordinates() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new StaticLayer(-1, 0, TEST_IMAGE_PATH));
        assertTrue(ex.getMessage().contains("non-negative"));
    }

    @Test
    void setTargetOpacityShouldThrowForInvalidValue() {
        StaticLayer layer = new StaticLayer(0, 0, TEST_IMAGE_PATH);
        assertThrows(IllegalArgumentException.class, () -> layer.setTargetOpacity(-0.1));
        assertThrows(IllegalArgumentException.class, () -> layer.setTargetOpacity(1.1));
    }

    @Test
    void showLayerShouldSetOpacityToOne() {
        StaticLayer layer = new StaticLayer(0, 0, TEST_IMAGE_PATH);
        layer.showLayer();
        assertEquals(1.0, layer.getOpacity(), 0.01);
    }

    @Test
    void hideLayerShouldSetOpacityToZero() {
        StaticLayer layer = new StaticLayer(0, 0, TEST_IMAGE_PATH);
        layer.hideLayer();
        assertEquals(0.0, layer.getOpacity(), 0.01);
    }
}