package ru.penf00k.filesharing.server;

import ru.penf00k.filesharing.network.SocketThread;
import ru.penf00k.filesharing.network.SocketThreadListener;

import java.net.Socket;

public class FileExchangerSocketThread extends SocketThread {

    private boolean isAuthorized;
    private String username;


    FileExchangerSocketThread(String name, SocketThreadListener listener, Socket socket) {
        super(name, listener, socket);
    }

    void setAuthorized(String username) {
        isAuthorized = true;
        this.username = username;
    }

    boolean isAuthorized() {
        return isAuthorized;
    }

    public String getUsername() {
        return username;
    }
}
