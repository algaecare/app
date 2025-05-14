package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.algaecare.model.Environment;
import com.algaecare.model.GameState;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentTest {

    private Environment environment;

    @BeforeEach
    void setUp() {
        environment = new Environment(50);
    }

    @Test
    void shouldInitializeWithCorrectAlgaeLevel() {
        assertEquals(50, environment.getAlgaeLevel());
    }

    @Test
    void shouldResetToDefaultLevel() {
        // given
        environment.updateEnvironment(GameState.OBJECT_AIRPLANE);

        // when
        environment.reset();

        // then
        assertEquals(50, environment.getAlgaeLevel());
    }

    @ParameterizedTest
    @EnumSource(value = GameState.class, names = { "OBJECT_BICYCLE", "OBJECT_TRASH_GRABBER" })
    void shouldIncreaseAlgaeLevelForPositiveObjects(GameState state) {
        // given
        int initialLevel = environment.getAlgaeLevel();

        // when
        environment.updateEnvironment(state);

        // then
        assertTrue(environment.getAlgaeLevel() > initialLevel);
    }

    @ParameterizedTest
    @EnumSource(value = GameState.class, names = { "OBJECT_AIRPLANE", "OBJECT_GARBAGE_BAG" })
    void shouldDecreaseAlgaeLevelForNegativeObjects(GameState state) {
        // given
        int initialLevel = environment.getAlgaeLevel();

        // when
        environment.updateEnvironment(state);

        // then
        assertTrue(environment.getAlgaeLevel() < initialLevel);
    }

    @Test
    void shouldNotExceedMaximumLevel() {
        // given
        environment = new Environment(95);

        // when
        environment.updateEnvironment(GameState.OBJECT_BICYCLE);
        environment.updateEnvironment(GameState.OBJECT_BICYCLE);

        // then
        assertEquals(100, environment.getAlgaeLevel());
    }

    @Test
    void shouldNotFallBelowMinimumLevel() {
        // given
        environment = new Environment(5);

        // when
        environment.updateEnvironment(GameState.OBJECT_AIRPLANE);
        environment.updateEnvironment(GameState.OBJECT_AIRPLANE);

        // then
        assertEquals(0, environment.getAlgaeLevel());
    }

    @Test
    void shouldHandleInvalidGameState() {
        // given
        int initialLevel = environment.getAlgaeLevel();

        // when
        environment.updateEnvironment(GameState.TITLE);

        // then
        assertEquals(initialLevel, environment.getAlgaeLevel());
    }
}