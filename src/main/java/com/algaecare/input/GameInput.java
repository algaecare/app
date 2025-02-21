package com.algaecare.input;

public interface GameInput {
    void initialize();

    void setInputCallback(Runnable callback);

    void cleanup();
}