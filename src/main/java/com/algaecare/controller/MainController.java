package com.algaecare.controller;

import com.algaecare.model.TextLayerData;
import javafx.stage.Stage;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.algaecare.model.Environment;
import com.algaecare.model.GameState;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class MainController implements GameStateEventManager, GameStateEventManager.EventEmitter, NFCChipListener, LeverInputListener {
    private final List<GameStateEventManager> listeners = new ArrayList<>();
    private GameState currentState;
    private GameState nextState;
    private final Environment environment;

    private NFCChipController nfcController;
    private LedController ledController;
    private StepMotorController motorController;

    private HashMap<Integer, GameState> nfcChipCodeHashmap;

    private static final String SETTINGS_CSV = "/NfConfigs.csv";

    public MainController(Stage stage) {
        // Initialize model
        environment = new Environment(50);

        // Initialize controllers
        KeyboardInputController keyboardInputController = new KeyboardInputController(stage, this);
        ScreenController screenController = new ScreenController(stage, this, this.environment);
        nfcController = new NFCChipController();
        ledController = new LedController(environment);
        motorController = new StepMotorController(environment);

        // Wire up event chain
        addGameStateChangeListener(screenController);
        addGameStateChangeListener(keyboardInputController);

        // Set initial state
        currentState = GameState.TITLE;
        notifyGameStateChanged(null, currentState);

        readNfcChipList();
    }

    public void readNfcChipList() {
        HashMap<Integer, GameState> nfcChipCodes = new HashMap<>();

        try (InputStream is = TextLayerData.class.getResourceAsStream(SETTINGS_CSV)) {
            assert is != null;
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setDelimiter(';')
                    .build();

                var parser = format.parse(reader);

                for (CSVRecord record : parser) {
                    if (record.size() < 2) {
                        //LOGGER.warning("Skipping malformed record: " + record); //TODO log
                        continue;
                    }
                    String id = record.get(0);
                    String text = record.get(1);
                    GameState[] gameStates = {GameState.AXOLOTL_INTRODUCTION, GameState.OBJECT_GARBAGE_BAG,
                        GameState.OBJECT_CAR, GameState.OBJECT_AIRPLANE, GameState.OBJECT_SHOPPING_BASKET_INTERNATIONAL,
                        GameState.OBJECT_RECYCLING_BIN, GameState.OBJECT_TRAIN, GameState.OBJECT_SHOPPING_BASKET_LOCAL,
                        GameState.OBJECT_BICYCLE, GameState.OBJECT_TRASH_GRABBER};
                    for (int i = 0; i < gameStates.length; i++) {
                        if (gameStates[i].name().equals(text)) {
                            nfcChipCodes.put(Integer.parseInt(id), gameStates[i]);
                            break;
                        }
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        nfcChipCodeHashmap = nfcChipCodes;
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

    @Override
    public void onNewTagDetected(int detectedData) {
        GameState object = nfcChipCodeHashmap.get(detectedData);
        if(currentState == GameState.GAMEPLAY) {
            nextState = object;
        }
        else if(currentState == GameState.TITLE && object == GameState.AXOLOTL_INTRODUCTION) {
            nextState = object;
        }
    }

    @Override
    public void onLeverInput() {
        if(currentState == GameState.GAMEPLAY && nextState != null) {
            motorController.openTrapDoor();
            GameState oldState = currentState;
            currentState = nextState;
            nextState = null;
            notifyGameStateChanged(oldState, currentState);
        }
    }
}