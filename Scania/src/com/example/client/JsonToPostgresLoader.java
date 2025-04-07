package com.example.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class JsonToPostgresLoader {
	public static void main(String[] args) throws Exception {
		Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ccenter", "postgres", "1234");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(new File("C:\\temp\\TestCompany\\output\\p125.json"));

        for (JsonNode packageNode : root.get("packages")) {
            Map<String, String> baseFields = new HashMap<>();
            baseFields.put("package_id", getTextOrNull(packageNode, "id"));
            baseFields.put("state", getTextOrNull(packageNode, "state"));
            baseFields.put("type", getTextOrNull(packageNode, "type"));
            baseFields.put("subtype", getTextOrNull(packageNode, "subtype"));

            Map<String, String> attributes = jsonNodeToMap(packageNode.get("attributes"));
            insertWithAttributes(conn, "packages", baseFields, attributes);
        }

        for (JsonNode partNode : root.get("parts")) {
            Map<String, String> baseFields = new HashMap<>();
            baseFields.put("part_id", getTextOrNull(partNode, "id"));
            baseFields.put("package_id", getTextOrNull(partNode, "packageId"));
            baseFields.put("name", getTextOrNull(partNode, "name"));
            baseFields.put("state", getTextOrNull(partNode, "state"));
            baseFields.put("partNumber", getTextOrNull(partNode, "partNumber"));
            baseFields.put("subtype", getTextOrNull(partNode, "subtype"));

            Map<String, String> attributes = jsonNodeToMap(partNode.get("attributes"));
            insertWithAttributes(conn, "parts", baseFields, attributes);
        }

        for (JsonNode docNode : root.get("documents")) {
            Map<String, String> baseFields = new HashMap<>();
            baseFields.put("document_id", getTextOrNull(docNode, "id"));
            baseFields.put("package_id", getTextOrNull(docNode, "packageId"));
            baseFields.put("title", getTextOrNull(docNode, "title"));
            baseFields.put("type", getTextOrNull(docNode, "type"));
            baseFields.put("subtype", getTextOrNull(docNode, "subtype"));

            Map<String, String> attributes = jsonNodeToMap(docNode.get("attributes"));
            insertWithAttributes(conn, "documents", baseFields, attributes);
        }

        conn.close();
    }

    private static String getTextOrNull(JsonNode node, String key) {
        JsonNode valueNode = node.get(key);
        return valueNode != null && !valueNode.isNull() ? valueNode.asText() : null;
    }

    private static Map<String, String> jsonNodeToMap(JsonNode node) {
        Map<String, String> map = new HashMap<>();
        if (node != null) {
            node.fields().forEachRemaining(entry -> {
                JsonNode value = entry.getValue();
                map.put(entry.getKey(), value.isNull() ? null : value.asText());
            });
        }
        return map;
    }

    private static void insertWithAttributes(Connection conn, String tableName, Map<String, String> baseFields, Map<String, String> attributes) throws SQLException {
        Map<String, String> allFields = new LinkedHashMap<>();
        allFields.putAll(baseFields);
        allFields.putAll(attributes);

        for (String column : allFields.keySet()) {
            ensureColumnExists(conn, tableName, column);
        }

        String columns = allFields.keySet().stream()
            .map(col -> "\"" + col + "\"")
            .collect(Collectors.joining(", "));
        String placeholders = String.join(", ", Collections.nCopies(allFields.size(), "?"));

        String conflictKey;
        if ("packages".equals(tableName)) {
            conflictKey = "package_id";
        } else if ("parts".equals(tableName)) {
            conflictKey = "part_id";
        } else if ("documents".equals(tableName)) {
            conflictKey = "document_id";
        } else {
            throw new IllegalArgumentException("Unknown table: " + tableName);
        }

        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ") " +
                     "ON CONFLICT (\"" + conflictKey + "\") DO NOTHING";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int i = 1;
            for (String value : allFields.values()) {
                stmt.setString(i++, value);
            }
            stmt.executeUpdate();
        }
    }

    private static void ensureColumnExists(Connection conn, String tableName, String columnName) {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT column_name FROM information_schema.columns WHERE table_name='" + tableName + "' AND column_name='" + columnName + "'");
            if (!rs.next()) {
                String alterSql = "ALTER TABLE " + tableName + " ADD COLUMN \"" + columnName + "\" TEXT";
                stmt.executeUpdate(alterSql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}