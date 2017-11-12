package ru.penf00k.filesharing.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerSocketThread extends Thread {

    private final int port;
    private final int timeout;
    private final ServerSocketThreadListener listener;

    public ServerSocketThread(String name , int port, int timeout, ServerSocketThreadListener listener) {
        super(name);
        this.port = port;
        this.timeout = timeout;
        this.listener = listener;
        start();
    }

    @Override
    public void run() {
        listener.onStartServerSocketThread(this);
        while (!isInterrupted()) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
//            try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName("192.168.0.150"))){
//                System.out.println(serverSocket.getInetAddress());
                serverSocket.setSoTimeout(timeout);
                listener.onReadyServerSocketThread(this, serverSocket);
                while (!isInterrupted()) {
                    Socket socket;
                    try {
                        socket = serverSocket.accept();
                    } catch (SocketTimeoutException e) {
                        listener.onAcceptTimeout(this, serverSocket);
                        continue;
                    }
                    listener.onAcceptedSocket(this, serverSocket, socket);
                }
            } catch (IOException e) {
                listener.onExceptionServerSocketThread(this, e);
            } finally {
                listener.onStopServerSocketThread(this);
            }
        }
    }
}
