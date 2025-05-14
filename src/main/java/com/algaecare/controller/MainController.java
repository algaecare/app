package com.algaecare.controller;

import com.algaecare.controller.input.KeyboardInputController;
import com.algaecare.controller.input.NFCInputController;
import com.algaecare.controller.input.TimeController;
import com.algaecare.controller.output.LedController;
import com.algaecare.controller.output.ScreenController;
import com.algaecare.controller.output.StepMotorController;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.platform.Platform;
import com.pi4j.platform.Platforms;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.algaecare.model.Environment;
import com.algaecare.model.GameState;

public class MainController
        implements GameStateEventManager, GameStateEventManager.EventEmitter {
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());
    private final List<GameStateEventManager> listeners = new ArrayList<>();
    private GameState currentState;
    private Context pi4j;

    public MainController(Stage stage) {
        // Initialize model
        Environment environment = new Environment(50);

        // Initialize controllers
        TimeController timeController = new TimeController(this, environment);
        KeyboardInputController keyboardInputController = new KeyboardInputController(stage, this);
        ScreenController screenController = new ScreenController(stage, this, environment, timeController);

        addGameStateChangeListener(timeController);
        addGameStateChangeListener(screenController);
        addGameStateChangeListener(keyboardInputController);

        if (getPlatform().equals("raspberrypi")) {
            StepMotorController motorController = new StepMotorController(environment, pi4j);
            NFCInputController nfcController = new NFCInputController(this, pi4j, motorController);
            LedController ledController = new LedController(environment, pi4j);

            addGameStateChangeListener(nfcController);
            addGameStateChangeListener(ledController);
            addGameStateChangeListener(motorController);
        }

        currentState = GameState.TITLE;
        notifyGameStateChanged(null, currentState);
    }

    public void addGameStateChangeListener(GameStateEventManager listener) {
        listeners.add(listener);
    }

    @Override
    public void emitGameStateChange(GameState newState) {
        GameState oldState = currentState;
        currentState = newState;
        notifyGameStateChanged(oldState, newState);
    }

    @Override
    public GameState getCurrentState() {
        return currentState;
    }

    @Override
    public void onGameStateChanged(GameState oldState, GameState newState) {
        return;
    }

    protected void notifyGameStateChanged(GameState oldState, GameState newState) {
        listeners.forEach(listener -> listener.onGameStateChanged(oldState, newState));
    }

    public String getPlatform() {
        String osArch = System.getProperty("os.arch").toLowerCase();
        String osName = System.getProperty("os.name").toLowerCase();

        if (osArch.contains("aarch64") && !osName.contains("mac")) {
            pi4j = Pi4J.newAutoContext();
            Platforms platforms = pi4j.platforms();
            Platform platform = platforms.get("raspberrypi");
            LOGGER.log(Level.INFO, "Running on ARM platform: " + platform.id());
            return platform.id();
        } else {
            LOGGER.warning("Not running on ARM platform - GPIO control disabled");
            return "none";
        }
    }
}