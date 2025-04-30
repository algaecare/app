package com.algaecare.controller;

import com.algaecare.model.GameState;

public interface NFCChipListener {
    void onNewTagDetected(int detectedData);
}
