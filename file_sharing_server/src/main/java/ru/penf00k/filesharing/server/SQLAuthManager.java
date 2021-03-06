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
            PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUser(String username, String password) {
        try {
            PreparedStatement ps = connection
                    .prepareStatement("SELECT username FROM users WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
