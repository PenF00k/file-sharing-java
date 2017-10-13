package ru.penf00k.filesharing.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLAuthManager implements AuthManager {

    private Connection connection;
//    private Statement statement;

    @Override
    public void init() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:chat_db.sqlite");
//            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dispose() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
