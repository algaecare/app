package com.algaecare.controller;

import com.algaecare.model.GameState;
import java.util.ArrayList;
import java.util.List;

public interface GameStateEventManager {
    void onGameStateChanged(GameState oldState, GameState newState);

    interface EventEmitter {
        void emitGameStateChange(GameState newState);
    }

    abstract class Notifier implements GameStateEventManager, EventEmitter {
        private final List<GameStateEventManager> listeners = new ArrayList<>();
        protected GameState currentState;

        public void addGameStateChangeListener(GameStateEventManager listener) {
            listeners.add(listener);
        }

        public void removeGameStateChangeListener(GameStateEventManager listener) {
            listeners.remove(listener);
        }

        protected void notifyGameStateChanged(GameState oldState, GameState newState) {
            listeners.forEach(listener -> listener.onGameStateChanged(oldState, newState));
        }

        @Override
        public void emitGameStateChange(GameState newState) {
            GameState oldState = currentState;
            currentState = newState;
            notifyGameStateChanged(oldState, newState);
        }
    }
}