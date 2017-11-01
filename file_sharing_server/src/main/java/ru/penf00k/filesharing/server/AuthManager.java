package ru.penf00k.filesharing.server;

public interface AuthManager {

    void init();
    void addNewUser(String username, String password);
    void dispose();
}
