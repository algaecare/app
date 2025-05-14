package model;

import org.junit.jupiter.api.*;

import com.algaecare.model.TextLayerData;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TextLayerDataTest {

    @Test
    void fetchAllShouldReturnNonEmptyMap() {
        Map<String, String> textData = TextLayerData.fetchAll();
        assertNotNull(textData, "fetchAll should not return null");
        assertFalse(textData.isEmpty(), "fetchAll should return a non-empty map if CSV is present and valid");
    }

    @Test
    void getTextShouldReturnTextForValidId() {
        Map<String, String> textData = TextLayerData.fetchAll();
        String anyId = textData.keySet().stream().findFirst().orElse(null);
        assertNotNull(anyId, "There should be at least one ID in the text data");
        String text = TextLayerData.getText(anyId);
        assertNotNull(text, "getText should not return null for a valid ID");
        assertNotEquals("Text not found", text, "getText should not return default message for a valid ID");
    }

    @Test
    void getTextShouldReturnDefaultForInvalidId() {
        String text = TextLayerData.getText("NON_EXISTENT_ID");
        assertEquals("Text not found", text, "getText should return default message for an invalid ID");
    }
}