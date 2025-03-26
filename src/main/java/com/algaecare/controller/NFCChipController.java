package com.algaecare.controller;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
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
        Thread asyncThread = new Thread(() -> {
            LOGGER.log(Level.INFO, "Starting NFCChip Controller...");
            while (true) {
                try {
                    int integer = getIntFromChip();
                    for (NFCChipListener listener : listeners) {
                        listener.onNewTagDetected(integer);
                    }
                } catch (IOException ignored) {
                    //does nothing
                } finally {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        LOGGER.log(Level.SEVERE, "NFC Chip controller async listening loop interrupted.", e);
                    }
                }
            }
        });

        asyncThread.setDaemon(true);
        asyncThread.start();
    }

    //<editor-fold desc="IO to chip">
    ///gets an array of 4 bytes from the NFC chips data block 4.
    /// if return is "null" no NFC reading device or no NFC chip could be found.
    public byte[] getDataOfChip() throws IOException {
        byte[] responseBytes = null;
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            CardTerminals terminals = factory.terminals();

            for (CardTerminal terminal : terminals.list()) {
                if (terminal.isCardPresent()) {
                    Card card = terminal.connect("*");
                    CardChannel channel = card.getBasicChannel();

                    byte[] command = new byte[]{ (byte) 0xFF, (byte) 0xB0, (byte) 0x04, (byte) 0x04};
                    LOGGER.log(Level.INFO, "Sending command to NFC tag: " + bytesToHex(command));
                    ResponseAPDU response = channel.transmit(new CommandAPDU(command));
                    String responseString;
                    responseString = bytesToHex(response.getBytes());
                    LOGGER.log(Level.INFO, "Receiving data from NFC tag: " + responseString);

                    byte[] failureCode = new byte[]{ (byte) 0x63, (byte) 0x00};
                    if(Arrays.equals(Arrays.copyOfRange(response.getBytes(), 0, 2), failureCode)) {
                        throw new IOException("NFC-chip could not be read.");
                    } else {
                        responseBytes = Arrays.copyOfRange(response.getBytes(), 0, 4);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error reading NFC tag.", e);
            throw new IOException("NFC-chip could not be read.");
        }
        return responseBytes;
    }

    ///sets 4 bytes of data on data block number 4 of the NFC chip.
    ///byte[] data must be length of 4.
    /// returns a boolean value based on success of operation.
    public boolean setDataOfChip(byte[] data) throws IllegalArgumentException{
        if(data.length != 4) {
            throw new IllegalArgumentException("data block must consist of 4 bytes.");
        }
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            CardTerminals terminals = factory.terminals();

            for (CardTerminal terminal : terminals.list()) {
                if (terminal.isCardPresent()) {
                    Card card = terminal.connect("*");
                    CardChannel channel = card.getBasicChannel();

                    byte[] command = new byte[]{ (byte) 0xFF, (byte) 0xD6, (byte) 0x00, (byte) 0x04, (byte) 0x04};

                    byte[] command2 = concatenateByteArrays(command, data);
                    ResponseAPDU response = channel.transmit(new CommandAPDU(command2));
                    LOGGER.log(Level.INFO, "Sending command to NFC tag: " + bytesToHex(command));
                    String responseString = bytesToHex(response.getBytes());
                    LOGGER.log(Level.INFO, "Receiving data from NFC tag: " + responseString);

                    byte[] successCode = new byte[]{ (byte) 0x90, (byte) 0x00};
                    if(Arrays.equals(response.getBytes(), successCode)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error writing NFC tag.", e);
        }

        return false;
    }

    ///Get an integer from the 4 byte codeblock on the NFC tag
    public int getIntFromChip() throws IOException {
        try {
            byte[] dataRaw = getDataOfChip();
            if(dataRaw != null && dataRaw.length == 4) {
                return ((0xFF & dataRaw[0]) << 24) | ((0xFF & dataRaw[1]) << 16) |
                    ((0xFF & dataRaw[2]) << 8) | (0xFF & dataRaw[3]);
            }
        } catch (IOException e) {
            throw new IOException();
        }
        throw new IOException();
    }

    ///Set an integer on the 4 byte codeblock on the NFC tag
    public boolean setIntOnChip(int integer) {
        byte[] dataRaw = ByteBuffer.allocate(4).putInt(integer).array();
        return setDataOfChip(dataRaw);
    }
    //</editor-fold>

    private byte[] concatenateByteArrays(byte[] array1, byte[] array2) {
        byte[] result = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
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
