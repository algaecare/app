package controller.input;

import com.algaecare.controller.GameStateEventManager;
import com.algaecare.controller.input.NFCInputController;
import com.algaecare.model.GameState;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NFCInputControllerTest {

    private GameStateEventManager.EventEmitter emitter;
    private Context pi4j;

    @BeforeAll
    static void initJfx() {
        // Initialize JavaFX toolkit for headless test environments
        new JFXPanel();
    }

    @BeforeEach
    void setUp() {
        emitter = mock(GameStateEventManager.EventEmitter.class);
        pi4j = mock(Context.class);
    }

    @Test
    void constructorShouldInitializeWithoutException() {
        DigitalInput digitalInput = mock(DigitalInput.class);
        when(pi4j.create(any(com.pi4j.io.gpio.digital.DigitalInputConfig.class))).thenReturn(digitalInput);

        assertDoesNotThrow(() -> new NFCInputController(emitter, pi4j));
    }

    @Test
    void onGameStateChangedShouldUpdateCurrentStateAndResetLever() {
        DigitalInput digitalInput = mock(DigitalInput.class);
        when(pi4j.create(any(com.pi4j.io.gpio.digital.DigitalInputConfig.class))).thenReturn(digitalInput);

        NFCInputController controller = new NFCInputController(emitter, pi4j);
        controller.onGameStateChanged(GameState.TITLE, GameState.GAMEPLAY);

        assertEquals(GameState.GAMEPLAY, controller.getCurrentState());
        assertFalse(controller.isLeverPressed);
    }

    @Test
    void readNfcChipListShouldNotThrowIfFileMissing() {
        DigitalInput digitalInput = mock(DigitalInput.class);
        when(pi4j.create(any(com.pi4j.io.gpio.digital.DigitalInputConfig.class))).thenReturn(digitalInput);

        NFCInputController controller = new NFCInputController(emitter, pi4j);
        assertDoesNotThrow(controller::readNfcChipList);
    }

    @Test
    void getIntFromChipShouldReturnMinusOneOnException() throws Exception {
        DigitalInput digitalInput = mock(DigitalInput.class);
        when(pi4j.create(any(com.pi4j.io.gpio.digital.DigitalInputConfig.class))).thenReturn(digitalInput);

        NFCInputController controller = new NFCInputController(emitter, pi4j) {
            @Override
            public byte[] getDataOfChip() throws IOException {
                throw new IOException("Simulated IO error");
            }
        };

        int result = controller.getIntFromChip();
        assertEquals(-1, result);
    }
}