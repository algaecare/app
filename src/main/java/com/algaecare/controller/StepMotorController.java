package com.algaecare.controller;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalState;

import java.util.*;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Implementation of the CrowPi Step Motor using GPIO with Pi4J
 */
public class StepMotorController {
    /**
     * Default GPIO BCM addresses used as part of the various steps
     */
    private final static int[] DEFAULT_PINS = {5, 6, 13, 19};
    /**
     * Default set of steps which will be executed once in order when turning one step
     *
     * @see #StepMotorController(Context, int[], int[][], long)
     */
    private final static int[][] DEFAULT_STEPS = {
        {3, 0},
        {0},
        {0, 1},
        {1},
        {1, 2},
        {2},
        {3, 2},
        {3},
    };
    /**
     * Default duration in milliseconds when pulsing one step, increasing this value makes the step motor turn slower.
     */
    private static final long DEFAULT_PULSE_MILLISECONDS = 1;

    /**
     * Desired pulse duration in milliseconds when executing a single step
     */
    private final long pulseMilliseconds;
    /**
     * Array of digital outputs used by this component, can be contained within {@link #stepsForward} or {@link #stepsBackward}
     */
    private final DigitalOutput[] digitalOutputs;
    /**
     * Pre-generated list of forward steps, containing a set of all digital outputs which need to be pulsed for each step
     */
    private final List<Set<DigitalOutput>> stepsForward;
    /**
     * Pre-generated list of backward steps, always equal to {@link #stepsForward} in reversed order.
     */
    private final List<Set<DigitalOutput>> stepsBackward;

    /**
     * Creates a new step motor component with the default pins, steps and pulse duration.
     *
     * @param pi4j Pi4J context
     */
    public StepMotorController(Context pi4j) {
        this(pi4j, DEFAULT_PINS, DEFAULT_STEPS, DEFAULT_PULSE_MILLISECONDS);
    }

    /**
     * Creates a new step motor component with custom pins, steps and pulse duration.
     * <p>
     * The first dimension of {@code steps} represents the various steps which are to be executed in order.
     * The second dimension of {@code steps} represents which output(s) should be pulsed for the current step.
     * Please note that the second dimension does not store the pin addresses but the index within {@code addresses}.
     *
     * @param pi4j              Pi4J context
     * @param addresses         Array of BCM pin addresses to be driven during steps
     * @param steps             Two-dimensional array containing steps with their associated pins
     * @param pulseMilliseconds Duration in milliseconds for pulses
     */
    public StepMotorController(Context pi4j, int[] addresses, int[][] steps, long pulseMilliseconds) {
        this.pulseMilliseconds = pulseMilliseconds;

        // Initialize digital outputs
        digitalOutputs = new DigitalOutput[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            digitalOutputs[i] = pi4j.create(buildDigitalOutputConfig(pi4j, addresses[i]));
        }

        // Transform two-dimensional array with steps into List<Set<DigitalOutput>>
        // The first dimension of the array indicates which step is being described
        // The second dimension of the array indicates which outputs (referenced by their index) should be driven
        stepsForward = new ArrayList<>();
        for (final var step : steps) {
            // Generate set of outputs driven as part of this step
            final var stepOutputs = new LinkedHashSet<DigitalOutput>();
            for (final var outputIndex : step) {
                // Ensure output index is within bounds
                if (outputIndex < 0 || outputIndex > digitalOutputs.length) {
                    throw new IllegalArgumentException("Output index must be between 0 and " + digitalOutputs.length);
                }

                // Add specified output to set
                stepOutputs.add(digitalOutputs[outputIndex]);
            }

            // Add collected step outputs to list of steps
            stepsForward.add(stepOutputs);
        }

        // Generate list of reversed steps
        stepsBackward = new ArrayList<>();
        stepsBackward.addAll(stepsForward);
        Collections.reverse(stepsBackward);
    }

    public StepMotorController() {
        pulseMilliseconds = DEFAULT_PULSE_MILLISECONDS;
        Context pi4j = Pi4J.newAutoContext();

        // Initialize digital outputs
        digitalOutputs = new DigitalOutput[DEFAULT_PINS.length];
        for (int i = 0; i < DEFAULT_PINS.length; i++) {
            digitalOutputs[i] = pi4j.create(buildDigitalOutputConfig(pi4j, DEFAULT_PINS[i]));
        }

        // Initialize forward steps
        stepsForward = new ArrayList<>();
        for (final var step : DEFAULT_STEPS) {
            final var stepOutputs = new LinkedHashSet<DigitalOutput>();
            for (final var outputIndex : step) {
                stepOutputs.add(digitalOutputs[outputIndex]);
            }
            stepsForward.add(stepOutputs);
        }

        // Initialize backward steps
        stepsBackward = new ArrayList<>();
        stepsBackward.addAll(stepsForward);
        Collections.reverse(stepsBackward);
    }

    /**
     * Turns the step motor by the given amount of degrees.
     * If a negative value is specified, the motor will automatically turn backward.
     *
     * @param degrees Number of degrees to turn for- or backward
     */
    public void turnDegrees(int degrees) throws InterruptedException {
        final int steps = degrees * 512 / 360;
        if (steps >= 0) {
            turnForward(steps);
        } else {
            turnBackward(steps * -1);
        }
    }

    /**
     * Turns the step motor forward for the given amount of steps.
     *
     * @param steps Steps to turn forward
     */
    public void turnForward(int steps) throws InterruptedException {
        turn(steps, stepsForward);
    }

    /**
     * Turns the step motor backward for the given amount of steps.
     *
     * @param steps Steps to turn backward
     */
    public void turnBackward(int steps) throws InterruptedException {
        turn(steps, stepsBackward);
    }

    /**
     * Turns the step motor for the given amount of steps using the provided step data.
     *
     * @param count Amount of steps to be turned
     * @param steps List of steps with associated digital outputs to iterate through
     */
    private void turn(int count, List<Set<DigitalOutput>> steps) throws InterruptedException {
        // Loop to reach desired count of steps
        for (int i = 0; i < count; i++) {
            // Iterate through all known steps
            for (final var step : steps) {
                // Turn all outputs for the current step to HIGH
                for (final var output : step) {
                    output.high();
                }

                // Sleep for the given duration in milliseconds
                sleep(pulseMilliseconds);

                // Turn all outputs for the current step to LOW
                for (final var output : step) {
                    output.low();
                }
            }
        }
    }

    /**
     * Returns an array of all initialized digital outputs for this component
     *
     * @return Digital output instances
     */
    protected DigitalOutput[] getDigitalOutputs() {
        return digitalOutputs;
    }

    /**
     * Builds a new digital output configuration for the GPIO step motor pin
     *
     * @param pi4j    Pi4J context
     * @param address BCM address
     * @return Digital output configuration
     */
    protected DigitalOutputConfig buildDigitalOutputConfig(Context pi4j, int address) {
        return DigitalOutput.newConfigBuilder(pi4j)
            .id("BCM" + address)
            .name("Step Motor @ BCM" + address)
            .address(address)
            .initial(DigitalState.LOW)
            .shutdown(DigitalState.LOW)
            .build();
    }
}
