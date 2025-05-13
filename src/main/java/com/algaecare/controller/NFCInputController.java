package com.algaecare.controller;

import com.algaecare.model.GameState;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
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

    public NFCInputController(GameStateEventManager.EventEmitter eventEmitter, Context pi4j) {
        this.eventEmitter = eventEmitter;
        this.nfcChipCodeHashmap = new HashMap<>();

        // Initialize Pi4J
        this.pi4j = pi4j;

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
        startLeverListener();
    }

    private void initializeLeverButton() {
        var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
                .id("LEVER")
                .name("Lever Button")
                .address(LEVER_PIN)
                .pull(PullResistance.PULL_DOWN)
                .provider("pigpio-digital-input");

        leverButton = pi4j.create(buttonConfig);
        LOGGER.info("Lever button initialized on pin " + LEVER_PIN);
    }

    private void startLeverListener() {
        Thread asyncThread = new Thread(() -> {
            LOGGER.info("Starting lever input listener...");
            while (true) {
                if (leverButton.isHigh()) {
                    LOGGER.info("Lever pressed - checking for NFC tag");
                    processNfcTag();
                }
                try {
                    Thread.sleep(DEBOUNCE_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.warning("Lever listener interrupted");
                    break;
                }
            }
        });
        asyncThread.setDaemon(true);
        asyncThread.start();
    }

    private void processNfcTag() {
        try {
            int tagId = getIntFromChip();
            LOGGER.info("NFC tag detected with ID: " + tagId);

            GameState matchingState = nfcChipCodeHashmap.get(tagId);
            if (matchingState != null) {
                LOGGER.info("Emitting game state: " + matchingState);
                eventEmitter.emitGameStateChange(matchingState);
            } else {
                LOGGER.warning("No matching game state found for tag ID: " + tagId);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to process NFC tag", e);
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
        this.currentState = newState;
        LOGGER.fine("NFCInput Controller state changed from " + oldState + " to " + newState);
    }
}
