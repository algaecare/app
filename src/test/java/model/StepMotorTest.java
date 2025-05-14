package model;

import com.algaecare.model.StepMotor;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class StepMotorTest {

    private DigitalOutput pin1;
    private DigitalOutput pin2;
    private DigitalOutput pin3;
    private DigitalOutput pin4;
    private StepMotor stepMotor;

    @BeforeEach
    void setUp() {
        pin1 = mock(DigitalOutput.class);
        pin2 = mock(DigitalOutput.class);
        pin3 = mock(DigitalOutput.class);
        pin4 = mock(DigitalOutput.class);
        stepMotor = new StepMotor(pin1, pin2, pin3, pin4);
    }

    @Test
    void setPinStateShouldSetHigh() {
        stepMotor.setPinState(1, true);
        verify(pin1).state(DigitalState.HIGH);

        stepMotor.setPinState(2, true);
        verify(pin2).state(DigitalState.HIGH);

        stepMotor.setPinState(3, true);
        verify(pin3).state(DigitalState.HIGH);

        stepMotor.setPinState(4, true);
        verify(pin4).state(DigitalState.HIGH);
    }

    @Test
    void setPinStateShouldSetLow() {
        stepMotor.setPinState(1, false);
        verify(pin1).state(DigitalState.LOW);

        stepMotor.setPinState(2, false);
        verify(pin2).state(DigitalState.LOW);

        stepMotor.setPinState(3, false);
        verify(pin3).state(DigitalState.LOW);

        stepMotor.setPinState(4, false);
        verify(pin4).state(DigitalState.LOW);
    }

    @Test
    void getPinShouldThrowForInvalidPin() {
        assertThrows(IllegalArgumentException.class, () -> stepMotor.setPinState(0, true));
        assertThrows(IllegalArgumentException.class, () -> stepMotor.setPinState(5, false));
    }
}