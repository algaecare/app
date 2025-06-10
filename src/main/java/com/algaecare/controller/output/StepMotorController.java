package com.algaecare.controller.output;

import com.algaecare.controller.GameStateEventManager;
import com.algaecare.model.Environment;
import com.algaecare.model.GameState;
import com.algaecare.model.StepMotor;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import static java.lang.Thread.sleep;

public class StepMotorController implements GameStateEventManager {

    private final Environment environment;
    private final StepMotor trapDoor;

    private final int[][] stepSequence = {
            { 1, 0, 0, 1 },
            { 1, 0, 0, 0 },
            { 1, 1, 0, 0 },
            { 0, 1, 0, 0 },
            { 0, 1, 1, 0 },
            { 0, 0, 1, 0 },
            { 0, 0, 1, 1 },
            { 0, 0, 0, 1 }
    };

    public StepMotorController(Environment environment, Context pi4j) {
        this.environment = environment;

        // trapDoor
        trapDoor = new StepMotor(
                pi4j.create(DigitalOutputConfigBuilder.newInstance(pi4j)
                        .id("IN2-1").address(2).shutdown(DigitalState.LOW).initial(DigitalState.LOW)
                        .provider("gpiod-digital-output")),
                pi4j.create(DigitalOutputConfigBuilder.newInstance(pi4j)
                        .id("IN2-2").address(3).shutdown(DigitalState.LOW).initial(DigitalState.LOW)
                        .provider("gpiod-digital-output")),
                pi4j.create(DigitalOutputConfigBuilder.newInstance(pi4j)
                        .id("IN2-3").address(4).shutdown(DigitalState.LOW).initial(DigitalState.LOW)
                        .provider("gpiod-digital-output")),
                pi4j.create(DigitalOutputConfigBuilder.newInstance(pi4j)
                        .id("IN2-4").address(7).shutdown(DigitalState.LOW).initial(DigitalState.LOW)
                        .provider("gpiod-digital-output")));

        // RESET POSITION
        rotateMotor(trapDoor, -4100);
    }

    public void rotateMotor(StepMotor motor, int steps) {
        boolean clockwise = steps > 0;
        steps = Math.abs(steps);
        for (int i = 0; i < steps; i++) {
            int phase = i % stepSequence.length;
            if (!clockwise) {
                phase = 7 - phase;
            }
            ;

            motor.setPinState(1, stepSequence[phase][0] == 1);
            motor.setPinState(2, stepSequence[phase][1] == 1);
            motor.setPinState(3, stepSequence[phase][2] == 1);
            motor.setPinState(4, stepSequence[phase][3] == 1);

            try {
                sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void openTrapDoor() {
        rotateMotor(trapDoor, -2000);
        rotateMotor(trapDoor, 3000);
    }

    @Override
    public GameState getCurrentState() {
        return null;
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'onGameStateChanged'");
    }
}
