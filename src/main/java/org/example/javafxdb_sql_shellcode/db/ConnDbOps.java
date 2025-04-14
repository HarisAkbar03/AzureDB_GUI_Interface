package org.example.javafxdb_sql_shellcode.db;

import java.sql.*;

public class ConnDbOps {

    final String MYSQL_SERVER_URL = "jdbc:mysql://csc311haris.mysql.database.azure.com/";
    public final String DB_URL = MYSQL_SERVER_URL + "AzureShellJava";
    public final String USERNAME = "haris";
    public static final String PASSWORD = "Password1";

    // Connect to the database and create the necessary tables and database if they don't exist
    public boolean connectToDatabase() {
        boolean hasRegisteredUsers = false;

        try {
            // Connect to MySQL server and create the database if it doesn't exist
            Connection conn = DriverManager.getConnection(MYSQL_SERVER_URL, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS AzureShellJava");
            statement.close();
            conn.close();

            // Connect to the database and create the "users" table if it doesn't exist
            conn = DriverManager.getConnection(DB_URL, USERNAME, "Password1");
            statement = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT(10) NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                    + "first_name VARCHAR(200) NOT NULL,"
                    + "last_name VARCHAR(200) NOT NULL,"
                    + "department VARCHAR(200),"
                    + "major VARCHAR(200)"
                    + ")";
            statement.executeUpdate(sql);

            // Check if there are registered users in the table
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");
            if (resultSet.next()) {
                hasRegisteredUsers = resultSet.getInt(1) > 0;
            }

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hasRegisteredUsers;
    }

    // Insert a new user into the database
    public boolean insertUser(String firstName, String lastName, String department, String major) {
        boolean success = false;
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, "Password1")) {
            String sql = "INSERT INTO users (first_name, last_name, department, major) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setString(3, department);
                statement.setString(4, major);
                int rowsInserted = statement.executeUpdate();
                success = rowsInserted > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    // Query a user by their first name
    public boolean queryUserByName(String firstName) {
        boolean found = false;
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, "Password1")) {
            String sql = "SELECT * FROM users WHERE first_name = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, firstName);
                ResultSet resultSet = statement.executeQuery();
                found = resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return found;
    }

    // List all users from the database
    public ResultSet listAllUsers() {
        ResultSet resultSet = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, "Password1")) {
            Statement statement = conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM users");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
}
