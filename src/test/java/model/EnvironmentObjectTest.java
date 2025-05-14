package model;

import org.junit.jupiter.api.Test;

import com.algaecare.model.EnvironmentObject;
import com.algaecare.model.GameState;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentObjectTest {

    @Test
    void constructorAndGettersShouldWork() {
        // given
        GameState state = GameState.OBJECT_BICYCLE;
        int algaeChange = 10;

        // when
        EnvironmentObject obj = new EnvironmentObject(state, algaeChange);

        // then
        assertEquals(state, obj.getObjectID());
        assertEquals(algaeChange, obj.getAlgaeChange());
    }
}