package com.algaecare.model;

import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;

public class StepMotor {
    private final DigitalOutput pin1;
    private final DigitalOutput pin2;
    private final DigitalOutput pin3;
    private final DigitalOutput pin4;

    public StepMotor(DigitalOutput pin1, DigitalOutput pin2, DigitalOutput pin3, DigitalOutput pin4) {
        this.pin1 = pin1;
        this.pin2 = pin2;
        this.pin3 = pin3;
        this.pin4 = pin4;
    }

    private DigitalOutput getPin(int pinNumber) throws IllegalArgumentException{
        return switch (pinNumber) {
            case 1 -> pin1;
            case 2 -> pin2;
            case 3 -> pin3;
            case 4 -> pin4;
            default -> throw new IllegalArgumentException("Invalid pin number: " + pinNumber);
        };
    }

    public void setPinState(int pinNumber, boolean state) {
        DigitalOutput pin = getPin(pinNumber);
        if (state) {
            pin.state(DigitalState.HIGH);
        } else {
            pin.state(DigitalState.LOW);
        }
    }
}
