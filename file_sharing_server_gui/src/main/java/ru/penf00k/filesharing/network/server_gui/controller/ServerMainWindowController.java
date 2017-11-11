package ru.penf00k.filesharing.network.server_gui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import ru.penf00k.filesharing.server.FileExchangerServer;
import ru.penf00k.filesharing.server.SQLAuthManager;
import ru.penf00k.filesharing.server.ServerListener;

public class ServerMainWindowController implements ServerListener {

    private final FileExchangerServer fileExchangerServer = new FileExchangerServer(this, new SQLAuthManager());

    @FXML
    private Button btnStartServer;
    @FXML
    private Button btnStopServer;
    @FXML
    private TextArea taLogs;

    public ServerMainWindowController() {
    }

    @FXML
    private void initialize() {
        System.out.println("ServerGUI controller initialize()"); //TODO
        Platform.runLater(() -> {
            taLogs.clear();
            startServer();
        });
    }

    @FXML
    private void startServer() {
        System.out.println("startServer()"); //TODO
        fileExchangerServer.startListening(9000);
        setUIRunning(true);
    }

    @FXML
    private void stopServer() {
        System.out.println("stopServer()"); //TODO
        fileExchangerServer.stopListening();
        setUIRunning(false);
    }

    private void setUIRunning(boolean isRunning) {
        btnStartServer.setDisable(isRunning);
        btnStopServer.setDisable(!isRunning);
    }

    @Override
    public void onServerLog(FileExchangerServer fileExchangerServer, String msg) {
        //TODO куда писать логи
//        System.out.println(msg);
        Platform.runLater(() -> taLogs.appendText(String.format("%s\n", msg)));
    }
}
