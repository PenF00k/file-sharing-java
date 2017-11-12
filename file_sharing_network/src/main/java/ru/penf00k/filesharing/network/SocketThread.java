package ru.penf00k.filesharing.network;

import ru.penf00k.filesharing.common.*;

import java.io.*;
import java.net.Socket;

public class SocketThread extends Thread {

    private final SocketThreadListener listener;
    private final Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

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
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            listener.onReadySocketThread(this, socket);
            while (!isInterrupted()) {
                try {
                    Object message = ois.readObject();
                    listener.onReceiveObjectMessage(this, socket, (AbstractMessage) message);

                    //TODO убрать лишние ифы
                    if (message instanceof FileMessage) {
                        System.out.println("Message is instance of FileMessage");
                        listener.onReceiveFile(this, socket, ois);
                    } else if (message instanceof TextMessage) {
                        System.out.println("Message is instance of TextMessage");
                        listener.onReceiveString(this, socket, ((TextMessage) message).getText());
                    } else if (message instanceof RegisterMessage) {
                        System.out.println("Message is instance of RegisterMessage");
                    } else if (message instanceof AuthMessage) {
                        System.out.println("Message is instance of AuthMessage");
                    }  else if (message instanceof ServerMessage) {
                        System.out.println("Message is instance of ServerMessage");
                    }  else if (message instanceof RequestMessage) {
                        System.out.println("Message is instance of RequestMessage");
                    }  else if (message instanceof FileListMessage) {
                        System.out.println("Message is instance of FileListMessage");
                    } else {
                        throw new RuntimeException("Invalid message type" + message);
                    }
                } catch (OptionalDataException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            listener.onExceptionSocketThread(this, socket, e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            listener.onStopSocketThread(this, socket);
            close();
        }
    }

    public synchronized void sendMessageObject(AbstractMessage msg) {
        try {
            oos.writeObject(msg);
            oos.flush();
        } catch (IOException e) {
            listener.onExceptionSocketThread(this, socket, e);
            close();
        }
    }

    public synchronized void sendFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = fis.read(buffer)) != -1) {
                oos.write(buffer, 0, bytesRead);
            }
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized void close() {
        System.out.println("Socket thread closed");
        interrupt();
        try {
            ois.close();
            oos.close();
            socket.close();
        } catch (IOException e) {
            listener.onExceptionSocketThread(this, socket, e);
        }
    }
}
