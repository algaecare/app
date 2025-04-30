package com.algaecare.controller;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;

public class NFCChipControllerTest implements NFCChipListener{
    private final NFCChipController controller = new NFCChipController();


    public static void main(String[] args){

        //<editor-fold desc="Manuel test of listener">
        NFCChipControllerTest test = new NFCChipControllerTest();
        //</editor-fold>

        /*
        //<editor-fold desc="Manuel test of writing an integer">
        NFCChipController newController = new NFCChipController();
        newController.setIntOnChip(12345);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            int integerFromChip = newController.getIntFromChip();
            System.out.println("integer read: " + integerFromChip);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //</editor-fold>
        */
    }

    public NFCChipControllerTest() {
        controller.addListener(this);
        while(true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onNewTagDetected(int detectedData) {
        System.out.println("Object detected: " + detectedData);
    }
}
