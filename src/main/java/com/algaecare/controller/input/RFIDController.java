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
    private static final String CHARSET = "UTF-8";
    private static final char END_MARKER = '\n';
    private static final int MAX_TAG_LENGTH = 12; // Standard RFID tag length

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
        SerialConfig config = Serial.newConfigBuilder(pi4j)
                .id("rfidReader")
                .name("RFID Reader")
                .device(SERIAL_PORT)
                .baud(BAUD_RATE)
                .dataBits_8()
                .parity(Parity.NONE)
                .stopBits_1()
                .flowControl(FlowControl.NONE)
                .build();

        serial = pi4j.create(config);

        /*
         * // Correct listener implementation for Pi4J v2
         * serial.addListener(new SerialListener() {
         * 
         * @Override
         * public void onSerialData(SerialData event) {
         * try {
         * String data = new String(event.getData(), CHARSET);
         * processRFIDData(data);
         * } catch (Exception e) {
         * LOGGER.log(Level.WARNING, "Error processing RFID data: " + e.getMessage(),
         * e);
         * tagBuffer.setLength(0); // Reset buffer on error
         * }
         * }
         * });
         */

        serial.open();
    }

    private void processRFIDData(String data) {
        try {
            for (char c : data.toCharArray()) {
                if (c == END_MARKER) {
                    processCompleteTag();
                } else if (Character.isLetterOrDigit(c)) {
                    // Only append valid characters
                    if (tagBuffer.length() < MAX_TAG_LENGTH) {
                        tagBuffer.append(c);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error in RFID data processing", e);
            tagBuffer.setLength(0);
        }
    }

    private void processCompleteTag() {
        String tag = tagBuffer.toString().trim();
        tagBuffer.setLength(0);

        if (tag.length() > 0) {
            LOGGER.info("Read RFID tag: " + tag);
            InputAction action = InputAction.fromRfidTag(tag);
            if (action != null) {
                if (callback != null) {
                    handleInput(action);
                } else {
                    LOGGER.warning("No callback registered for RFID input");
                }
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