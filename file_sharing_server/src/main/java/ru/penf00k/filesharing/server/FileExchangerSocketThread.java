package ru.penf00k.filesharing.server;

import ru.penf00k.filesharing.network.SocketThread;
import ru.penf00k.filesharing.network.SocketThreadListener;

import java.net.Socket;

public class FileExchangerSocketThread extends SocketThread {

    private boolean isAuthorized;
    private String login;


    public FileExchangerSocketThread(String name, SocketThreadListener listener, Socket socket) {
        super(name, listener, socket);
    }

    void setAuthorized(String login) {
        isAuthorized = true;
        this.login = login;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public String getLogin() {
        return login;
    }


}
