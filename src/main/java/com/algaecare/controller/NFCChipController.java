package com.algaecare.controller;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NFCChipController {
    private static final Logger LOGGER = Logger.getLogger(NFCChipController.class.getName());
    private List<NFCChipListener> listeners = new ArrayList<>();

    public NFCChipController() {
        try {
            TerminalFactory.getDefault();
            LOGGER.info("PCSC system initialized successfully");
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize PCSC system. Is pcscd running? Error: " + e.getMessage());
            // Continue anyway - we'll handle errors in the polling thread
        }

        Thread asyncThread = new Thread(() -> {
            LOGGER.info("Starting NFCChip Controller...");
            int retryCount = 0;
            while (true) {
                try {
                    int integer = getIntFromChip();
                    retryCount = 0; // Reset counter on success
                    for (NFCChipListener listener : listeners) {
                        listener.onNewTagDetected(integer);
                    }
                } catch (IOException e) {
                    retryCount++;
                    if (retryCount % 60 == 0) { // Log every ~30 seconds
                        LOGGER.warning("NFC reader not available. Retry count: " + retryCount);
                    }
                } finally {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        LOGGER.severe("NFC Chip controller interrupted");
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        });

        asyncThread.setDaemon(true);
        asyncThread.start();
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
            LOGGER.log(Level.SEVERE, "Unexpected error reading NFC chip", e);
            throw new IOException("Unexpected error reading NFC chip: " + e.getMessage());
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    public void addListener(NFCChipListener toAdd) {
        listeners.add(toAdd);
    }

    public void removeListener(NFCChipListener toRemove) {
        listeners.remove(toRemove);
    }
}
