package com.algaecare.model;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextLayerData {
    private static final Logger LOGGER = Logger.getLogger(TextLayerData.class.getName());
    private static final String TEXT_LAYERS_CSV = "/text_layers.csv";
    private static final String SETTINGS_CSV = "/settings.csv";
    private static Language defaultLanguage;

    private enum Language {
        DE, FR, IT
    }

    static {
        loadDefaultLanguage();
    }

    private static void loadDefaultLanguage() {
        try (InputStream is = TextLayerData.class.getResourceAsStream(SETTINGS_CSV)) {
            assert is != null;
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

                Iterable<CSVRecord> records = CSVFormat.RFC4180.builder()
                        .setHeader("name", "value")
                        .setSkipHeaderRecord(true)
                        .get()
                        .parse(reader);

                for (CSVRecord record : records) {
                    if ("DEFAULT_LANGUAGE".equals(record.get("name"))) {
                        defaultLanguage = Language.valueOf(record.get("value"));
                        return;
                    }
                }
                LOGGER.warning("Default language not found in settings, using DE");
                defaultLanguage = Language.DE;

            }
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Failed to load default language, using DE", e);
            defaultLanguage = Language.DE;
        }
    }

    public static Map<String, String> fetchAll() {
        Map<String, String> textData = new HashMap<>();
        String languageColumn = defaultLanguage.name().toLowerCase();

        try (InputStream is = TextLayerData.class.getResourceAsStream(TEXT_LAYERS_CSV)) {
            assert is != null;
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                CSVFormat format = CSVFormat.DEFAULT.builder()
                        .setDelimiter(';')
                        .setHeader() // Use first record as header
                        .setSkipHeaderRecord(true)
                        .build();

                var parser = format.parse(reader);
                var headers = parser.getHeaderNames();

                if (!headers.contains(languageColumn)) {
                    LOGGER.severe("Language column '" + languageColumn + "' not found in CSV");
                    throw new RuntimeException("Language column not found in CSV");
                }

                for (CSVRecord record : parser) {
                    if (record.size() < 2) {
                        LOGGER.warning("Skipping malformed record: " + record);
                        continue;
                    }
                    String id = record.get(0); // First column is always ID
                    String text = record.get(languageColumn);
                    textData.put(id, text);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch text layers", e);
            throw new RuntimeException("Failed to fetch text layers", e);
        }

        return textData;
    }

    public static String getText(String id) {
        Map<String, String> textData = fetchAll();
        return textData.getOrDefault(id, "Text not found");
    }
}