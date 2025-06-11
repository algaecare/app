package view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import com.algaecare.view.MainScene;

import static org.junit.jupiter.api.Assertions.*;

class MainSceneTest extends ApplicationTest {

    private static Stage testStage;

    @Override
    public void start(Stage stage) {
        testStage = stage;
    }

    @Test
    void mainSceneShouldConfigureStageAndScene() throws Exception {
        Platform.runLater(() -> {
            MainScene mainScene = new MainScene(testStage);

            // Check scene is not null and has correct size
            Scene scene = mainScene.getScene();
            assertNotNull(scene);
            assertEquals(MainScene.WINDOW_WIDTH, scene.getWidth(), 0.1);
            assertEquals(MainScene.WINDOW_HEIGHT, scene.getHeight(), 0.1);

            // Check stage properties
            assertEquals("Algae Care", testStage.getTitle());
            assertEquals(StageStyle.UNDECORATED, testStage.getStyle());
            assertFalse(testStage.isResizable());
            assertTrue(testStage.isFullScreen());
            assertEquals(MainScene.WINDOW_WIDTH, testStage.getWidth(), 0.1);
            assertEquals(MainScene.WINDOW_HEIGHT, testStage.getHeight(), 0.1);
        });
        // Wait for JavaFX thread to finish
        Thread.sleep(500);
    }
}