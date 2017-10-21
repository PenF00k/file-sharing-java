package ru.penf00k.filesharing.server;

public interface ServerListener {

    void onServerLog(FileExchangerServer fileExchangerServer, String msg);
}
