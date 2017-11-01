package ru.penf00k.filesharing.server;

import java.sql.*;

public class SQLAuthManager implements AuthManager {

    private Connection connection;
    private Statement statement;

    @Override
    public void init() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:file_sharing_db.sqlite");
            statement = connection.createStatement();
            createUsersTable();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dispose() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createUsersTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users " +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " username TEXT UNIQUE NOT NULL," +
                " password TEXT NOT NULL)";
        statement.executeUpdate(sql);
    }

    @Override
    public void addNewUser(String username, String password) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
