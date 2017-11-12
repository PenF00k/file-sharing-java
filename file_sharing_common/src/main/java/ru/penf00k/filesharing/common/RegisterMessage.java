package ru.penf00k.filesharing.common;

public class RegisterMessage extends AbstractMessage {

    private String username;
    private String password;

    public RegisterMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
