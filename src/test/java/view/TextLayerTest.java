package view;

import com.algaecare.model.GameState;
import com.algaecare.view.TextLayer;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextLayerTest {

    @BeforeAll
    static void initJfx() {
        // Initialize JavaFX toolkit for headless test environments
        new JFXPanel();
    }

    @Test
    void constructorShouldSetPropertiesCorrectly() {
        TextLayer layer = new TextLayer(GameState.TITLE, 400, 100, 10, 20, "INTER");
        assertEquals(GameState.TITLE, layer.getID());
        assertEquals(1920, layer.getPrefWidth(), 0.01);
        assertEquals(1080, layer.getPrefHeight(), 0.01);
    }

    @Test
    void constructorShouldAcceptAllFontTypes() {
        assertDoesNotThrow(() -> new TextLayer(GameState.TITLE, 400, 100, 10, 20, "INTER"));
        assertDoesNotThrow(() -> new TextLayer(GameState.TITLE, 400, 100, 10, 20, "SUPERWATER_BIG"));
        assertDoesNotThrow(() -> new TextLayer(GameState.TITLE, 400, 100, 10, 20, "SUPERWATER_SMALL"));
    }

    @Test
    void constructorShouldThrowForInvalidFontType() {
        assertThrows(IllegalArgumentException.class,
                () -> new TextLayer(GameState.TITLE, 400, 100, 10, 20, "NOT_A_FONT"));
    }
}