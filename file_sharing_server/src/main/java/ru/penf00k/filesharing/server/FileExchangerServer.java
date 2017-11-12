package ru.penf00k.filesharing.server;

import ru.penf00k.filesharing.common.*;
import ru.penf00k.filesharing.network.ServerSocketThread;
import ru.penf00k.filesharing.network.ServerSocketThreadListener;
import ru.penf00k.filesharing.network.SocketThread;
import ru.penf00k.filesharing.network.SocketThreadListener;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

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
                String password = rm.getPassword();
                authManager.addNewUser(username, password);
                authorise(client, socket, new AuthMessage(username, password));
                putLog(String.format("Registered user: %s",username));
            } else {
                client.sendMessageObject(new ServerMessage(Response.NEED_AUTHORIZATION));
            }
            return;
        }

        if (message instanceof FileMessage) {
            fm = (FileMessage) message;
            TextMessage tm = new TextMessage("file name = " + fm.getFile().getName());
            socketThread.sendMessageObject(tm);
            return;
        }

        if (message instanceof RequestMessage) {
            RequestMessage rm = (RequestMessage) message;
            File file = new File(userDir, rm.getFile().getName());
            Request request = rm.getRequest();
            switch (request) {
                case GET_FILES_LIST:
                    //TODO
                    break;
                case DELETE_FILE:
                    if (!file.delete())
                        socketThread.sendMessageObject(new ServerMessage(Response.ERROR_DELETE_FILE));
                    break;
                case RENAME_FILE:
                    if (!file.renameTo(new File(file.getParent(), rm.getNewFileName())))
                        socketThread.sendMessageObject(new ServerMessage(Response.ERROR_RENAME_FILE));
                    break;
            }
            sendFilesList(socketThread);
        }
    }

    private void sendFilesList(SocketThread socketThread) {
        try {
            userDir = new File(String.format("%s\\%s", FILES_PATH, client.getUsername()));
            if (!userDir.exists()) userDir.mkdirs();
            List<File> files = Files.list(userDir.toPath())
                    .map(path -> new File(path.toUri()))
                    .collect(Collectors.toList());
            socketThread.sendMessageObject(new FileListMessage(files));
//                        socketThread.sendMessageObject(
//                                new FileListMessage(
//                                        Files.newDirectoryStream(Paths.get(userDir.toURI()))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    File userDir;

    private boolean authorise(FileExchangerSocketThread client, Socket socket, AbstractMessage message) {
        if (!(message instanceof AuthMessage)) return false;
        AuthMessage am = (AuthMessage) message;
        String username = am.getUsername();
        if (authManager.getUser(username, am.getPassword()) != null) {
            client.setAuthorized(username);
            client.sendMessageObject(new ServerMessage(Response.AUTHORIZATION_OK, username));
            sendFilesList(client);
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
            sendFilesList(socketThread);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        e.printStackTrace();
    }
}
