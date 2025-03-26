package com.algaecare.view.layer;

import com.algaecare.view.sequences.ImageSequence;

public class DynamicLayer extends Layer {

    public enum DynamicState {
        HIDDEN, INTRO, IDLE, OUTRO
    }

    private final ImageSequence introSequence;
    private final ImageSequence idleSequence;
    private final ImageSequence outroSequence;
    private DynamicState currentState = DynamicState.HIDDEN;

    public DynamicLayer(String layerName) {
        super(LayerType.DYNAMIC);

        // Load the three sequences based on a folder naming convention
        this.introSequence = new ImageSequence("/" + layerName + "/intro", 24);
        this.idleSequence = new ImageSequence("/" + layerName + "/idle", 24);
        this.outroSequence = new ImageSequence("/" + layerName + "/outro", 24);
        // Start hidden
        setStateInternal(DynamicState.HIDDEN);
    }

    public void showLayer() {
        if (currentState == DynamicState.HIDDEN || currentState == DynamicState.OUTRO) {
            setStateInternal(DynamicState.INTRO);
        }
    }

    public void hideLayer() {
        if (currentState == DynamicState.IDLE) {
            idleSequence.setOnCycleComplete(() -> {
                idleSequence.setOnCycleComplete(null);
                setStateInternal(DynamicState.OUTRO);
            });
        } else if (currentState == DynamicState.INTRO) {
            introSequence.setOnCycleComplete(() -> {
                introSequence.setOnCycleComplete(null);
                setStateInternal(DynamicState.OUTRO);
            });
        }
    }

    private void setStateInternal(DynamicState newState) {
        // Clear any active sequences and pause them
        getChildren().clear();
        introSequence.pause();
        idleSequence.pause();
        outroSequence.pause();

        currentState = newState;
        switch (newState) {
            case HIDDEN:
                // No sequence is added, layer remains clear.
                break;
            case INTRO:
                getChildren().add(introSequence);
                introSequence.play();
                // When the intro finishes a full cycle, switch automatically to idle.
                introSequence.setOnCycleComplete(() -> {
                    introSequence.setOnCycleComplete(null);
                    setStateInternal(DynamicState.IDLE);
                });
                break;
            case IDLE:
                getChildren().add(idleSequence);
                idleSequence.play();
                // Idle normally loops without setting a callback.
                break;
            case OUTRO:
                getChildren().add(outroSequence);
                outroSequence.play();
                // When the outro completes a full cycle, clear the layer (hidden).
                outroSequence.setOnCycleComplete(() -> {
                    outroSequence.setOnCycleComplete(null);
                    getChildren().clear();
                    currentState = DynamicState.HIDDEN;
                });
                break;
        }
    }

    public DynamicState getState() {
        return currentState;
    }
}