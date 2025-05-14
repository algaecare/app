package view;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.algaecare.view.AxolotlLayer;

import static org.junit.jupiter.api.Assertions.*;

class AxolotlLayerTest {

    @BeforeAll
    static void initJfx() {
        // Initialize JavaFX toolkit for headless test environments
        new JFXPanel();
    }

    @Test
    void constructorShouldSetInitialStateAndExpression() {
        AxolotlLayer layer = new AxolotlLayer(0, 0, 100, 100);
        assertEquals(AxolotlLayer.AnimationState.HIDDEN, layer.getState());
        assertEquals(AxolotlLayer.Expression.WORST, layer.getExpression());
    }

    @Test
    void setExpressionShouldUpdateExpression() {
        AxolotlLayer layer = new AxolotlLayer(0, 0, 100, 100);
        layer.setExpression(AxolotlLayer.Expression.HAPPY);
        assertEquals(AxolotlLayer.Expression.HAPPY, layer.getExpression());
        layer.setExpression(AxolotlLayer.Expression.SAD);
        assertEquals(AxolotlLayer.Expression.SAD, layer.getExpression());
    }

    @Test
    void showLayerShouldSetStateToIntro() {
        AxolotlLayer layer = new AxolotlLayer(0, 0, 100, 100);
        layer.showLayer();
        assertEquals(AxolotlLayer.AnimationState.INTRO, layer.getState());
    }

    @Test
    void hideLayerShouldSetStateToOutro() {
        AxolotlLayer layer = new AxolotlLayer(0, 0, 100, 100);
        layer.showLayer();
        layer.hideLayer();
        assertEquals(AxolotlLayer.AnimationState.OUTRO, layer.getState());
    }
}