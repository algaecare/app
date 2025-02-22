package com.algaecare.controller.input;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.serial.*;
import com.algaecare.model.InputAction;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.logging.Level;

public class RFIDController implements InputController {
    private static final Logger LOGGER = Logger.getLogger(RFIDController.class.getName());
    private static final String SERIAL_PORT = "/dev/ttyAMA0";
    private static final int BAUD_RATE = 9600;

    private Context pi4j;
    private Serial serial;
    private Consumer<InputAction> callback;
    private boolean isInitialized;
    private StringBuilder tagBuffer;

    @Override
    public void initialize() {
        try {
            pi4j = Pi4J.newAutoContext();
            initializeSerialPort();
            tagBuffer = new StringBuilder();
            isInitialized = true;
            LOGGER.info("RFID controller initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize RFID controller", e);
            throw new RuntimeException("RFID initialization failed", e);
        }
    }

    private void initializeSerialPort() {
        serial = pi4j.create(Serial.newConfigBuilder(pi4j)
                .id("rfidReader")
                .name("RFID Reader")
                .device(SERIAL_PORT)
                .baud(BAUD_RATE)
                .dataBits_8()
                .parity(Parity.NONE)
                .stopBits_1()
                .flowControl(FlowControl.NONE)
                .build());

        serial.addListener(event -> {
            try {
                String data = new String(event.getBytes());
                processRFIDData(data);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error processing RFID data", e);
            }
        });
    }

    private void processRFIDData(String data) {
        tagBuffer.append(data);
        if (data.contains("\n")) {
            String tag = tagBuffer.toString().trim();
            tagBuffer.setLength(0);

            InputAction action = InputAction.fromRfidTag(tag);
            if (action != null && callback != null) {
                handleInput(action);
            } else {
                LOGGER.warning("Unknown RFID tag received: " + tag);
            }
        }
    }

    private void handleInput(InputAction action) {
        try {
            callback.accept(action);
            LOGGER.info("RFID input processed: " + action.getDescription());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing RFID input: " + action, e);
        }
    }

    @Override
    public void setInputCallback(Consumer<InputAction> callback) {
        this.callback = callback;
        LOGGER.info("RFID callback registered");
    }

    @Override
    public void cleanup() {
        if (isInitialized) {
            if (serial != null) {
                serial.close();
            }
            if (pi4j != null) {
                pi4j.shutdown();
            }
            isInitialized = false;
            LOGGER.info("RFID controller cleaned up");
        }
    }
}