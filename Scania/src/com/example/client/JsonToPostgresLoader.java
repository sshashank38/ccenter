package com.example.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.sql.*;
import java.util.*;

public class JsonToPostgresLoader {
   
    private static final String DB_URL = ConfigUtil.get("db.url");
    private static final String USER = ConfigUtil.get("db.user");
    private static final String PASS = ConfigUtil.get("db.pass");


    public static Response loadJsonToDatabase(String fileKey) {
        Response response = new Response();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            File jsonFile = new File("C:/temp/TestCompany/output/" + fileKey + ".json");
            if (!jsonFile.exists()) {
                response.setError("JSON file not found.");
                return response;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonFile);

            for (JsonNode pkg : root.withArray("packages")) {
                insertWithAttributes(conn, "packages", pkg);
            }
            for (JsonNode part : root.withArray("parts")) {
                insertWithAttributes(conn, "parts", part);
            }
            for (JsonNode doc : root.withArray("documents")) {
                insertWithAttributes(conn, "documents", doc);
            }

            LoggerUtil.info("Inserted data from JSON to DB successfully.");
            response.setData("Inserted JSON data to DB successfully.");
        } catch (Exception e) {
            LoggerUtil.error("Error loading JSON to DB: " + e.getMessage());
            response.setError("DB Error: " + e.getMessage());
        }
        return response;
    }

    private static void insertWithAttributes(Connection conn, String table, JsonNode node) throws SQLException {
        Map<String, String> fields = new LinkedHashMap<>();

        // Add direct fields
        Iterator<Map.Entry<String, JsonNode>> it = node.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = it.next();
            if (!"attributes".equals(entry.getKey())) {
                fields.put(entry.getKey(), entry.getValue().asText());
            }
        }

        // Add attributes
        JsonNode attrNode = node.get("attributes");
        if (attrNode != null && attrNode.isObject()) {
            attrNode.fields().forEachRemaining(entry -> fields.put(entry.getKey().replaceAll("[^a-zA-Z0-9_]", "_"), entry.getValue().asText()));
        }

        ensureColumns(conn, table, fields);
        buildAndExecuteInsert(conn, table, fields);
    }

    private static void ensureColumns(Connection conn, String table, Map<String, String> fields) throws SQLException {
        for (String col : fields.keySet()) {
            String checkSQL = "SELECT column_name FROM information_schema.columns WHERE table_name = ? AND column_name = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSQL)) {
                ps.setString(1, table);
                ps.setString(2, col);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    String alterSQL = "ALTER TABLE " + table + " ADD COLUMN \"" + col + "\" TEXT";
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate(alterSQL);
                    }
                }
            }
        }
    }

    private static void buildAndExecuteInsert(Connection conn, String table, Map<String, String> fields) throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO " + table + " (");
        StringBuilder placeholders = new StringBuilder("VALUES (");
        List<String> values = new ArrayList<>();

        for (String col : fields.keySet()) {
            sql.append("\"").append(col).append("\",");
            placeholders.append("?,");
            values.add(fields.get(col));
        }

        sql.setLength(sql.length() - 1);
        placeholders.setLength(placeholders.length() - 1);
        sql.append(") ").append(placeholders).append(")");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                ps.setString(i + 1, values.get(i));
            }
            ps.executeUpdate();
        }
    }
}
