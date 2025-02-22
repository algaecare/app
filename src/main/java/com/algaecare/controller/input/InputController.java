package com.algaecare.controller.input;

import java.util.function.Consumer;
import com.algaecare.model.InputAction;

public interface InputController {
    void initialize();

    void setInputCallback(Consumer<InputAction> callback);

    void cleanup();
}