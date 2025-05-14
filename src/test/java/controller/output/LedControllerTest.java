package controller.output;

import com.algaecare.controller.output.LedController;
import com.algaecare.model.Environment;
import com.algaecare.model.GameState;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class LedControllerTest {

    private Environment environment;
    private Context pi4j;
    private DigitalOutput digitalOutput;

    @BeforeEach
    void setUp() {
        environment = mock(Environment.class);
        pi4j = mock(Context.class);
        digitalOutput = mock(DigitalOutput.class);

        // Mock pi4j.digitalOutput().create(pin) to always return our digitalOutput mock
        var digitalOutputProvider = mock(com.pi4j.io.gpio.digital.DigitalOutputProvider.class);
        when(pi4j.digitalOutput()).thenReturn(digitalOutputProvider);
        when(digitalOutputProvider.create(anyInt())).thenReturn(digitalOutput);
    }

    @Test
    void onGameStateChangedShouldUpdateLedsBasedOnAlgaeLevel() {
        when(environment.getAlgaeLevel()).thenReturn(50);

        LedController controller = new LedController(environment, pi4j);
        controller.onGameStateChanged(GameState.GAMEPLAY, GameState.GAMEPLAY);

        // With algae level 50, thresholdIndex should be 3, so first 4 LEDs on, rest off
        verify(digitalOutput, times(4)).high();
        verify(digitalOutput, times(2)).low();
    }

    @Test
    void updateLedShouldCallHighOrLow() {
        LedController controller = new LedController(environment, pi4j);

        controller.updateLed(11, true);
        verify(digitalOutput).high();

        controller.updateLed(11, false);
        verify(digitalOutput).low();
    }
}