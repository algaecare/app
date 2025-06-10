package controller.output;

import com.algaecare.controller.output.StepMotorController;
import com.algaecare.model.Environment;
import com.algaecare.model.StepMotor;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StepMotorControllerTest {

    private Environment environment;
    private Context pi4j;
    private DigitalOutput digitalOutput;

    // Test subclass to override rotateMotor and count calls
    static class TestStepMotorController extends StepMotorController {
        public int rotateMotorCallCount = 0;

        public TestStepMotorController(Environment env, Context pi4j) {
            super(env, pi4j);
        }

        @Override
        public void rotateMotor(StepMotor motor, int steps) {
            rotateMotorCallCount++;
            // Do nothing for tests
        }
    }

    @BeforeEach
    void setUp() {
        environment = mock(Environment.class);
        pi4j = mock(Context.class);
        digitalOutput = mock(DigitalOutput.class);

        // Return mock for any DigitalOutputConfigBuilder to avoid NPEs
        when(pi4j.create(any(DigitalOutputConfigBuilder.class))).thenReturn(digitalOutput);
    }

    @Test
    void constructorShouldInitializeAndResetMotors() {
        assertDoesNotThrow(() -> new TestStepMotorController(environment, pi4j));
    }

    @Test
    void openTrapDoorShouldRotateTrapMotor() {
        TestStepMotorController controller = new TestStepMotorController(environment, pi4j);
        int before = controller.rotateMotorCallCount;
        controller.openTrapDoor();
        // Should call rotateMotor twice for trapDoor
        assertEquals(before + 2, controller.rotateMotorCallCount, "openTrapDoor should call rotateMotor twice");
    }
}