package ru.penf00k.filesharing.server;

import ru.penf00k.filesharing.common.AbstractMessage;
import ru.penf00k.filesharing.common.FileMessage;
import ru.penf00k.filesharing.common.TextMessage;
import ru.penf00k.filesharing.network.ServerSocketThread;
import ru.penf00k.filesharing.network.ServerSocketThreadListener;
import ru.penf00k.filesharing.network.SocketThreadListener;
import ru.penf00k.filesharing.network.SocketThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class FileExchangerServer implements ServerSocketThreadListener, SocketThreadListener {

    private final ServerListener listener;
    private final AuthManager authManager;
    private ServerSocketThread serverSocketThread;
    private final Vector<SocketThread> clients = new Vector<>();
    private FileExchangerSocketThread client;

    public FileExchangerServer(ServerListener listener, AuthManager authManager) {
        this.listener = listener;
        this.authManager = authManager;
    }

    public void startListening(int port) {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            putLog("FileExchangerServer is already running");
            return;
        }
        serverSocketThread = new ServerSocketThread("ServerSocketThread", port, 2000, this);
        authManager.init();
    }

    private synchronized void putLog(String msg) {
        listener.onServerLog(this, msg); // TODO добавить время лога
    }

    public void stopListening() {
        if (serverSocketThread == null || !serverSocketThread.isAlive()) {
            putLog("FileExchangerServer was not running");
            return;
        }
        serverSocketThread.interrupt();
        authManager.dispose();
    }

    // ServerSocketThreadListener
    @Override
    public void onStartServerSocketThread(ServerSocketThread thread) {
        putLog("FileExchangerServer started");
    }

    @Override
    public void onStopServerSocketThread(ServerSocketThread thread) {
        putLog("FileExchangerServer stopped");
    }

    @Override
    public void onReadyServerSocketThread(ServerSocketThread thread, ServerSocket serverSocket) {
        putLog("FileExchangerServer socket is ready");
    }

    @Override
    public void onAcceptTimeout(ServerSocketThread thread, ServerSocket serverSocket) {
        putLog("FileExchangerServer accept() timed out");
    }

    @Override
    public void onAcceptedSocket(ServerSocketThread thread, ServerSocket serverSocket, Socket socket) {
        putLog("Client connected");
        String threadName = "Socket thread: " + socket.getInetAddress() + ":" + socket.getPort();
        new FileExchangerSocketThread(threadName, this, socket);
    }

    @Override
    public void onExceptionServerSocketThread(ServerSocketThread thread, Exception e) {
        putLog("Exception: " + e.getClass() + ": " + e.getMessage());
    }

    // SocketThreadListener
    @Override
    public synchronized void onStartSocketThread(SocketThread socketThread) {

    }

    @Override
    public synchronized void onStopSocketThread(SocketThread socketThread) {
        clients.remove(socketThread);
        putLog("Client disconnected");
        // TODO
    }

    @Override
    public synchronized void onReadySocketThread(SocketThread socketThread, Socket socket) {
        clients.add(socketThread);
    }

    @Override
    public synchronized void onReceiveString(SocketThread socketThread, Socket socket, String value) {
        client = (FileExchangerSocketThread) socketThread;
        System.out.println("FileExchangerServer received string: " + value);
        if (client.isAuthorized()) {
            // TODO
        }
    }

    private FileMessage fm;

    @Override
    public synchronized void onReceiveObjectMessage(SocketThread socketThread, Socket socket, AbstractMessage message) {
        if (message instanceof FileMessage) {
            fm = (FileMessage) message;
            TextMessage textMessage = new TextMessage("type = " + fm.getType() + ", file name = " + fm.getFile().getName());
            socketThread.sendMessageObject(textMessage);
        }
    }

    @Override
    public synchronized void onReceiveFile(SocketThread socketThread, Socket socket, ObjectInputStream ois) {
        //TODO
        File file = new File("C:\\Users\\curly\\Desktop\\files2share\\" + fm.getFile().getName());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            int bytesRead;
            int totalBytes = 0;
            byte[] buffer = new byte[8192];
            while (totalBytes < fm.getLength() && (bytesRead = ois.read(buffer)) != -1) { //TODO задать вопрос: если поменять местами условие, то цикл бесконечный
                fos.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }
            System.out.println("File saves fine");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        e.printStackTrace();
    }
}
