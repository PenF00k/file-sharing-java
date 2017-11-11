package ru.penf00k.filesharing.common;

public class AuthMessage extends AbstractMessage {

    private String username;
    private String password;

    public AuthMessage(String username, String password) {
        setType(Messages.AUTH);
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
