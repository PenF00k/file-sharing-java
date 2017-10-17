package ru.penf00k.filesharing.network;

import ru.penf00k.filesharing.common.AbstractMessage;
import ru.penf00k.filesharing.common.FileMessage;
import ru.penf00k.filesharing.common.TextMessage;

import java.io.*;
import java.net.Socket;

public class SocketThread extends Thread {

    private final SocketThreadListener listener;
    private final Socket socket;
    private Socket pairedSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private final byte[] buffer = new byte[8192]; // TODO delete

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
                    if (message instanceof FileMessage) {
                        FileMessage fm = (FileMessage) message;
                        byte[] fileBytes = new byte[fm.getLength()];
                        ois.read(fileBytes, 0, fm.getLength());
                        listener.onReceiveFile(this, socket, fileBytes);
                    } else if (message instanceof TextMessage) {
                        listener.onReceiveString(this, socket, ((TextMessage) message).getText());
                    }
                } catch (OptionalDataException e) {
                    System.out.println("EXCEPTION!!");
                    e.printStackTrace();
                    System.out.println("object: " + ois.readObject());
                }
            }
        } catch (IOException e) {
            listener.onExceptionSocketThread(this, socket, e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            listener.onStopSocketThread(this);
            close();
        }
    }

//    private byte[] getFileBytes(FileMessage fm) throws IOException {
//        byte[] fileBytes = new byte[fm.getLength()];
//        ois.read(fileBytes, 0, fm.getLength());
//        return fileBytes;
//    }

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
        try {
            byte[] fileBytes = new byte[(int) file.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
//            int bytesSent;
//            while ((bytesSent = ))
            bis.read(fileBytes, 0, fileBytes.length);
            oos.write(fileBytes, 0, fileBytes.length);
            oos.flush();
            bis.close();
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
//            dis.close();
//            dos.close();
            socket.close();
        } catch (IOException e) {
            listener.onExceptionSocketThread(this, socket, e);
        }
    }
}
