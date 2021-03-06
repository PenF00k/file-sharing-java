package ru.penf00k.filesharing.network;

import ru.penf00k.filesharing.common.AbstractMessage;
import ru.penf00k.filesharing.common.FileMessage;

import java.io.ObjectInputStream;
import java.net.Socket;

public interface SocketThreadListener {

    void onStartSocketThread(SocketThread socketThread);
    void onStopSocketThread(SocketThread socketThread, Socket socket);

    void onReadySocketThread(SocketThread socketThread, Socket socket);
    void onReceiveString(SocketThread socketThread, Socket socket, String value);
    void onReceiveObjectMessage(SocketThread socketThread, Socket socket, AbstractMessage message);
    void onReceiveFile(SocketThread socketThread, Socket socket, FileMessage fm, ObjectInputStream ois);

    void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e);
}
