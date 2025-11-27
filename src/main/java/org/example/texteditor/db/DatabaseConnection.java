package org.example.texteditor.db;

import java.sql.*;


public class DatabaseConnection {
    private static final String LOCAL_URL = "jdbc:sqlserver://localhost:1433;databaseName=text_editor;encrypt=false;";
    private static final String LOCAL_USER = "Cucumber";
    private static final String LOCAL_PASS = "1657udte";

    private Connection connection;

    public DatabaseConnection() {
        connect();
    }

    private void connect() {
        try {
            String pgHost = System.getenv("PGHOST");

            if (pgHost != null) {
                System.out.println("☁️ Виявлено Railway! Підключаємось до PostgreSQL...");
                String dbUrl = "jdbc:postgresql://" + pgHost + ":" + System.getenv("PGPORT") + "/" + System.getenv("PGDATABASE");
                String dbUser = System.getenv("PGUSER");
                String dbPass = System.getenv("PGPASSWORD");
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);

            } else {
                System.out.println("🏠 Локальний запуск! Підключаємось до MSSQL...");

                connection = DriverManager.getConnection(LOCAL_URL, LOCAL_USER, LOCAL_PASS);
            }

            System.out.println("✅ Успішне підключення до БД!");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Не знайдено драйвер БД: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Помилка SQL підключення: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
