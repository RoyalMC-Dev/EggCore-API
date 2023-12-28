package me.verfiedfemboy.velocitycore.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtils {


    public static boolean doesTableExist(Connection conn, String tableName) {
        boolean tableExists = false;
        try (Statement stmt = conn.createStatement()) {

            String checkTableQuery = "SHOW TABLES LIKE '" + tableName + "'";
            if (stmt.executeQuery(checkTableQuery).next()) {
                tableExists = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tableExists;
    }

    public static void createTable(Connection conn, String tableName, String... columns) {
        StringBuilder columnsStringBuilder = new StringBuilder();
        for (String column : columns) {
            columnsStringBuilder.append(column).append(", ");
        }
        String columnsString = columnsStringBuilder.toString();
        //Deleting spaces and other shits
        columnsString = columnsString.substring(0, columnsString.length() - 2);

        String createTableQuery = "CREATE TABLE " + tableName +
                " (" + columnsString + ") " +
                "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        try (Statement stmt = conn.createStatement()) {
            // Executing updates
            stmt.executeUpdate(createTableQuery);
            LoggerUtils.log(LoggerUtils.DEBUG, "Tabela została utworzona");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertInto(Connection conn, String tableName, String[] columns, String[] values) {
        if (columns.length != values.length) {
            throw new IllegalArgumentException("Liczba kolumn musi być równa liczbie wartości.");
        }

        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
        for (int i = 0; i < columns.length; i++) {
            query.append(columns[i]);
            if (i != columns.length - 1) {
                query.append(", ");
            }
        }
        query.append(") VALUES (");
        for (int i = 0; i < values.length; i++) {
            query.append("?");
            if (i != values.length - 1) {
                query.append(", ");
            }
        }
        query.append(")");

        try (PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setString(i + 1, values[i]);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateRow(Connection conn, String tableName, String[] columns, String[] values, String conditionColumn, String conditionValue) throws SQLException {
        if (columns.length != values.length) {
            throw new IllegalArgumentException("Liczba kolumn musi być równa liczbie wartości.");
        }

        StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
        for (int i = 0; i < columns.length; i++) {
            query.append(columns[i]).append(" = ?");
            if (i != columns.length - 1) {
                query.append(", ");
            }
        }
        query.append(" WHERE ").append(conditionColumn).append(" = ?");

        try (PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            int parameterIndex = 1;
            for (int i = 0; i < values.length; i++) {
                pstmt.setString(parameterIndex++, values[i]);
            }
            pstmt.setString(parameterIndex, conditionValue);

            pstmt.executeUpdate();
        }
    }
}
