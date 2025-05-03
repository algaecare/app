package com.algaecare.model;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextLayerData {
    private static final Logger LOGGER = Logger.getLogger(TextLayerData.class.getName());
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/data.sqlite";
    private static Language defaultLanguage;

    private enum Language {
        DE, FR, IT
    }

    static {
        loadDefaultLanguage();
    }

    private static void loadDefaultLanguage() {
        String sql = "SELECT value FROM settings WHERE name = 'DEFAULT_LANGUAGE'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                defaultLanguage = Language.valueOf(rs.getString("value"));
            }
        } catch (SQLException | IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Failed to load default language, using DE", e);
            defaultLanguage = Language.DE;
        }
    }

    public static Map<String, String> fetchAll() {
        Map<String, String> textData = new HashMap<>();
        String languageColumn = defaultLanguage.name().toLowerCase();
        String sql = "SELECT id, " + languageColumn + " FROM text_layers";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String text = rs.getString(languageColumn);
                textData.put(id, text);
            }
        } catch (SQLException e) {
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