package com.algaecare.model;

import javafx.scene.input.KeyCode;

public enum InputAction {
    ALGAE_1("Algae Type 1", "A1-RFID-TAG", KeyCode.DIGIT1),
    ALGAE_2("Algae Type 2", "A2-RFID-TAG", KeyCode.DIGIT2),
    ALGAE_3("Algae Type 3", "A3-RFID-TAG", KeyCode.DIGIT3),
    CONFIRM("Confirm Selection", "CONFIRM-RFID", KeyCode.SPACE),
    CANCEL("Cancel Action", "CANCEL-RFID", KeyCode.ESCAPE);

    private final String description;
    private final String rfidTag;
    private final KeyCode keyboardKey;

    InputAction(String description, String rfidTag, KeyCode keyboardKey) {
        this.description = description;
        this.rfidTag = rfidTag;
        this.keyboardKey = keyboardKey;
    }

    public String getDescription() {
        return description;
    }

    public String getRfidTag() {
        return rfidTag;
    }

    public KeyCode getKeyboardKey() {
        return keyboardKey;
    }

    public static InputAction fromRfidTag(String tag) {
        for (InputAction action : values()) {
            if (action.getRfidTag().equals(tag)) {
                return action;
            }
        }
        return null;
    }

    public static InputAction fromKeyCode(KeyCode code) {
        for (InputAction action : values()) {
            if (action.getKeyboardKey() == code) {
                return action;
            }
        }
        return null;
    }
}