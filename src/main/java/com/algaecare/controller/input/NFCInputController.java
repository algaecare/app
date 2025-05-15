package com.algaecare.controller.input;

import com.algaecare.controller.GameStateEventManager;
import com.algaecare.controller.output.StepMotorController;
import com.algaecare.model.GameState;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import javax.smartcardio.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NFCInputController implements GameStateEventManager {
    private static final Logger LOGGER = Logger.getLogger(NFCInputController.class.getName());
    private final GameStateEventManager.EventEmitter eventEmitter;
    private final HashMap<Integer, GameState> nfcChipCodeHashmap;
    private static final String SETTINGS_CSV = "/nfc.csv";
    private final Context pi4j;
    private GameState currentState;
    private DigitalInput leverButton;
    private static final int LEVER_PIN = 16;
    private static final int DEBOUNCE_TIME = 100; // milliseconds
    public boolean isLeverPressed = false;
    private AnimationTimer leverTimer;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final StepMotorController stepMotorController;

    public NFCInputController(GameStateEventManager.EventEmitter eventEmitter, Context pi4j, StepMotorController stepMotorController) {
        this.eventEmitter = eventEmitter;
        this.nfcChipCodeHashmap = new HashMap<>();

        // Initialize Pi4J
        this.pi4j = pi4j;

        this.stepMotorController = stepMotorController;

        // Initialize NFC system
        try {
            TerminalFactory.getDefault();
            LOGGER.info("PCSC system initialized successfully");
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize PCSC system. Is pcscd running? Error: " + e.getMessage());
        }

        // Initialize lever button
        initializeLeverButton();

        // Load NFC mappings
        readNfcChipList();

        // Start listening for lever input
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::startLeverListener);
        } else {
            startLeverListener();
        }
    }

    private void initializeLeverButton() {
        var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
                .id("LEVER")
                .name("Lever Button")
                .address(LEVER_PIN)
                .pull(PullResistance.PULL_DOWN);

        leverButton = pi4j.create(buttonConfig);
        LOGGER.info("Lever button initialized on pin " + LEVER_PIN);
    }

    private void startLeverListener() {
        leverTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate < DEBOUNCE_TIME * 1_000_000)
                    return;
                lastUpdate = now;

                if (leverButton.isHigh()) {
                    if (!isLeverPressed) {
                        isLeverPressed = true;
                        LOGGER.info("Lever pressed - scheduling NFC read");
                        executor.submit(this::readAndEmitNfc);
                    }
                }
            }

            private void readAndEmitNfc() {
                try {
                    int tagId = getIntFromChip();
                    LOGGER.info("NFC tag read in bg thread: " + tagId);
                    GameState state = nfcChipCodeHashmap.get(tagId);
                    if (state != null) {
                        Platform.runLater(() -> {
                            LOGGER.info("Emitting game state on FX thread: " + state);
                            eventEmitter.emitGameStateChange(state);
                        });
                    } else {
                        LOGGER.warning("No mapping for tag: " + tagId);
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "NFC read failed", e);
                }
            }
        };
        leverTimer.start();
        LOGGER.info("Started JavaFX lever listener (non-blocking)");
    }

    public void shutdown() {
        if (leverTimer != null) {
            leverTimer.stop();
        }
    }

    private boolean ensurePCSCAvailable() {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            CardTerminals terminals = factory.terminals();
            return !terminals.list().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public byte[] getDataOfChip() throws IOException, CardException {
        if (!ensurePCSCAvailable()) {
            LOGGER.warning("PCSC system not available");
            throw new IOException("PCSC system not available");
        }

        TerminalFactory factory = TerminalFactory.getDefault();
        CardTerminals terminals = factory.terminals();
        List<CardTerminal> terminalList = terminals.list();

        if (terminalList.isEmpty()) {
            LOGGER.warning("No NFC terminals found");
            throw new IOException("No NFC terminals available");
        }

        for (CardTerminal terminal : terminalList) {
            if (!terminal.isCardPresent()) {
                continue;
            }

            Card card = null;
            try {
                card = terminal.connect("*");
                CardChannel channel = card.getBasicChannel();

                // Command to read 4 bytes from block 4
                byte[] command = { (byte) 0xFF, (byte) 0xB0, (byte) 0x04, (byte) 0x04 };
                LOGGER.log(Level.FINE, "Sending command to NFC tag: {0}", bytesToHex(command));

                ResponseAPDU response = channel.transmit(new CommandAPDU(command));
                byte[] responseData = response.getBytes();

                LOGGER.log(Level.FINE, "Received response: {0}", bytesToHex(responseData));

                // Check response status
                if (response.getSW() != 0x9000) {
                    LOGGER.warning("NFC read failed with status: " + Integer.toHexString(response.getSW()));
                    continue;
                }

                // Validate response length
                if (responseData.length < 4) {
                    LOGGER.warning("Invalid response length: " + responseData.length);
                    continue;
                }

                return Arrays.copyOfRange(responseData, 0, 4);
            } catch (CardException e) {
                LOGGER.log(Level.WARNING, "Failed to read card in terminal: " + terminal.getName(), e);
                // Continue to next terminal if available
            } finally {
                if (card != null) {
                    try {
                        card.disconnect(false);
                    } catch (CardException e) {
                        LOGGER.log(Level.WARNING, "Failed to disconnect card in terminal: " + terminal.getName(), e);
                    }
                }
            }
        }
        throw new IOException("Could not read NFC tag from any available terminal");
    }

    public int getIntFromChip() throws IOException {
        try {
            byte[] dataRaw = getDataOfChip();
            if (dataRaw == null) {
                throw new IOException("No data received from NFC chip");
            }
            if (dataRaw.length != 4) {
                throw new IOException("Invalid data length: expected 4 bytes, got " + dataRaw.length);
            }

            return ByteBuffer.wrap(dataRaw).getInt();

        } catch (CardException e) {
            LOGGER.log(Level.WARNING, "Failed to read NFC chip", e);
            throw new IOException("Failed to read NFC chip: " + e.getMessage());
        } catch (Exception e) {
            // ignore other exceptions
        }
        return -1; // or throw new IOException("Unknown error occurred");
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    public void readNfcChipList() {
        try (InputStream is = getClass().getResourceAsStream(SETTINGS_CSV)) {
            if (is == null) {
                LOGGER.severe("NFC Settings file not found: " + SETTINGS_CSV);
                return;
            }

            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                CSVFormat format = CSVFormat.DEFAULT.builder()
                        .setDelimiter(';')
                        .setSkipHeaderRecord(true)
                        .setTrim(true)
                        .build();

                var parser = format.parse(reader);

                for (CSVRecord record : parser) {
                    try {
                        if (record.size() < 2) {
                            LOGGER.warning("Invalid record format: " + record);
                            continue;
                        }

                        int id = Integer.parseInt(record.get(0));
                        String stateStr = record.get(1).trim();

                        try {
                            GameState state = GameState.valueOf(stateStr);
                            nfcChipCodeHashmap.put(id, state);
                            LOGGER.fine("Mapped NFC ID " + id + " to state " + state);
                        } catch (IllegalArgumentException e) {
                            LOGGER.warning("Invalid game state in CSV: " + stateStr);
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.warning("Invalid NFC ID format in record: " + record.get(0));
                    }
                }

                if (nfcChipCodeHashmap.isEmpty()) {
                    LOGGER.warning("No valid NFC mappings found in " + SETTINGS_CSV);
                } else {
                    LOGGER.info("Loaded " + nfcChipCodeHashmap.size() + " NFC mappings");
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Failed to read NFC settings: " + e.getMessage());
        }
    }

    @Override
    public GameState getCurrentState() {
        return currentState;
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        if((oldState == GameState.GAMEPLAY || oldState == GameState.TITLE ||
            oldState == GameState.AXOLOTL_INTRODUCTION) && newState != oldState) {
            new Thread(() ->{
                stepMotorController.openTrapDoor();
            }).start();
        }
        this.currentState = newState;
        this.isLeverPressed = false; // Reset lever state after game state change
        LOGGER.fine("NFCInput Controller state changed from " + oldState + " to " + newState);
    }
}
