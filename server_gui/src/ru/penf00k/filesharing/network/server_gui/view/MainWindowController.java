package ru.penf00k.filesharing.network.server_gui.view;

import javafx.fxml.FXML;
import ru.penf00k.filesharing.server.FileExchangerServer;

public class MainWindowController {

    private FileExchangerServer fileExchangerServer;

    public MainWindowController() {
    }

    @FXML
    private void initialize() {
        System.out.println("ServerGUI controller initialize()"); //TODO
    }

    @FXML
    private void startServer() {
        System.out.println("startServer()"); //TODO
        fileExchangerServer.startListening(9000);
    }

    @FXML
    private void stopServer() {
        System.out.println("stopServer()"); //TODO
        fileExchangerServer.stopListening();
    }

    public void setFileExchangerServer(FileExchangerServer fileExchangerServer) {
        this.fileExchangerServer = fileExchangerServer;
    }
}
