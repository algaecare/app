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
    private final StepMotor o2Display;
    private final StepMotor co2Display;
    private final StepMotor trapDoor;
    private int stepCounterO2 = 0;
    private int stepCounterCO2 = 0;
    private int stepCounterTrapDoor = 0;

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

        co2Display = new StepMotor(
                pi4j.create(DigitalOutputConfigBuilder.newInstance(pi4j)
                        .id("IN1-1").address(5).shutdown(DigitalState.LOW).initial(DigitalState.LOW)
                        .provider("gpiod-digital-output")),
                pi4j.create(DigitalOutputConfigBuilder.newInstance(pi4j)
                        .id("IN1-2").address(6).shutdown(DigitalState.LOW).initial(DigitalState.LOW)
                        .provider("gpiod-digital-output")),
                pi4j.create(DigitalOutputConfigBuilder.newInstance(pi4j)
                        .id("IN1-3").address(13).shutdown(DigitalState.LOW).initial(DigitalState.LOW)
                        .provider("gpiod-digital-output")),
                pi4j.create(DigitalOutputConfigBuilder.newInstance(pi4j)
                        .id("IN1-4").address(19).shutdown(DigitalState.LOW).initial(DigitalState.LOW)
                        .provider("gpiod-digital-output")));

        o2Display = new StepMotor(
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

        trapDoor = new StepMotor(
                pi4j.create(DigitalOutputConfigBuilder.newInstance(pi4j)
                        .id("IN3-1").address(27).shutdown(DigitalState.LOW).initial(DigitalState.LOW)
                        .provider("gpiod-digital-output")),
                pi4j.create(DigitalOutputConfigBuilder.newInstance(pi4j)
                        .id("IN3-2").address(22).shutdown(DigitalState.LOW).initial(DigitalState.LOW)
                        .provider("gpiod-digital-output")),
                pi4j.create(DigitalOutputConfigBuilder.newInstance(pi4j)
                        .id("IN3-3").address(10).shutdown(DigitalState.LOW).initial(DigitalState.LOW)
                        .provider("gpiod-digital-output")),
                pi4j.create(DigitalOutputConfigBuilder.newInstance(pi4j)
                        .id("IN3-4").address(12).shutdown(DigitalState.LOW).initial(DigitalState.LOW)
                        .provider("gpiod-digital-output")));

        // RESET POSITION
        rotateMotor(co2Display, 4100);
        rotateMotor(o2Display, 4100);
        rotateMotor(trapDoor, 4100);

        rotateMotor(co2Display, -2050);
        rotateMotor(o2Display, -2050);
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

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        int level = environment.getAlgaeLevel();
        int stepsGood = Math.round(2000 - ((float) level / 100 * 4000));

        int co2StepsToDo = stepsGood - stepCounterCO2;
        int o2StepsToDo = stepsGood - stepCounterO2;

        if (co2StepsToDo != 0) {
            rotateMotor(co2Display, co2StepsToDo);
            stepCounterCO2 += co2StepsToDo;
        }
        if (o2StepsToDo != 0) {
            rotateMotor(o2Display, o2StepsToDo);
            stepCounterO2 += o2StepsToDo;
        }
    }

    public void openTrapDoor() {
        rotateMotor(trapDoor, 2000);
        rotateMotor(trapDoor, -2000);
    }

    @Override
    public GameState getCurrentState() {
        return null;
    }
}
