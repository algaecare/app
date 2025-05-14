package controller;

import com.algaecare.controller.GameStateEventManager;
import com.algaecare.model.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateEventManagerTest {

    static class DummyManager implements GameStateEventManager {
        private GameState state = GameState.TITLE;

        @Override
        public void onGameStateChanged(GameState oldState, GameState newState) {
            state = newState;
        }

        @Override
        public GameState getCurrentState() {
            return state;
        }
    }

    static class DummyEmitter implements GameStateEventManager.EventEmitter {
        public GameState emittedState = null;

        @Override
        public void emitGameStateChange(GameState newState) {
            emittedState = newState;
        }
    }

    @Test
    void onGameStateChangedShouldUpdateState() {
        DummyManager manager = new DummyManager();
        manager.onGameStateChanged(GameState.TITLE, GameState.GAMEPLAY);
        assertEquals(GameState.GAMEPLAY, manager.getCurrentState());
    }

    @Test
    void eventEmitterShouldEmitState() {
        DummyEmitter emitter = new DummyEmitter();
        emitter.emitGameStateChange(GameState.ENDSCREEN_POSITIVE);
        assertEquals(GameState.ENDSCREEN_POSITIVE, emitter.emittedState);
    }
}