package ru.penf00k.filesharing.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketThread extends Thread {

    private final SocketThreadListener listener;
    private final Socket socket;
    private DataOutputStream out;


    public SocketThread(String name, SocketThreadListener listener, Socket socket) {
        super(name);
        this.listener = listener;
        this.socket = socket;
        start();
    }

    @Override
    public void run() {
        listener.onStartSocketThread(this);
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            listener.onReadySocketThread(this, socket);
            while (!isInterrupted()) {
                String msg = in.readUTF();
                listener.onReceiveString(this, socket, msg);
            }
        } catch (IOException e) {
            listener.onExceptionSocketThread(this, socket, e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                listener.onExceptionSocketThread(this, socket, e);
            }
            listener.onStopSocketThread(this);
        }
    }

    public synchronized void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            listener.onExceptionSocketThread(this, socket, e);
            close();
        }
    }

    public synchronized void close() {
        interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            listener.onExceptionSocketThread(this, socket, e);
        }
    }
}
