package ru.penf00k.filesharing.server;

import javafx.application.Platform;
import ru.penf00k.filesharing.common.*;
import ru.penf00k.filesharing.network.ServerSocketThread;
import ru.penf00k.filesharing.network.ServerSocketThreadListener;
import ru.penf00k.filesharing.network.SocketThreadListener;
import ru.penf00k.filesharing.network.SocketThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class FileExchangerServer implements ServerSocketThreadListener, SocketThreadListener {

    private static final String FILES_PATH = "C:\\Users\\curly\\Desktop\\files2share";

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
            putLog("FileExchangerServer is not running");
            return;
        }
        ServerMessage serverMessage = new ServerMessage(Response.SERVER_STOP);
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).sendMessageObject(serverMessage);
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
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).close();
        }
    }

    @Override
    public void onReadyServerSocketThread(ServerSocketThread thread, ServerSocket serverSocket) {
        putLog("FileExchangerServer socket is ready");
    }

    @Override
    public void onAcceptTimeout(ServerSocketThread thread, ServerSocket serverSocket) {
//        putLog("FileExchangerServer accept() timed out");
    }

    @Override
    public void onAcceptedSocket(ServerSocketThread thread, ServerSocket serverSocket, Socket socket) {
//        putLog(String.format("Client %s connected", client.getUsername()));
        putLog(String.format("Client %s connected", socket.getInetAddress().getHostAddress()));
//        putLog("Client connected");
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
    public synchronized void onStopSocketThread(SocketThread socketThread, Socket socket) {
        clients.remove(socketThread);
        putLog(String.format("Client %s disconnected. Client's username: %s",
                socket.getInetAddress().getHostAddress(),
                ((FileExchangerSocketThread)socketThread).getUsername()
        ));
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
        client = (FileExchangerSocketThread) socketThread;

        if (message instanceof AuthMessage) {
            authorise(client, socket, message);
            return;
        }

        if (!client.isAuthorized() && !authorise(client, socket, message)) {
            if (message instanceof RegisterMessage) {
                RegisterMessage rm = (RegisterMessage) message;
                String username = rm.getUsername();
                authManager.addNewUser(username, rm.getPassword());
                client.setAuthorized(username);
                putLog(String.format("Registered user: %s",username));
//                System.out.println("Registered user: " + username);
            } else {
                client.sendMessageObject(new ServerMessage(Response.NEED_AUTHORIZATION));
            }
            return;
        }

        if (message instanceof FileMessage) {
            fm = (FileMessage) message;
            TextMessage textMessage = new TextMessage("type = " + fm.getType() + ", file name = " + fm.getFile().getName());
            socketThread.sendMessageObject(textMessage);
            return;
        }
    }

    File userDir;

    private boolean authorise(FileExchangerSocketThread client, Socket socket, AbstractMessage message) {
        if (!(message instanceof AuthMessage)) return false;
        AuthMessage am = (AuthMessage) message;
        String username = am.getUsername();
        if (authManager.getUser(username, am.getPassword()) != null) {
            client.setAuthorized(username);
            userDir = new File(String.format("%s\\%s", FILES_PATH, client.getUsername()));
            if (!userDir.exists()) userDir.mkdirs();
            client.sendMessageObject(new ServerMessage(Response.AUTHORIZATION_OK, username));
            putLog(String.format("Client %s was successfully authorised as %s", socket.getInetAddress().getHostAddress(), username));
            return true;
        } else client.sendMessageObject(new ServerMessage(Response.AUTHORIZATION_ERROR));
        return false;
    }

    @Override
    public synchronized void onReceiveFile(SocketThread socketThread, Socket socket, ObjectInputStream ois) {
        File file = new File(String.format("%s\\%s", userDir, fm.getFile().getName()));
        try (FileOutputStream fos = new FileOutputStream(file)) {
            int bytesRead;
            int totalBytes = 0;
            byte[] buffer = new byte[8192];
            while (totalBytes < fm.getLength() && (bytesRead = ois.read(buffer)) != -1) { //TODO задать вопрос: если поменять местами условие, то цикл бесконечный
                fos.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }
            System.out.println("File saved fine");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        e.printStackTrace();
    }
}
