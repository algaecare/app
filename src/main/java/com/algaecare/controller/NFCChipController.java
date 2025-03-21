package com.algaecare.controller;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NFCChipController {
    private static final Logger LOGGER = Logger.getLogger(NFCChipController.class.getName());

    ///gets an array of 4 bytes from the NFC chips data block 4.
    /// if return is "null" no NFC reading device or no NFC chip could be found.
    private static byte[] getDataOfChip() throws IOException {
        byte[] responseBytes = null;
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            CardTerminals terminals = factory.terminals();

            for (CardTerminal terminal : terminals.list()) {
                if (terminal.isCardPresent()) {
                    Card card = terminal.connect("*");
                    CardChannel channel = card.getBasicChannel();

                    byte[] command = new byte[]{ (byte) 0xFF, (byte) 0xB0, (byte) 0x04, (byte) 0x4};
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
    private static boolean setDataOfChip(byte[] data) throws IllegalArgumentException{
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

    public static byte[] concatenateByteArrays(byte[] array1, byte[] array2) {
        byte[] result = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
